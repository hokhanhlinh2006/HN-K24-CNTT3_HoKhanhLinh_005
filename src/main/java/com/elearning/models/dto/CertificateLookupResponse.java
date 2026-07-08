package com.elearning.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateLookupResponse {
    private String certificateCode;
    private String studentName;
    private String studentEmail;
    private String courseTitle;
    private LocalDateTime issuedDate;
    private LocalDateTime expiryDate;
    private String status;
}
