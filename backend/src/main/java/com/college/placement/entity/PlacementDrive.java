package com.college.placement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "placement_drives")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacementDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "job_role", nullable = false, length = 120)
    private String jobRole;

    @Column(length = 3000)
    private String description;

    @Column(name = "package_lpa", nullable = false)
    private Double packageLpa;

    @Column(nullable = false, length = 100)
    private String location;

    // Eligibility criteria
    @Column(name = "min_cgpa", nullable = false)
    private Double minCgpa;

    @Column(name = "max_backlogs", nullable = false)
    private Integer maxBacklogs;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "drive_allowed_branches", joinColumns = @JoinColumn(name = "drive_id"))
    @Column(name = "branch", length = 50)
    private Set<String> allowedBranches = new HashSet<>();

    @Column(name = "application_deadline", nullable = false)
    private LocalDate applicationDeadline;

    @Column(name = "drive_date")
    private LocalDate driveDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DriveStatus status = DriveStatus.UPCOMING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
