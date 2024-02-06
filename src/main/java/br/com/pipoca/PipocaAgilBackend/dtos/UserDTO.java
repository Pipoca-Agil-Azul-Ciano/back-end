package br.com.pipoca.PipocaAgilBackend.dtos;

import br.com.pipoca.PipocaAgilBackend.enums.UserTypeEnum;

import java.time.LocalDate;

public class UserDTO {
    public String fullName;

    public String email;
    public LocalDate dateBirth;

    public UserTypeEnum userType;
    public UserDTO() {}
    public UserDTO(String fullName, String email, LocalDate dateBirth, UserTypeEnum userType) {
        this.fullName = fullName;
        this.email = email;
        this.dateBirth = dateBirth;
        this.userType = userType;
    }

}
