package br.edu.ibmec.cartao_credito.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.edu.ibmec.cartao_credito.model.Cartao;
import br.edu.ibmec.cartao_credito.model.Usuario;

@Service
public class UsuarioService {
    private static List<Usuario> database = new ArrayList<>();

    public Usuario criarUsuario(String nome, String cpf, LocalDateTime dataNascimento) {
        Usuario usuario = new Usuario();
        
        //TODO: Validar CPF
        usuario.setCpf(cpf);
        
        usuario.setNome(nome);
        usuario.setDataNascimento(dataNascimento);
        //usuario.setId(UUID.randomUUID());
        
        database.add(usuario);

        return usuario;
    }

    public Usuario buscaUsuario(int id) {
        return this.findUsuario(id);
    }

    public void associarCartao(Cartao cartao, int id) throws Exception {
        //Buscar usuario

        Usuario usuario = this.findUsuario(id);

        //Valida se encontrou o usuario
        if (usuario == null) {
            throw new Exception("Não encontrei o usuario");
        }

        //valida se o cartão está ativo
        if (cartao.getAtivo() == false) {
            throw new Exception("Não posso associar um cartão inativo ao usuário");
        }

        //Associa um cartão a um usuario
        usuario.associarCartao(cartao);

    }

    private Usuario findUsuario(int id) {
        for (Usuario item : database) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }
    
}
