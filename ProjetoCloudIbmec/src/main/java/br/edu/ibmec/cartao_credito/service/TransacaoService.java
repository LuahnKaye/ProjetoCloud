package br.edu.ibmec.cartao_credito.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ibmec.cartao_credito.model.Cartao;
import br.edu.ibmec.cartao_credito.model.Transacao;
import br.edu.ibmec.cartao_credito.repository.CartaoRepository;
import br.edu.ibmec.cartao_credito.repository.TransacaoRepository;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository repository;

    @Autowired
    private CartaoRepository cartaoRepository;

    private final long TRANSACTION_TIME_INTERVAL_MINUTES = 2;  // Intervalo de 2 minutos
    private final int MAX_TRANSACTIONS_IN_INTERVAL = 3;        // No máximo 3 transações no intervalo
    private final int MAX_SIMILAR_TRANSACTIONS = 2;            // No máximo 2 transações semelhantes no intervalo

    public Transacao autorizacaoTransacao(Cartao cartao, double valor, String comerciante) throws Exception {

        // Verificar se o cartão está ativo
        if (!cartao.getAtivo()) {
            throw new Exception("Cartão não está ativo");
        }

        // Verificar se o valor da transação excede o limite disponível
        if (cartao.getLimite() < valor) {
            throw new Exception("Limite insuficiente");
        }

        // Verificar regras antifraude
        this.verificarAntifraude(cartao, valor, comerciante);

        // Criar uma nova transação
        Transacao transacao = new Transacao();
        transacao.setComerciante(comerciante);
        transacao.setDataTransacao(LocalDateTime.now());
        transacao.setValor(valor);

        // Salvar a nova transação
        repository.save(transacao);

        // Atualizar o limite do cartão
        cartao.setLimite(cartao.getLimite() - valor);
        cartao.getTransacoes().add(transacao);
        cartaoRepository.save(cartao);

        return transacao;
    }

    private void verificarAntifraude(Cartao cartao, double valor, String comerciante) throws Exception {

        // Tempo atual menos 2 minutos
        LocalDateTime intervaloLimite = LocalDateTime.now().minus(TRANSACTION_TIME_INTERVAL_MINUTES, ChronoUnit.MINUTES);

        // Transações dentro do intervalo de tempo
        List<Transacao> transacoesRecentes = cartao.getTransacoes().stream()
                .filter(t -> t.getDataTransacao().isAfter(intervaloLimite))
                .collect(Collectors.toList());

        // Verificar se há mais de 3 transações no intervalo de 2 minutos
        if (transacoesRecentes.size() >= MAX_TRANSACTIONS_IN_INTERVAL) {
            throw new Exception("Alta frequência de transações em um curto intervalo");
        }

        // Verificar se há mais de 2 transações semelhantes (mesmo valor e comerciante) no intervalo de 2 minutos
        long transacoesSemelhantes = transacoesRecentes.stream()
                .filter(t -> t.getValor() == valor && t.getComerciante().equals(comerciante))
                .count();

        if (transacoesSemelhantes >= MAX_SIMILAR_TRANSACTIONS) {
            throw new Exception("Transação duplicada");
        }
    }
}
