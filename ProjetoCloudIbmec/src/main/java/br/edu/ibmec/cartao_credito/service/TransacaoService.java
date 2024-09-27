package br.edu.ibmec.cartao_credito.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ibmec.cartao_credito.model.Cartao;
import br.edu.ibmec.cartao_credito.model.Transacao;
import br.edu.ibmec.cartao_credito.repository.CartaoRepository;
import br.edu.ibmec.cartao_credito.repository.TransacaoRepository;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private CartaoRepository cartaoRepository;

    private final long TRANSACTION_TIME_INTERVAL = 3;

    public Transacao autorizacaoTransacao(int cartaoId, double valor, String comerciante) throws Exception {
        // Recupera o cartão
        Cartao cartao = cartaoRepository.findById(cartaoId)
                .orElseThrow(() -> new Exception("Cartão não encontrado"));

        // Verifica se o cartão está ativo
        if (!cartao.getAtivo()) {
            throw new Exception("Cartão não está ativo");
        }

        // Verifica se o cartão tem limite suficiente
        if (cartao.getLimite() < valor) {
            throw new Exception("Cartão sem limite para efetuar a compra");
        }

        // Verifica as regras de antifraude
        verificarAntifraude(cartao, valor);

        // Cria e salva a transação
        Transacao transacao = new Transacao();
        transacao.setComerciante(comerciante);
        transacao.setDataTransacao(LocalDateTime.now());
        transacao.setValor(valor);
        transacaoRepository.save(transacao);

        // Atualiza o limite do cartão e salva
        cartao.setLimite(cartao.getLimite() - valor);
        cartao.getTransacoes().add(transacao);
        cartaoRepository.save(cartao);

        return transacao;
    }

    private void verificarAntifraude(Cartao cartao, double valor) throws Exception {
        LocalDateTime localDateTime = LocalDateTime.now().minus(TRANSACTION_TIME_INTERVAL, ChronoUnit.MINUTES);

        List<Transacao> ultimasTransacoes = cartao
                .getTransacoes()
                .stream()
                .filter(x -> x.getDataTransacao().isAfter(localDateTime))
                .toList();

        if (ultimasTransacoes.size() >= 3) {
            throw new Exception("Cartão utilizado muitas vezes em um período curto");
        }
    }
}
