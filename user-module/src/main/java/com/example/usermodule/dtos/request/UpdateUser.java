package com.example.usermodule.dtos.request;

import com.example.usermodule.validation.RoleTypeValidation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUser {
    private String name;

    @Email(message = "Invalid email address")
    private String email;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid phone number")
    private String phoneNumber;

    @RoleTypeValidation
    private String role;
}
