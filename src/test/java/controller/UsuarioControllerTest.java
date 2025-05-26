package controller;

import com.genation.blogpessoal.model.Usuario;
import com.genation.blogpessoal.model.UsuarioLogin;
import com.genation.blogpessoal.repository.UsuarioRepository;
import com.genation.blogpessoal.service.UsuarioService;
import jakarta.validation.constraints.Null;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import util.TestBuilder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    void start(){
        usuarioRepository.deleteAll();
        usuarioService.cadastrarUsuario(TestBuilder.criarUsuarioRoot());
    }

    @Test
    @DisplayName("Deve cadastrar um novo usuario com sucesso")
    public void deveCadastrarUmNovoUsuario(){
        //Given
        Usuario usuario = TestBuilder.criarUsuario(null,"Renata Negrini", "renata_negrini@email.com.br","12345678");
        //When
        HttpEntity<Usuario> requisicao = new HttpEntity<>(usuario);
        ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
                BASE_URL_USUARIOS, HttpMethod.POST, requisicao, Usuario.class);
        //Then
        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals("Renata Negrini", resposta.getBody().getNome());
        assertEquals("renata_negrini@email.com", resposta.getBody().getUsuario());
    }

    @Test
    @DisplayName("Nao deve permitir duplicacao do usuario")
    public void naoDevePermitirDuplicacao(){
        //Given
        Usuario usuario = TestBuilder.criarUsuario(null,"Angelo dos santos", "angelo@email.com","12345678");
       usuarioService.cadastrarUsuario(usuario);
        //When
        HttpEntity<Usuario> requisicao = new HttpEntity<>(usuario);
        ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
                BASE_URL_USUARIOS + "/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
        //Then
        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());

    }

    @Test
    @DisplayName("Deve atualizar os dados de um usuario com sucesso")
    public void deveAtualizarUmUsuario(){
        //Given
        Usuario usuario = TestBuilder.criarUsuario(null,"Giovana Lucia", "giovanal@email.com.br","12345678");
        Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);

        var usuarioUpdate = TestBuilder.criarUsuario(usuarioCadastrado.get().getId(), "Giovana Lucia Freitas","giovanalfreitas@email.com", "1234321");

        //When
        HttpEntity<Usuario> requisicao = new HttpEntity<>(usuarioUpdate);
        ResponseEntity<Usuario> resposta = testRestTemplate
                .withBasicAuth(USUARIO_ROOT_EMAIL,USUARIO_ROOT_SENHA)
                .exchange(BASE_URL_USUARIOS + "/atualziar", HttpMethod.PUT, requisicao, Usuario.class);

        //then
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Giovana Lucia Freitas", resposta.getBody().getNome());
        assertEquals("giovanalfreitas@email.com", resposta.getBody().getUsuario());
    }

    @Test
    @DisplayName("Deve lsitar todos os usuarios com sucesso")
    public void deveLsitarTodosUsuarios(){
        //Given
        usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null,"Jovani Almeida", "jovani@email.com","12345678"));
        usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null,"Carlos Garcia", "carlos@email.com","12345678"));

        //When
        ResponseEntity<Usuario[]> resposta = testRestTemplate
                .withBasicAuth(USUARIO_ROOT_EMAIL,USUARIO_ROOT_SENHA)
                .exchange(BASE_URL_USUARIOS + "/all", HttpMethod.GET,  null, Usuario[].class);

        //Then
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
    }

    @Test
    @DisplayName("Deve retornar status NOT_FOUND ao buscar usuário por ID inexistente")
    public void deveRetornarNotFoundAoBuscarPorIdInexistente() {

        // Given
        Long idInexistente = 9090909L;

        // When
        ResponseEntity<Usuario> resposta = testRestTemplate
                .withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
                .exchange(BASE_URL_USUARIOS + "/" + idInexistente, HttpMethod.GET, null, Usuario.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }
    @Test
    @DisplayName("Não deve autenticar usuário com credenciais inválidas")
    public void naoDeveAutenticarComCredenciaisInvalidas() {

        // Given
        UsuarioLogin usuarioLoginInvalido = new UsuarioLogin();
        usuarioLoginInvalido.setUsuario("usuario_inexistente@email.com");
        usuarioLoginInvalido.setSenha("senhaErrada");

        // When
        HttpEntity<UsuarioLogin> requisicaoLogin = new HttpEntity<>(usuarioLoginInvalido);
        ResponseEntity<UsuarioLogin> respostaLogin = testRestTemplate.exchange(
                BASE_URL_USUARIOS + "/logar", HttpMethod.POST, requisicaoLogin, UsuarioLogin.class);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, respostaLogin.getStatusCode());

    }

}
