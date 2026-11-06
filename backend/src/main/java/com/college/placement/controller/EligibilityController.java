package com.college.placement.controller;

import com.college.placement.dto.response.EligibilityResponse;
import com.college.placement.entity.PlacementDrive;
import com.college.placement.entity.Student;
import com.college.placement.security.SecurityUtils;
import com.college.placement.service.EligibilityService;
import com.college.placement.service.PlacementDriveService;
import com.college.placement.service.StudentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/eligibility")
@RequiredArgsConstructor
@Tag(name = "Eligibility")
public class EligibilityController {

    private final EligibilityService eligibilityService;
    private final StudentService studentService;
    private final PlacementDriveService driveService;

    /** Student checks their own eligibility for a specific drive. */
    @GetMapping("/check/{driveId}")
    public ResponseEntity<EligibilityResponse> checkMyEligibility(@PathVariable Long driveId) {
        Student student = studentService.getEntityByUserId(SecurityUtils.getCurrentUserId());
        PlacementDrive drive = driveService.getEntityById(driveId);
        return ResponseEntity.ok(eligibilityService.evaluate(student, drive));
    }

    /** Admin/Company checks a specific student's eligibility for a drive. */
    @GetMapping("/check/{driveId}/student/{studentId}")
    public ResponseEntity<EligibilityResponse> checkStudentEligibility(@PathVariable Long driveId,
                                                                         @PathVariable Long studentId) {
        Student student = studentService.getEntityById(studentId);
        PlacementDrive drive = driveService.getEntityById(driveId);
        return ResponseEntity.ok(eligibilityService.evaluate(student, drive));
    }
}
