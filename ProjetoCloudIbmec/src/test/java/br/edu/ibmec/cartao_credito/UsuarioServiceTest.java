package br.edu.ibmec.cartao_credito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.edu.ibmec.cartao_credito.exception.UsuarioException;
import br.edu.ibmec.cartao_credito.model.Cartao;
import br.edu.ibmec.cartao_credito.model.Usuario;
import br.edu.ibmec.cartao_credito.repository.CartaoRepository;
import br.edu.ibmec.cartao_credito.repository.UsuarioRepository;
import br.edu.ibmec.cartao_credito.service.UsuarioService;

class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CartaoRepository cartaoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCriarUsuario_Sucesso() throws UsuarioException {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome("Luahn Kaiaque");
        novoUsuario.setCpf("123.456.789-00");
        novoUsuario.setDataNascimento(LocalDateTime.now().minusYears(30));

        when(usuarioRepository.findUsuarioByCpf(novoUsuario.getCpf())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(novoUsuario);

        Usuario usuarioCriado = usuarioService.criarUsuario(novoUsuario);

        assertNotNull(usuarioCriado);
        assertEquals(novoUsuario.getNome(), usuarioCriado.getNome());
        assertEquals(novoUsuario.getCpf(), usuarioCriado.getCpf());
        verify(usuarioRepository).save(novoUsuario);
    }

    @Test
    void testCriarUsuario_CPFJaExiste() {
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setCpf("123.456.789-00");

        when(usuarioRepository.findUsuarioByCpf(usuarioExistente.getCpf())).thenReturn(Optional.of(usuarioExistente));

        assertThrows(UsuarioException.class, () -> usuarioService.criarUsuario(usuarioExistente));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testAssociarCartao_Sucesso() throws UsuarioException {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setCartoes(new ArrayList<>());

        Cartao cartao = new Cartao();
        cartao.setAtivo(true);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(cartaoRepository.save(any(Cartao.class))).thenReturn(cartao);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        assertDoesNotThrow(() -> usuarioService.associarCartao(cartao, 1));
        assertTrue(usuario.getCartoes().contains(cartao));
        verify(cartaoRepository).save(cartao);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testAssociarCartao_CartaoInativo() {
        Usuario usuario = new Usuario();
        usuario.setId(1);

        Cartao cartaoInativo = new Cartao();
        cartaoInativo.setAtivo(false);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        assertThrows(UsuarioException.class, () -> usuarioService.associarCartao(cartaoInativo, 1));
        verify(cartaoRepository, never()).save(any(Cartao.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testAssociarCartao_UsuarioNaoEncontrado() {
        Cartao cartao = new Cartao();
        cartao.setAtivo(true);

        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UsuarioException.class, () -> usuarioService.associarCartao(cartao, 1));
        verify(cartaoRepository, never()).save(any(Cartao.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}