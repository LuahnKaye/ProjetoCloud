package br.edu.ibmec.cartao_credito.controller;

import br.edu.ibmec.cartao_credito.service.CartaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cartao")
public class CartaoController {

    @Autowired
    private CartaoService cartaoService;

    @PostMapping("/{id}/ativar")
    public ResponseEntity<String> ativarCartao(@PathVariable int id) {
        try {
            cartaoService.ativarCartao(id);
            return new ResponseEntity<>("Cartão ativado com sucesso", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/desativar")
    public ResponseEntity<String> desativarCartao(@PathVariable int id) {
        try {
            cartaoService.desativarCartao(id);
            return new ResponseEntity<>("Cartão desativado com sucesso", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
