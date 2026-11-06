package com.college.placement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyUpdateRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String description;

    private String website;

    private String industry;
}
