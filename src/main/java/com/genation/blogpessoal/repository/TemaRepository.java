package com.genation.blogpessoal.repository;

import com.genation.blogpessoal.model.Postagem;
import com.genation.blogpessoal.model.Tema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemaRepository extends JpaRepository<Tema, Long> {

    List<Tema> findAllByDescricaoContainingIgnoreCase(String descricao);

}
