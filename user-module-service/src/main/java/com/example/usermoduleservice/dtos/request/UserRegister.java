package com.example.usermoduleservice.dtos.request;

import com.example.usermoduleservice.validation.RoleTypeValidation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegister {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Email(message = "Invalid email address")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^0[789][01][0-9]{7}$", message = "Invalid phone number")
    private String phoneNumber;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @RoleTypeValidation
    @NotBlank(message = "Role must not be blank")
    private String role;
}
