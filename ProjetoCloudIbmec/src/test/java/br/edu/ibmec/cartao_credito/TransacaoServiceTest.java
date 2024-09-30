package br.edu.ibmec.cartao_credito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.edu.ibmec.cartao_credito.model.Cartao;
import br.edu.ibmec.cartao_credito.model.Transacao;
import br.edu.ibmec.cartao_credito.repository.CartaoRepository;
import br.edu.ibmec.cartao_credito.repository.TransacaoRepository;
import br.edu.ibmec.cartao_credito.service.TransacaoService;

class TransacaoServiceTest {

    @InjectMocks
    private TransacaoService transacaoService;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private CartaoRepository cartaoRepository;

    private Cartao cartao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartao = new Cartao();
        cartao.setAtivo(true);
        cartao.setLimite(1000);
        cartao.setTransacoes(new ArrayList<>());
    }

    @Test
    void testAutorizacaoTransacao_Sucesso() throws Exception {
        Transacao transacao = transacaoService.autorizacaoTransacao(cartao, 100, "Loja X");

        assertNotNull(transacao);
        assertEquals(900, cartao.getLimite()); // Limite foi atualizado
        verify(transacaoRepository).save(any(Transacao.class));
        verify(cartaoRepository).save(cartao);
    }

    @Test
    void testAutorizacaoTransacao_CartaoInativo() {
        cartao.setAtivo(false);

        Exception exception = assertThrows(Exception.class, () -> {
            transacaoService.autorizacaoTransacao(cartao, 100, "Loja X");
        });

        assertEquals("Cartão não está ativo", exception.getMessage());
    }

    @Test
    void testAutorizacaoTransacao_LimiteInsuficiente() {
        cartao.setLimite(50);

        Exception exception = assertThrows(Exception.class, () -> {
            transacaoService.autorizacaoTransacao(cartao, 100, "Loja X");
        });

        assertEquals("Limite insuficiente", exception.getMessage());
    }

    @Test
    void testAutorizacaoTransacao_AltaFrequencia() throws Exception {
        // Criar 3 transações recentes
        LocalDateTime now = LocalDateTime.now();
        cartao.getTransacoes().add(new Transacao(1, now.minusMinutes(1), 50, "Loja X"));
        cartao.getTransacoes().add(new Transacao(2, now.minusSeconds(30), 50, "Loja X"));
        cartao.getTransacoes().add(new Transacao(3, now.minusSeconds(10), 50, "Loja Y"));

        Exception exception = assertThrows(Exception.class, () -> {
            transacaoService.autorizacaoTransacao(cartao, 100, "Loja Z");
        });

        assertEquals("Alta frequência de transações em um curto intervalo", exception.getMessage());
    }

    @Test
    void testAutorizacaoTransacao_TransacaoDuplicada() throws Exception {
        // Criar 2 transações semelhantes
        LocalDateTime now = LocalDateTime.now();
        cartao.getTransacoes().add(new Transacao(1, now.minusMinutes(1), 50, "Loja X"));
        cartao.getTransacoes().add(new Transacao(2, now.minusSeconds(30), 50, "Loja X"));
        

        Exception exception = assertThrows(Exception.class, () -> {
            transacaoService.autorizacaoTransacao(cartao, 50, "Loja X");
        });

        assertEquals("Transação duplicada", exception.getMessage());
    }
}
