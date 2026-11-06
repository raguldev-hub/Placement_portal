package com.college.placement.service;

import com.college.placement.dto.response.EligibilityResponse;
import com.college.placement.entity.PlacementDrive;
import com.college.placement.entity.Student;
import org.springframework.stereotype.Service;

@Service
public class EligibilityService {

    /**
     * Evaluates whether a student meets a drive's eligibility criteria.
     * Rules (all must pass):
     *  1. student.cgpa >= drive.minCgpa
     *  2. student.department is in drive.allowedBranches
     *  3. student.backlogs <= drive.maxBacklogs
     */
    public EligibilityResponse evaluate(Student student, PlacementDrive drive) {
        boolean cgpaOk = student.getCgpa() != null && student.getCgpa() >= drive.getMinCgpa();

        boolean branchOk = drive.getAllowedBranches() != null &&
                drive.getAllowedBranches().stream()
                        .anyMatch(b -> b.equalsIgnoreCase(student.getDepartment()));

        boolean backlogsOk = student.getBacklogs() != null && student.getBacklogs() <= drive.getMaxBacklogs();

        boolean eligible = cgpaOk && branchOk && backlogsOk;

        String reason;
        if (eligible) {
            reason = "Meets all eligibility criteria";
        } else {
            StringBuilder sb = new StringBuilder("Not eligible: ");
            if (!cgpaOk) sb.append(String.format("CGPA %.2f < required %.2f; ", student.getCgpa(), drive.getMinCgpa()));
            if (!branchOk) sb.append(String.format("branch '%s' not in allowed branches; ", student.getDepartment()));
            if (!backlogsOk) sb.append(String.format("backlogs %d > allowed %d; ", student.getBacklogs(), drive.getMaxBacklogs()));
            reason = sb.toString().trim();
        }

        return EligibilityResponse.builder()
                .driveId(drive.getId())
                .studentId(student.getId())
                .eligible(eligible)
                .reason(reason)
                .cgpaOk(cgpaOk)
                .branchOk(branchOk)
                .backlogsOk(backlogsOk)
                .build();
    }
}
