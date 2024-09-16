package com.projectcloud.project_cloud.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class User {

    @NotNull(message = "Campo id obrigatório")
    private int id;

    @NotBlank(message = "Campo nome obrigatório")
    private String name;

    @NotBlank(message = "Campo cpf obrigatório")
    private String cpf;

    @NotBlank(message = "Campo telefone obrigatório")
    private String phone;

    @NotBlank(message = "Campo data nascimento obrigatório")
    private String birth_date;

}
