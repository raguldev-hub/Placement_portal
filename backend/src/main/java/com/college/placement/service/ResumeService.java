package com.college.placement.service;

import com.college.placement.dto.response.ResumeResponse;
import com.college.placement.entity.Resume;
import com.college.placement.entity.Student;
import com.college.placement.exception.BadRequestException;
import com.college.placement.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;

    @Value("${app.file.resume-upload-dir}")
    private String uploadDir;

    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    @Transactional
    public ResumeResponse upload(Student student, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Please select a file to upload");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Only PDF or Word documents are allowed");
        }

        try {
            Path dirPath = Paths.get(uploadDir);
            Files.createDirectories(dirPath);

            String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "resume";
            String extension = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : "";
            String storedFileName = "resume_" + student.getId() + "_" + UUID.randomUUID() + extension;

            Path targetPath = dirPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            Resume resume = resumeRepository.findByStudentId(student.getId()).orElse(null);
            if (resume == null) {
                resume = Resume.builder()
                        .student(student)
                        .fileName(storedFileName)
                        .filePath(targetPath.toString())
                        .build();
            } else {
                // Replace old file record; old physical file left in place (or could be deleted here)
                resume.setFileName(storedFileName);
                resume.setFilePath(targetPath.toString());
                resume.setUploadedAt(java.time.LocalDateTime.now());
            }

            resume = resumeRepository.save(resume);

            return ResumeResponse.builder()
                    .id(resume.getId())
                    .fileName(resume.getFileName())
                    .downloadUrl("/uploads/resumes/" + resume.getFileName())
                    .uploadedAt(resume.getUploadedAt())
                    .build();

        } catch (IOException e) {
            throw new BadRequestException("Failed to store resume file: " + e.getMessage());
        }
    }
}
