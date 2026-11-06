package com.college.placement.dto.response;

import com.college.placement.entity.DriveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacementDriveResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private String jobRole;
    private String description;
    private Double packageLpa;
    private String location;
    private Double minCgpa;
    private Integer maxBacklogs;
    private Set<String> allowedBranches;
    private LocalDate applicationDeadline;
    private LocalDate driveDate;
    private DriveStatus status;

    // Populated only for student-facing endpoints
    private Boolean eligible;
    private String eligibilityReason;
    private Boolean alreadyApplied;
}
