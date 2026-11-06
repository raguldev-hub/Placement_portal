package com.college.placement.dto.response;

import com.college.placement.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponse {
    private Long id;

    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Double studentCgpa;
    private String studentDepartment;

    private Long driveId;
    private String jobRole;
    private String companyName;
    private Double packageLpa;

    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
