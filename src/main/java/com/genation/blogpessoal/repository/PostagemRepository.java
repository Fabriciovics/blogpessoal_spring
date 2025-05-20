package com.genation.blogpessoal.repository;

import com.genation.blogpessoal.model.Postagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostagemRepository extends JpaRepository<Postagem, Long> {

    List<Postagem>  findAllByTituloContainingIgnoreCase(String titulo);
    //SELECT * FROM tb_postagens WHERE titulo  LIKE "%?%;"
}
