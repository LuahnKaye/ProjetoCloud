package br.edu.ibmec.cartao_credito.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ibmec.cartao_credito.model.Cartao;
import br.edu.ibmec.cartao_credito.repository.CartaoRepository;

import java.util.Optional;

@Service
public class CartaoService {

    @Autowired
    private CartaoRepository cartaoRepository;

    public void ativarCartao(int id) throws Exception {
        Cartao cartao = buscarCartaoPorId(id);
        if (cartao == null) {
            throw new Exception("Cartão não encontrado");
        }
        cartao.setAtivo(true);
        cartaoRepository.save(cartao);
    }

    public void desativarCartao(int id) throws Exception {
        Cartao cartao = buscarCartaoPorId(id);
        if (cartao == null) {
            throw new Exception("Cartão não encontrado");
        }
        cartao.setAtivo(false);
        cartaoRepository.save(cartao);
    }

     public Cartao buscarCartaoPorId(int id) {
        Optional<Cartao> cartaoOptional = cartaoRepository.findById(id);
        return cartaoOptional.orElse(null);
    }
}
