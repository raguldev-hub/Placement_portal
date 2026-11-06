package com.college.placement.service;

import com.college.placement.dto.response.DashboardStatsResponse;
import com.college.placement.entity.ApplicationStatus;
import com.college.placement.entity.DriveStatus;
import com.college.placement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final PlacementDriveRepository driveRepository;
    private final ApplicationRepository applicationRepository;

    public DashboardStatsResponse getStats() {
        long totalStudents = studentRepository.count();
        long totalCompanies = companyRepository.count();
        long totalDrives = driveRepository.count();
        long activeDrives = driveRepository.findByStatus(DriveStatus.ONGOING).size()
                + driveRepository.findByStatus(DriveStatus.UPCOMING).size();
        long totalApplications = applicationRepository.count();

        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (ApplicationStatus status : ApplicationStatus.values()) {
            byStatus.put(status.name(), applicationRepository.countByStatus(status));
        }

        long totalSelected = byStatus.getOrDefault(ApplicationStatus.SELECTED.name(), 0L);

        // Distinct students who have at least one SELECTED application
        long distinctSelectedStudents = applicationRepository.findAll().stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SELECTED)
                .map(a -> a.getStudent().getId())
                .distinct()
                .count();

        double placementPercentage = totalStudents == 0 ? 0.0
                : Math.round((distinctSelectedStudents * 10000.0) / totalStudents) / 100.0;

        return DashboardStatsResponse.builder()
                .totalStudents(totalStudents)
                .totalCompanies(totalCompanies)
                .totalDrives(totalDrives)
                .activeDrives(activeDrives)
                .totalApplications(totalApplications)
                .totalSelected(totalSelected)
                .applicationsByStatus(byStatus)
                .placementPercentage(placementPercentage)
                .build();
    }
}
