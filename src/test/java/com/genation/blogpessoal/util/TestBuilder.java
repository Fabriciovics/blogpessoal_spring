package com.genation.blogpessoal.util;

import com.genation.blogpessoal.model.Usuario;
import com.genation.blogpessoal.model.UsuarioLogin;

public class TestBuilder {

    public static Usuario criarUsuario(Long id, String nome, String email, String senha) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome(nome);
        usuario.setUsuario(email);
        usuario.setSenha(senha);
        usuario.setFoto("-");
        return usuario;
    }

    public static UsuarioLogin criarLoginUsuario(String usuario, String senha) {
        UsuarioLogin usuarioLogin = new UsuarioLogin();
        usuarioLogin.setUsuario(usuario);
        usuarioLogin.setSenha(senha);

        return usuarioLogin;
    }

    public static Usuario criarUsuarioRoot() {
        return criarUsuario(null, "Root", "root@email.com", "rootroot");
    }
}