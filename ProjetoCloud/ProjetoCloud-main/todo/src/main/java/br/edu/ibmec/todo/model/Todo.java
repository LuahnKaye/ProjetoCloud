package br.edu.ibmec.todo.model;

import org.hibernate.validator.constraints.NotEmpty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Todo {

    private int id;
    
    @NotBlank(message = "Campo nome é obrigatório")
    private String name;

    @NotBlank(message = "Campo responsável é obrigatório")
    private String owner;

    @NotBlank(message = "Campo status é obrigatório")
    private String status;

    @NotBlank(message = "Campo descrição é obrigatório")
    private String description;

}
