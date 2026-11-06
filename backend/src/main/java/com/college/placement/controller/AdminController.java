package com.college.placement.controller;

import com.college.placement.dto.request.ApplicationStatusUpdateRequest;
import com.college.placement.dto.response.*;
import com.college.placement.entity.ApplicationStatus;
import com.college.placement.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin")
public class AdminController {

    private final AdminDashboardService dashboardService;
    private final StudentService studentService;
    private final CompanyService companyService;
    private final PlacementDriveService driveService;
    private final ApplicationService applicationService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAll());
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getById(id));
    }

    @GetMapping("/companies")
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAll());
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<CompanyResponse> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getById(id));
    }

    @GetMapping("/drives")
    public ResponseEntity<List<PlacementDriveResponse>> getAllDrives() {
        return ResponseEntity.ok(driveService.getAll());
    }

    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAll());
    }

    @GetMapping("/selections")
    public ResponseEntity<List<ApplicationResponse>> getAllSelections() {
        List<ApplicationResponse> selections = applicationService.getAll().stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SELECTED)
                .toList();
        return ResponseEntity.ok(selections);
    }

    @PatchMapping("/applications/{id}/status")
    public ResponseEntity<ApplicationResponse> overrideApplicationStatus(
            @PathVariable Long id, @Valid @RequestBody ApplicationStatusUpdateRequest request) {
        // companyId = null -> admin override, bypasses ownership check
        return ResponseEntity.ok(applicationService.updateStatus(id, null, request.getStatus()));
    }
}
