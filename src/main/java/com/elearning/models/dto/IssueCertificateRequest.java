package com.elearning.models.dto;

import lombok.Data;

@Data
public class IssueCertificateRequest {
    private Long userId;
    private Long courseId;
}
