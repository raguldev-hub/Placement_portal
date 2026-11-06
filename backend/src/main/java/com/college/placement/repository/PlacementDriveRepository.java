package com.college.placement.repository;

import com.college.placement.entity.DriveStatus;
import com.college.placement.entity.PlacementDrive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlacementDriveRepository extends JpaRepository<PlacementDrive, Long> {
    List<PlacementDrive> findByCompanyId(Long companyId);
    List<PlacementDrive> findByStatus(DriveStatus status);
}
