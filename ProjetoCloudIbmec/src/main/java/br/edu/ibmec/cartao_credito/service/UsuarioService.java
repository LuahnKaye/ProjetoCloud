package br.edu.ibmec.cartao_credito.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ibmec.cartao_credito.exception.UsuarioException;
import br.edu.ibmec.cartao_credito.model.Cartao;
import br.edu.ibmec.cartao_credito.model.Usuario;
import br.edu.ibmec.cartao_credito.repository.CartaoRepository;
import br.edu.ibmec.cartao_credito.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CartaoRepository cartaoRepository;

    public Usuario criarUsuario(Usuario usuario) throws UsuarioException {
        Optional<Usuario> optUsuario = usuarioRepository.findUsuarioByCpf(usuario.getCpf());

        if (optUsuario.isPresent()) {
            throw new UsuarioException("Usuário com CPF informado já cadastrado");
        }

        // INSERE NA BASE DE DADOS
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarUsuario(int id) throws UsuarioException {
        Usuario usuario = findUsuario(id);
        if (usuario == null) {
            throw new UsuarioException("Usuário não encontrado");
        }
        return usuario;
    }

    public void associarCartao(Cartao cartao, int id) throws UsuarioException {
        // Buscar usuário
        Usuario usuario = buscarUsuario(id);

        // Validar se o cartão está ativo
        if (!cartao.getAtivo()) {
            throw new UsuarioException("Não posso associar um cartão inativo ao usuário");
        }

        // Associa um cartão a um usuário
        usuario.associarCartao(cartao);

        // Salvar cartão de crédito do usuário
        cartaoRepository.save(cartao);

        // Atualiza o usuário com a referência do cartão
        usuarioRepository.save(usuario);
    }

    private Usuario findUsuario(int id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.orElse(null);
    }
}
