package com.college.placement.service;

import com.college.placement.dto.response.ApplicationResponse;
import com.college.placement.dto.response.EligibilityResponse;
import com.college.placement.entity.*;
import com.college.placement.exception.BadRequestException;
import com.college.placement.exception.DuplicateResourceException;
import com.college.placement.exception.IneligibleApplicationException;
import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final PlacementDriveService placementDriveService;
    private final EligibilityService eligibilityService;

    @Transactional
    public ApplicationResponse apply(Student student, Long driveId) {
        PlacementDrive drive = placementDriveService.getEntityById(driveId);

        if (drive.getStatus() == DriveStatus.CLOSED) {
            throw new BadRequestException("This placement drive is closed for applications");
        }

        if (drive.getApplicationDeadline() != null && LocalDate.now().isAfter(drive.getApplicationDeadline())) {
            throw new BadRequestException("Application deadline has passed for this drive");
        }

        if (applicationRepository.existsByStudentIdAndDriveId(student.getId(), driveId)) {
            throw new DuplicateResourceException("You have already applied to this drive");
        }

        // Automatic eligibility engine — blocks ineligible students server-side
        EligibilityResponse eligibility = eligibilityService.evaluate(student, drive);
        if (!eligibility.isEligible()) {
            throw new IneligibleApplicationException(eligibility.getReason());
        }

        Application application = Application.builder()
                .student(student)
                .drive(drive)
                .status(ApplicationStatus.APPLIED)
                .build();

        return toResponse(applicationRepository.save(application));
    }

    public List<ApplicationResponse> getByStudent(Long studentId) {
        return applicationRepository.findByStudentId(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ApplicationResponse> getByDrive(Long driveId) {
        return applicationRepository.findByDriveId(driveId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ApplicationResponse> getByCompany(Long companyId) {
        return applicationRepository.findByCompanyId(companyId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ApplicationResponse> getAll() {
        return applicationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ApplicationResponse updateStatus(Long applicationId, Long companyId, ApplicationStatus newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        // Companies may only update applications for their own drives; null companyId = admin override
        if (companyId != null && !application.getDrive().getCompany().getId().equals(companyId)) {
            throw new ResourceNotFoundException("Application not found for this company");
        }

        application.setStatus(newStatus);
        return toResponse(applicationRepository.save(application));
    }

    public ApplicationResponse getById(Long applicationId) {
        return toResponse(applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId)));
    }

    private ApplicationResponse toResponse(Application app) {
        Student student = app.getStudent();
        PlacementDrive drive = app.getDrive();

        return ApplicationResponse.builder()
                .id(app.getId())
                .studentId(student.getId())
                .studentName(student.getFullName())
                .studentEmail(student.getUser().getEmail())
                .studentCgpa(student.getCgpa())
                .studentDepartment(student.getDepartment())
                .driveId(drive.getId())
                .jobRole(drive.getJobRole())
                .companyName(drive.getCompany().getCompanyName())
                .packageLpa(drive.getPackageLpa())
                .status(app.getStatus())
                .appliedAt(app.getAppliedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }
}
