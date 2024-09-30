package br.edu.ibmec.cartao_credito.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ibmec.cartao_credito.exception.UsuarioException;
import br.edu.ibmec.cartao_credito.model.Cartao;
import br.edu.ibmec.cartao_credito.model.Usuario;
import br.edu.ibmec.cartao_credito.repository.CartaoRepository;
import br.edu.ibmec.cartao_credito.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CartaoRepository cartaoRepository;

    // Criar um novo usuário
    public Usuario criarUsuario(Usuario usuario) throws UsuarioException {
        // if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
        //     throw new UsuarioException("Usuário já existe com este e-mail.");
        // }
        return usuarioRepository.save(usuario);
    }

    // Buscar um usuário por ID
    public Usuario buscaUsuario(int id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        return usuarioOpt.orElse(null);
    }

    // Associar um cartão a um usuário
    public void associarCartao(Cartao cartao, int usuarioId) throws Exception {
        Usuario usuario = buscaUsuario(usuarioId);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado.");
        }
        if (usuario.getCartoes() == null) {
            usuario.setCartoes(List.of(cartao));
        } else {
            usuario.getCartoes().add(cartao);
        }
        cartaoRepository.save(cartao);
        usuarioRepository.save(usuario);
    }

    // Atualizar um usuário
    public Usuario atualizarUsuario(int id, Usuario usuarioAtualizado) throws Exception {
        Usuario usuarioExistente = buscaUsuario(id);
        if (usuarioExistente == null) {
            throw new Exception("Usuário não encontrado.");
        }

        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        usuarioExistente.setCartoes(usuarioAtualizado.getCartoes());

        return usuarioRepository.save(usuarioExistente);
    }

    // Deletar um usuário por ID
    public void deletarUsuario(int id) throws Exception {
        Usuario usuario = buscaUsuario(id);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado.");
        }
        usuarioRepository.delete(usuario);
    }

    // Ver cartões associados a um usuário
    public List<Cartao> verCartoesAssociados(int id) throws Exception {
        Usuario usuario = buscaUsuario(id);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado.");
        }
        return usuario.getCartoes();
    }

    // Listar todos os usuários
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }
}
