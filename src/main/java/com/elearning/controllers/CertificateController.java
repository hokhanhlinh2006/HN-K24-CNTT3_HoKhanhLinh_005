package com.elearning.controllers;

import com.elearning.advice.ApiResponse;
import com.elearning.models.dto.CertificateLookupResponse;
import com.elearning.models.dto.IssueCertificateRequest;
import com.elearning.models.dto.RevokeCertificateRequest;
import com.elearning.models.entities.Certificate;
import com.elearning.models.services.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/certificates/lookup/{code}")
    public ResponseEntity<ApiResponse<CertificateLookupResponse>> lookupCertificate(@PathVariable String code) {
        CertificateLookupResponse response = certificateService.lookupCertificate(code);
        return ResponseEntity.ok(ApiResponse.success(response, "Tra cứu chứng chỉ thành công"));
    }

    @GetMapping("/certificates/my")
    public ResponseEntity<ApiResponse<List<CertificateLookupResponse>>> getMyCertificates(Authentication authentication) {
        String email = authentication.getName();
        List<CertificateLookupResponse> response = certificateService.getMyCertificates(email);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách chứng chỉ thành công"));
    }

    @PostMapping("/admin/certificates/issue")
    public ResponseEntity<ApiResponse<Certificate>> issueCertificate(@RequestBody IssueCertificateRequest request) {
        Certificate certificate = certificateService.issueCertificate(request);
        return ResponseEntity.ok(ApiResponse.success(certificate, "Cấp chứng chỉ thành công"));
    }

    @PostMapping("/admin/certificates/{code}/revoke")
    public ResponseEntity<ApiResponse<Certificate>> revokeCertificate(
            @PathVariable String code,
            @RequestBody(required = false) RevokeCertificateRequest request) {
        String reason = (request != null && request.getReason() != null) ? request.getReason() : "Học viên vi phạm quy chế";
        Certificate certificate = certificateService.revokeCertificate(code, reason);
        return ResponseEntity.ok(ApiResponse.success(certificate, "Thu hồi chứng chỉ thành công"));
    }
}
