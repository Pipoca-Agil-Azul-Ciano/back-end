package br.com.pipoca.PipocaAgilBackend.dtos;

import jakarta.validation.constraints.NotBlank;

public class UserIdentityDTO {
    @NotBlank
    public String userHash;

}
