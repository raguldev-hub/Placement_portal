package com.college.placement.controller;

import com.college.placement.dto.request.CompanyUpdateRequest;
import com.college.placement.dto.response.CompanyResponse;
import com.college.placement.security.SecurityUtils;
import com.college.placement.service.CompanyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Company")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/me")
    public ResponseEntity<CompanyResponse> getMyProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(companyService.getByUserId(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<CompanyResponse> updateMyProfile(@Valid @RequestBody CompanyUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(companyService.updateProfile(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getById(id));
    }
}
