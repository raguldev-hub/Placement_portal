package com.college.placement.controller;

import com.college.placement.dto.request.StudentUpdateRequest;
import com.college.placement.dto.response.ResumeResponse;
import com.college.placement.dto.response.StudentResponse;
import com.college.placement.entity.Student;
import com.college.placement.security.SecurityUtils;
import com.college.placement.service.ResumeService;
import com.college.placement.service.StudentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student")
public class StudentController {

    private final StudentService studentService;
    private final ResumeService resumeService;

    @GetMapping("/me")
    public ResponseEntity<StudentResponse> getMyProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(studentService.getByUserId(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<StudentResponse> updateMyProfile(@Valid @RequestBody StudentUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(studentService.updateProfile(userId, request));
    }

    @PostMapping(value = "/me/resume", consumes = "multipart/form-data")
    public ResponseEntity<ResumeResponse> uploadResume(@RequestParam("file") MultipartFile file) {
        Long userId = SecurityUtils.getCurrentUserId();
        Student student = studentService.getEntityByUserId(userId);
        return ResponseEntity.ok(resumeService.upload(student, file));
    }
}
