package com.college.placement.controller;

import com.college.placement.dto.request.ApplicationStatusUpdateRequest;
import com.college.placement.dto.response.ApplicationResponse;
import com.college.placement.entity.Company;
import com.college.placement.entity.PlacementDrive;
import com.college.placement.entity.Student;
import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.security.SecurityUtils;
import com.college.placement.service.ApplicationService;
import com.college.placement.service.CompanyService;
import com.college.placement.service.PlacementDriveService;
import com.college.placement.service.StudentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Application")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final StudentService studentService;
    private final CompanyService companyService;
    private final PlacementDriveService driveService;

    // ---- Student endpoints ----

    @PostMapping("/my/{driveId}")
    public ResponseEntity<ApplicationResponse> apply(@PathVariable Long driveId) {
        Student student = studentService.getEntityByUserId(SecurityUtils.getCurrentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.apply(student, driveId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications() {
        Student student = studentService.getEntityByUserId(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(applicationService.getByStudent(student.getId()));
    }

    // ---- Company endpoints ----

    @GetMapping("/drive/{driveId}")
    public ResponseEntity<List<ApplicationResponse>> getApplicantsForDrive(@PathVariable Long driveId) {
        Company company = companyService.getEntityByUserId(SecurityUtils.getCurrentUserId());
        PlacementDrive drive = driveService.getEntityById(driveId);
        if (!drive.getCompany().getId().equals(company.getId())) {
            throw new ResourceNotFoundException("Drive not found for this company");
        }
        return ResponseEntity.ok(applicationService.getByDrive(driveId));
    }

    @GetMapping("/company")
    public ResponseEntity<List<ApplicationResponse>> getAllApplicantsForMyCompany() {
        Company company = companyService.getEntityByUserId(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(applicationService.getByCompany(company.getId()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(@PathVariable Long id,
                                                              @Valid @RequestBody ApplicationStatusUpdateRequest request) {
        Company company = companyService.getEntityByUserId(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(applicationService.updateStatus(id, company.getId(), request.getStatus()));
    }
}
