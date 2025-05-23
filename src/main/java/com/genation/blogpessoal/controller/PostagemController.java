package com.genation.blogpessoal.controller;

import com.genation.blogpessoal.model.Postagem;
import com.genation.blogpessoal.repository.PostagemRepository;
import com.genation.blogpessoal.repository.TemaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostagemController {

    private final PostagemRepository postagemRepository;
    private final TemaRepository temaRepository;

    public PostagemController(PostagemRepository postagemRepository, TemaRepository temaRepository) {
        this.postagemRepository = postagemRepository;
        this.temaRepository = temaRepository;
    }

    @GetMapping
    public ResponseEntity<List<Postagem>> getAll(){
        return ResponseEntity.ok(postagemRepository.findAll());
    }


    @PostMapping
    public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem){
        if(temaRepository.existsById(postagem.getTema().getId())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem));
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"O tema nao exista",null);
    }

    @PutMapping
    public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem) {

        if (postagemRepository.existsById(postagem.getId())){
            if(temaRepository.existsById(postagem.getTema().getId()))

                return ResponseEntity.status(HttpStatus.OK).body(postagemRepository.save(postagem));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"O tema nao exista",null);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete (@PathVariable Long id){
        Optional<Postagem> postagem = postagemRepository.findById(id);

        if(postagem.isEmpty())
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND);

        postagemRepository.deleteById(id);
    }
}
