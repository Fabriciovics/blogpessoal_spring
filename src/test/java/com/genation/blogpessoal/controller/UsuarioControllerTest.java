package com.genation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.genation.blogpessoal.model.Usuario;
import com.genation.blogpessoal.model.UsuarioLogin;
import com.genation.blogpessoal.repository.UsuarioRepository;
import com.genation.blogpessoal.service.UsuarioService;
import com.genation.blogpessoal.util.TestBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final String USUARIO_ROOT_EMAIL = "root@email.com";
    private static final String USUARIO_ROOT_SENHA = "rootroot";
    private static final String BASE_URL_USUARIOS = "/usuarios";

    @BeforeAll
    void start() {
        usuarioRepository.deleteAll();
        usuarioService.cadastrarUsuario(TestBuilder.criarUsuarioRoot());
    }

    @Test
    @DisplayName("Deve cadastrar um novo usuário com sucesso")
    public void deveCadastrarUsuario() {

        //Given
        Usuario usuario = TestBuilder.criarUsuario(null, "Renata Negrini", "renata_negrini@email.com.br", "12345678");

        //When
        HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
        ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
                BASE_URL_USUARIOS + "/cadastrar", HttpMethod.POST, requisicao, Usuario.class);

        //Then
        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals("Renata Negrini", resposta.getBody().getNome());
        assertEquals("renata_negrini@email.com.br", resposta.getBody().getUsuario());

    }

    @Test
    @DisplayName("Não deve permitir a duplicação do usuário")
    public void naoDeveDuplicarUsuario() {

        //Given
        Usuario usuario = TestBuilder.criarUsuario(null, "Angelo dos Santos","angelo@email.com.br", "12345678");
        usuarioService.cadastrarUsuario(usuario);

        // When
        HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
        ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
                BASE_URL_USUARIOS + "/cadastrar", HttpMethod.POST, requisicao, Usuario.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());

    }
    @Test
    @DisplayName("Deve atualizar os dados de um usuário com sucesso")
    public void deveAtualizarUmUsuario() {

        //Given
        Usuario usuario = TestBuilder.criarUsuario(null, "Giovana Lucia Freitas", "giovana_lf@email.com.br", "12345678");
        Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);

        Usuario usuarioUpdate = TestBuilder.criarUsuario(usuarioCadastrado.get().getId(), "Giovana Lucia Freitas", "giovana_lf@email.com.br", "12345678");

        //When
        HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuarioUpdate);
        ResponseEntity<Usuario> resposta = testRestTemplate
                .withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
                .exchange(BASE_URL_USUARIOS + "/atualizar", HttpMethod.PUT, requisicao, Usuario.class);

        //Then
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Giovana Lucia Freitas", resposta.getBody().getNome());
        assertEquals("giovana_lf@email.com.br", resposta.getBody().getUsuario());

    }

    @Test
    @DisplayName("Deve listar todos os usuários com sucesso")
    public void deveListarTodosUsuarios() {


        //Given
        usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Jovani Almeida", "jovani_almeida@email.com.br", "12345678"));
        usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Carlos Garcia", "carlos_garcia@email.com.br", "12345678"));

        //When
        ResponseEntity<Usuario[]> resposta = testRestTemplate
                .withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
                .exchange(BASE_URL_USUARIOS + "/listar", HttpMethod.GET, null, Usuario[].class);

        //Then
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
    }

    @Test
    @DisplayName("Deve listar usuario por ID")
    public void deveListarUsuarioPorId() {

        //Given
        usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Jovani Almeida", "jovani_almeida@email.com.br", "12345678"));

        //When
        ResponseEntity<Usuario> resposta = testRestTemplate
                .withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
                .exchange(BASE_URL_USUARIOS + "/listar/1", HttpMethod.GET, null, Usuario.class);

        //Then
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
    }

    @Test
    @DisplayName("Não Deve listar usuario por ID")
    public void naoDeveListarUsuarioPorId() {

        //Given
        usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Jovani Almeida", "jovani_almeida@email.com.br", "12345678"));

        //When
        ResponseEntity<Usuario> resposta = testRestTemplate
                .withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
                .exchange(BASE_URL_USUARIOS + "/listar/0", HttpMethod.GET, null, Usuario.class);

        //Then
        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve logar um usuário com sucesso")
    public void deveLogarUsuario() {

        //Given
        Usuario usuario = TestBuilder.criarUsuario(null, "Rafael Queiroz", "rafael_queiroz@email.com.br", "12345678");
        UsuarioLogin usuarioLogin = TestBuilder.criarLoginUsuario(usuario.getUsuario(), usuario.getSenha());

        //When
        HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
        ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
                BASE_URL_USUARIOS + "/cadastrar", HttpMethod.POST, requisicao, Usuario.class);

        HttpEntity<UsuarioLogin> requisicaoLogin = new HttpEntity<UsuarioLogin>(usuarioLogin);
        ResponseEntity<UsuarioLogin> respostaLogin = testRestTemplate.exchange(
                BASE_URL_USUARIOS + "/logar", HttpMethod.POST, requisicaoLogin, UsuarioLogin.class);

        //Then
        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(HttpStatus.CREATED, respostaLogin.getStatusCode());
        assertNotNull(respostaLogin.getBody());
    }

    @Test
    @DisplayName("Não deve logar um usuário com sucesso")
    public void naoDeveLogarUsuario() {

        //Given
        Usuario usuario = TestBuilder.criarUsuario(null, "Victor Hugo", "victor_hugo@email.com.br", "12345678");
        UsuarioLogin usuarioLogin = TestBuilder.criarLoginUsuario(usuario.getUsuario(), "senhaerrada");

        //When
        HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
        ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
                BASE_URL_USUARIOS + "/cadastrar", HttpMethod.POST, requisicao, Usuario.class);

        HttpEntity<UsuarioLogin> requisicaoLogin = new HttpEntity<UsuarioLogin>(usuarioLogin);
        ResponseEntity<UsuarioLogin> respostaLogin = testRestTemplate.exchange(
                BASE_URL_USUARIOS + "/logar", HttpMethod.POST, requisicaoLogin, UsuarioLogin.class);

        //Then
        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, respostaLogin.getStatusCode());
    }

}