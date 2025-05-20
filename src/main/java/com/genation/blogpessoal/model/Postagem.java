package com.genation.blogpessoal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="tb_postagens")
public class Postagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 100)
    @NotBlank(message = "O atributo titulo é obrigatorio")
    @Size(min = 5, max = 100, message = "O titulo deve ter no minimo 5 caracteres e no maximo 100")
    private String titulo;

    @Column(length = 1000)
    @NotBlank(message = "O atributo texto é obrigatorio")
    @Size(min = 10, max = 100, message = "O texto  deve ter no minimo 10 caracteres e no maximo 1000")
    private String texto;

    @UpdateTimestamp
    private LocalDateTime data;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}
