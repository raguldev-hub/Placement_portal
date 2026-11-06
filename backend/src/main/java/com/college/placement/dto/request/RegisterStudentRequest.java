package com.college.placement.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterStudentRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Department is required")
    private String department;

    @NotNull(message = "CGPA is required")
    @DecimalMin(value = "0.0", message = "CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "CGPA cannot exceed 10.0")
    private Double cgpa;

    @NotNull(message = "Backlogs count is required")
    @Min(value = 0, message = "Backlogs cannot be negative")
    private Integer backlogs;

    private String phone;

    private Integer graduationYear;
}
