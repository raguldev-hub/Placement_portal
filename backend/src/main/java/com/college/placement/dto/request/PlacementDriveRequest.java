package com.college.placement.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class PlacementDriveRequest {

    @NotBlank(message = "Job role is required")
    private String jobRole;

    private String description;

    @NotNull(message = "Package (LPA) is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Package must be greater than 0")
    private Double packageLpa;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Minimum CGPA is required")
    @DecimalMin(value = "0.0", message = "Minimum CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "Minimum CGPA cannot exceed 10.0")
    private Double minCgpa;

    @NotNull(message = "Maximum backlogs is required")
    @Min(value = 0, message = "Maximum backlogs cannot be negative")
    private Integer maxBacklogs;

    @NotEmpty(message = "At least one allowed branch is required")
    private Set<String> allowedBranches;

    @NotNull(message = "Application deadline is required")
    @FutureOrPresent(message = "Application deadline cannot be in the past")
    private LocalDate applicationDeadline;

    private LocalDate driveDate;
}
