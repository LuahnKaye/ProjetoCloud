package br.edu.ibmec.cartao_credito.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.ibmec.cartao_credito.exception.TransacaoException;
import br.edu.ibmec.cartao_credito.model.Cartao;
import br.edu.ibmec.cartao_credito.model.Transacao;
import br.edu.ibmec.cartao_credito.service.TransacaoService;
import br.edu.ibmec.cartao_credito.service.CartaoService;

@RestController
@RequestMapping("/transacao")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private CartaoService cartaoService;

    @PostMapping("/autorizar")
    public ResponseEntity<?> autorizarTransacao(
            @RequestParam int cartaoId,
            @RequestParam double valor,
            @RequestParam String comerciante) {
        try {
            Cartao cartao = cartaoService.buscarCartaoPorId(cartaoId);
            if (cartao == null) {
                throw new TransacaoException("Cartão com ID " + cartaoId + " não encontrado.");
            }

            if (!cartao.getAtivo()) {
                throw new TransacaoException("Cartão com ID " + cartaoId + " está inativo.");
            }

            if (cartao.getLimite() < valor) {
                throw new TransacaoException("Limite insuficiente para realizar a transação de valor " + valor);
            }

            Transacao transacao = transacaoService.autorizacaoTransacao(cartao, valor, comerciante);
            return new ResponseEntity<>(transacao, HttpStatus.OK);
        } catch (TransacaoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao autorizar a transação: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarTransacaoPorId(@PathVariable int id) {
        try {
            Transacao transacao = transacaoService.buscarTransacaoPorId(id);
            return new ResponseEntity<>(transacao, HttpStatus.OK);
        } catch (TransacaoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao buscar a transação: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
