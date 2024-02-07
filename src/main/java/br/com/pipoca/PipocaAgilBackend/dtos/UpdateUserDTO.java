package br.com.pipoca.PipocaAgilBackend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class UpdateUserDTO {
    @NotBlank
    public String fullName;
    @Email(message = "Invalid email format. Enter a valid email.")
    @NotBlank(message = "Email cannot be blank.")
    public String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,}$", message = "Invalid Password. Enter a valid password.")
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
