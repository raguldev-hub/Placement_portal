package com.college.placement.service;

import com.college.placement.dto.request.StudentUpdateRequest;
import com.college.placement.dto.response.ResumeResponse;
import com.college.placement.dto.response.StudentResponse;
import com.college.placement.entity.Resume;
import com.college.placement.entity.Student;
import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentResponse getByUserId(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return toResponse(student);
    }

    public StudentResponse getById(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return toResponse(student);
    }

    public List<StudentResponse> getAll() {
        return studentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public StudentResponse updateProfile(Long userId, StudentUpdateRequest request) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        student.setFullName(request.getFullName());
        student.setDepartment(request.getDepartment());
        student.setCgpa(request.getCgpa());
        student.setBacklogs(request.getBacklogs());
        student.setPhone(request.getPhone());
        student.setGraduationYear(request.getGraduationYear());
        if (request.getSkills() != null) {
            student.setSkills(request.getSkills());
        }

        return toResponse(studentRepository.save(student));
    }

    /** Used internally by other services (eligibility, applications) to get the raw entity. */
    public Student getEntityByUserId(Long userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
    }

    public Student getEntityById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
    }

    public StudentResponse toResponse(Student student) {
        Resume resume = student.getResume();
        ResumeResponse resumeResponse = null;
        if (resume != null) {
            resumeResponse = ResumeResponse.builder()
                    .id(resume.getId())
                    .fileName(resume.getFileName())
                    .downloadUrl("/uploads/resumes/" + resume.getFileName())
                    .uploadedAt(resume.getUploadedAt())
                    .build();
        }

        return StudentResponse.builder()
                .id(student.getId())
                .userId(student.getUser().getId())
                .email(student.getUser().getEmail())
                .fullName(student.getFullName())
                .department(student.getDepartment())
                .cgpa(student.getCgpa())
                .backlogs(student.getBacklogs())
                .phone(student.getPhone())
                .graduationYear(student.getGraduationYear())
                .skills(student.getSkills())
                .resume(resumeResponse)
                .build();
    }
}
