package br.edu.ibmec.cartao_credito.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.ibmec.cartao_credito.model.Cartao;
import br.edu.ibmec.cartao_credito.model.Usuario;
import br.edu.ibmec.cartao_credito.service.UsuarioService;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Criar um novo usuário
    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@Valid @RequestBody Usuario usuario) throws Exception { 
        usuarioService.criarUsuario(usuario);
        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }

    // Buscar usuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuario(@PathVariable int id) {
        Usuario usuario = usuarioService.buscaUsuario(id);
        if (usuario != null) {
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Associar um cartão a um usuário
    @PostMapping("/{id}/cartao")
    public ResponseEntity<String> associarCartao(@PathVariable int id, @Valid @RequestBody Cartao cartao) {
        try {
            usuarioService.associarCartao(cartao, id);
            return new ResponseEntity<>("Cartão associado com sucesso", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Ver cartões associados a um usuário
    @GetMapping("/{id}/cartoes")
    public ResponseEntity<List<Cartao>> verCartoesAssociados(@PathVariable int id) {
        try {
            List<Cartao> cartoes = usuarioService.verCartoesAssociados(id);
            return new ResponseEntity<>(cartoes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
