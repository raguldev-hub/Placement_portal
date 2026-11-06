package com.college.placement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibilityResponse {
    private Long driveId;
    private Long studentId;
    private boolean eligible;
    private String reason;
    // Individual criteria breakdown, useful for showing students exactly why
    private boolean cgpaOk;
    private boolean branchOk;
    private boolean backlogsOk;
}
