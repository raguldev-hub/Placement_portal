package com.college.placement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {
    private long totalStudents;
    private long totalCompanies;
    private long totalDrives;
    private long activeDrives;
    private long totalApplications;
    private long totalSelected;
    private Map<String, Long> applicationsByStatus;
    private double placementPercentage; // (distinct students selected / total students) * 100
}
