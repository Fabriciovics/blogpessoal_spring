package com.genation.blogpessoal.service;

import com.genation.blogpessoal.model.Usuario;
import com.genation.blogpessoal.model.UsuarioLogin;
import com.genation.blogpessoal.repository.UsuarioRepository;
import com.genation.blogpessoal.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;


    public Optional<Usuario> cadastrarUsuario(Usuario usuario) {
        if(usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()){
            return Optional.empty();
        }
        usuario.setSenha(criptografarSenha(usuario.getSenha()));

        return Optional.ofNullable(usuarioRepository.save(usuario));
    }

    public Optional<Usuario> atualizarUsuario(Usuario usuario) {

        if(usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
            usuario.setSenha(criptografarSenha(usuario.getSenha()));

            return Optional.ofNullable(usuarioRepository.save(usuario));
        }
        return Optional.empty();
    }

    public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {
        var credencias = new UsernamePasswordAuthenticationToken(usuarioLogin.get().getUsuario(), usuarioLogin.get().getSenha());

        Authentication authentication = authenticationManager.authenticate(credencias);

        if(authentication.isAuthenticated()){
            Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());
            if(usuario.isPresent()){
                usuarioLogin.get().setId(usuario.get().getId());
                usuarioLogin.get().setNome(usuario.get().getNome());
                usuarioLogin.get().setFoto(usuario.get().getFoto());
                usuarioLogin.get().setSenha("");
                usuarioLogin.get().setToken(gerarToken(usuarioLogin.get().getUsuario()));
                return usuarioLogin;
            }
        }
        return Optional.empty();
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    private String gerarToken(String usuario) {
        return "Bearer " + jwtService.generateToken(usuario);
    }

    private String criptografarSenha(String senha) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(senha);
    }
}
