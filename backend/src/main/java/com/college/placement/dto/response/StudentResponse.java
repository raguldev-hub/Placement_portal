package com.college.placement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {
    private Long id;
    private Long userId;
    private String email;
    private String fullName;
    private String department;
    private Double cgpa;
    private Integer backlogs;
    private String phone;
    private Integer graduationYear;
    private Set<String> skills;
    private ResumeResponse resume;
}
