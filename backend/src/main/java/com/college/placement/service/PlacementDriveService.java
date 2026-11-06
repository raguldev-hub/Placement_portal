package com.college.placement.service;

import com.college.placement.dto.request.PlacementDriveRequest;
import com.college.placement.dto.response.EligibilityResponse;
import com.college.placement.dto.response.PlacementDriveResponse;
import com.college.placement.entity.Company;
import com.college.placement.entity.DriveStatus;
import com.college.placement.entity.PlacementDrive;
import com.college.placement.entity.Student;
import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.repository.ApplicationRepository;
import com.college.placement.repository.PlacementDriveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlacementDriveService {

    private final PlacementDriveRepository driveRepository;
    private final ApplicationRepository applicationRepository;
    private final EligibilityService eligibilityService;

    @Transactional
    public PlacementDriveResponse create(Company company, PlacementDriveRequest request) {
        PlacementDrive drive = PlacementDrive.builder()
                .company(company)
                .jobRole(request.getJobRole())
                .description(request.getDescription())
                .packageLpa(request.getPackageLpa())
                .location(request.getLocation())
                .minCgpa(request.getMinCgpa())
                .maxBacklogs(request.getMaxBacklogs())
                .allowedBranches(request.getAllowedBranches())
                .applicationDeadline(request.getApplicationDeadline())
                .driveDate(request.getDriveDate())
                .status(DriveStatus.UPCOMING)
                .build();

        return toResponse(driveRepository.save(drive), null);
    }

    @Transactional
    public PlacementDriveResponse update(Long driveId, Company company, PlacementDriveRequest request) {
        PlacementDrive drive = getOwnedDrive(driveId, company.getId());

        drive.setJobRole(request.getJobRole());
        drive.setDescription(request.getDescription());
        drive.setPackageLpa(request.getPackageLpa());
        drive.setLocation(request.getLocation());
        drive.setMinCgpa(request.getMinCgpa());
        drive.setMaxBacklogs(request.getMaxBacklogs());
        drive.setAllowedBranches(request.getAllowedBranches());
        drive.setApplicationDeadline(request.getApplicationDeadline());
        drive.setDriveDate(request.getDriveDate());

        return toResponse(driveRepository.save(drive), null);
    }

    @Transactional
    public void delete(Long driveId, Company company) {
        PlacementDrive drive = getOwnedDrive(driveId, company.getId());
        driveRepository.delete(drive);
    }

    @Transactional
    public PlacementDriveResponse updateStatus(Long driveId, Company company, DriveStatus status) {
        PlacementDrive drive = getOwnedDrive(driveId, company.getId());
        drive.setStatus(status);
        return toResponse(driveRepository.save(drive), null);
    }

    public List<PlacementDriveResponse> getByCompany(Long companyId) {
        return driveRepository.findByCompanyId(companyId).stream()
                .map(d -> toResponse(d, null))
                .toList();
    }

    public List<PlacementDriveResponse> getAll() {
        return driveRepository.findAll().stream()
                .map(d -> toResponse(d, null))
                .toList();
    }

    /** Student-facing list: every drive enriched with eligibility + already-applied flag. */
    public List<PlacementDriveResponse> getAllForStudent(Student student) {
        return driveRepository.findAll().stream()
                .map(d -> toResponse(d, student))
                .toList();
    }

    public PlacementDriveResponse getByIdForStudent(Long driveId, Student student) {
        PlacementDrive drive = getEntityById(driveId);
        return toResponse(drive, student);
    }

    public PlacementDriveResponse getById(Long driveId) {
        return toResponse(getEntityById(driveId), null);
    }

    public PlacementDrive getEntityById(Long driveId) {
        return driveRepository.findById(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Placement drive not found with id: " + driveId));
    }

    private PlacementDrive getOwnedDrive(Long driveId, Long companyId) {
        PlacementDrive drive = getEntityById(driveId);
        if (!drive.getCompany().getId().equals(companyId)) {
            throw new ResourceNotFoundException("Placement drive not found for this company");
        }
        return drive;
    }

    private PlacementDriveResponse toResponse(PlacementDrive drive, Student student) {
        PlacementDriveResponse.PlacementDriveResponseBuilder builder = PlacementDriveResponse.builder()
                .id(drive.getId())
                .companyId(drive.getCompany().getId())
                .companyName(drive.getCompany().getCompanyName())
                .jobRole(drive.getJobRole())
                .description(drive.getDescription())
                .packageLpa(drive.getPackageLpa())
                .location(drive.getLocation())
                .minCgpa(drive.getMinCgpa())
                .maxBacklogs(drive.getMaxBacklogs())
                .allowedBranches(drive.getAllowedBranches())
                .applicationDeadline(drive.getApplicationDeadline())
                .driveDate(drive.getDriveDate())
                .status(drive.getStatus());

        if (student != null) {
            EligibilityResponse eligibility = eligibilityService.evaluate(student, drive);
            boolean applied = applicationRepository.existsByStudentIdAndDriveId(student.getId(), drive.getId());
            builder.eligible(eligibility.isEligible())
                    .eligibilityReason(eligibility.getReason())
                    .alreadyApplied(applied);
        }

        return builder.build();
    }
}
