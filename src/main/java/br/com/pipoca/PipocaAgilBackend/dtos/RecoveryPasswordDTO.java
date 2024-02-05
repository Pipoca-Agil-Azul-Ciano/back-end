package br.com.pipoca.PipocaAgilBackend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RecoveryPasswordDTO {
    @Email(message = "Invalid email format. Enter a valid email.")
    @NotBlank(message = "Email cannot be blank.")
    public String email;
    public RecoveryPasswordDTO(String email) {
        this.email = email;
    }
    public RecoveryPasswordDTO() {}

}
