package br.com.pipoca.PipocaAgilBackend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class UpdateUserDTO {

    public String fullName;
    public String email;
    public String password;
    public LocalDate dateBirth;
    public UpdateUserDTO() {}

    public UpdateUserDTO( String fullName, String email, String password, LocalDate dateBirth) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.dateBirth = dateBirth;
    }
}
