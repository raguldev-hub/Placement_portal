package com.college.placement.controller;

import com.college.placement.dto.request.PlacementDriveRequest;
import com.college.placement.dto.response.MessageResponse;
import com.college.placement.dto.response.PlacementDriveResponse;
import com.college.placement.entity.Company;
import com.college.placement.entity.DriveStatus;
import com.college.placement.entity.Role;
import com.college.placement.entity.Student;
import com.college.placement.security.SecurityUtils;
import com.college.placement.security.UserPrincipal;
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
@RequestMapping("/api/drives")
@RequiredArgsConstructor
@Tag(name = "Placement Drive")
public class PlacementDriveController {

    private final PlacementDriveService driveService;
    private final StudentService studentService;
    private final CompanyService companyService;

    /** Unauthenticated teaser listing - open drives only, no eligibility info. */
    @GetMapping("/public")
    public ResponseEntity<List<PlacementDriveResponse>> getPublicDrives() {
        List<PlacementDriveResponse> drives = driveService.getAll().stream()
                .filter(d -> d.getStatus() != DriveStatus.CLOSED)
                .toList();
        return ResponseEntity.ok(drives);
    }

    /** Authenticated listing: students get eligibility + applied flags; others get plain list. */
    @GetMapping
    public ResponseEntity<List<PlacementDriveResponse>> getAllDrives() {
        UserPrincipal principal = SecurityUtils.getCurrentUser();
        if (principal.getRole() == Role.STUDENT) {
            Student student = studentService.getEntityByUserId(principal.getId());
            return ResponseEntity.ok(driveService.getAllForStudent(student));
        }
        return ResponseEntity.ok(driveService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlacementDriveResponse> getDriveById(@PathVariable Long id) {
        UserPrincipal principal = SecurityUtils.getCurrentUser();
        if (principal.getRole() == Role.STUDENT) {
            Student student = studentService.getEntityByUserId(principal.getId());
            return ResponseEntity.ok(driveService.getByIdForStudent(id, student));
        }
        return ResponseEntity.ok(driveService.getById(id));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<PlacementDriveResponse>> getByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(driveService.getByCompany(companyId));
    }

    // ---- Company management endpoints ----

    @GetMapping("/manage")
    public ResponseEntity<List<PlacementDriveResponse>> getMyDrives() {
        Company company = companyService.getEntityByUserId(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(driveService.getByCompany(company.getId()));
    }

    @PostMapping("/manage")
    public ResponseEntity<PlacementDriveResponse> createDrive(@Valid @RequestBody PlacementDriveRequest request) {
        Company company = companyService.getEntityByUserId(SecurityUtils.getCurrentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(driveService.create(company, request));
    }

    @PutMapping("/manage/{id}")
    public ResponseEntity<PlacementDriveResponse> updateDrive(@PathVariable Long id,
                                                                @Valid @RequestBody PlacementDriveRequest request) {
        Company company = companyService.getEntityByUserId(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(driveService.update(id, company, request));
    }

    @DeleteMapping("/manage/{id}")
    public ResponseEntity<MessageResponse> deleteDrive(@PathVariable Long id) {
        Company company = companyService.getEntityByUserId(SecurityUtils.getCurrentUserId());
        driveService.delete(id, company);
        return ResponseEntity.ok(MessageResponse.builder().message("Placement drive deleted successfully").build());
    }

    @PatchMapping("/manage/{id}/status")
    public ResponseEntity<PlacementDriveResponse> updateStatus(@PathVariable Long id,
                                                                 @RequestParam DriveStatus status) {
        Company company = companyService.getEntityByUserId(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(driveService.updateStatus(id, company, status));
    }
}
