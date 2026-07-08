package com.elearning.models.services;

import com.elearning.exceptions.BusinessException;
import com.elearning.exceptions.CertificateExpiredException;
import com.elearning.exceptions.CertificateRevokedException;
import com.elearning.models.dto.CertificateLookupResponse;
import com.elearning.models.dto.IssueCertificateRequest;
import com.elearning.models.entities.Certificate;
import com.elearning.models.entities.Course;
import com.elearning.models.entities.User;
import com.elearning.models.repositories.CertificateRepository;
import com.elearning.models.repositories.CourseRepository;
import com.elearning.models.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Certificate issueCertificate(IssueCertificateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(404, "Student not found with ID: " + request.getUserId()));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new BusinessException(404, "Course not found with ID: " + request.getCourseId()));

        // Generate unique certificate code
        String code;
        do {
            code = "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (certificateRepository.existsByCertificateCode(code));

        Certificate certificate = new Certificate();
        certificate.setCertificateCode(code);
        certificate.setUser(user);
        certificate.setCourse(course);
        certificate.setIssuedDate(LocalDateTime.now());
        certificate.setIsRevoked(false);

        return certificateRepository.save(certificate);
    }

    @Transactional
    public Certificate revokeCertificate(String code, String reason) {
        Certificate certificate = certificateRepository.findByCertificateCode(code)
                .orElseThrow(() -> new BusinessException(404, "Không tìm thấy chứng chỉ với mã đã cho"));

        certificate.setIsRevoked(true);
        certificate.setRevokedReason(reason);
        certificate.setRevokedAt(LocalDateTime.now());

        return certificateRepository.save(certificate);
    }

    @Transactional(readOnly = true)
    public CertificateLookupResponse lookupCertificate(String code) {
        Certificate certificate = certificateRepository.findByCertificateCode(code)
                .orElseThrow(() -> new BusinessException(404, "Không tìm thấy chứng chỉ với mã đã nhập."));

        if (Boolean.TRUE.equals(certificate.getIsRevoked())) {
            throw new CertificateRevokedException("Chứng chỉ không hợp lệ do vi phạm!");
        }

        LocalDateTime expiryDate = certificate.getIssuedDate().plusYears(1);
        if (LocalDateTime.now().isAfter(expiryDate)) {
            throw new CertificateExpiredException("Đã hết hạn");
        }

        return CertificateLookupResponse.builder()
                .certificateCode(certificate.getCertificateCode())
                .studentName(certificate.getUser().getFullName())
                .studentEmail(certificate.getUser().getEmail())
                .courseTitle(certificate.getCourse().getTitle())
                .issuedDate(certificate.getIssuedDate())
                .expiryDate(expiryDate)
                .status("Hợp lệ")
                .build();
    }

    @Transactional(readOnly = true)
    public List<CertificateLookupResponse> getMyCertificates(String email) {
        List<Certificate> certificates = certificateRepository.findByUserEmail(email);

        return certificates.stream().map(cert -> {
            LocalDateTime expiryDate = cert.getIssuedDate().plusYears(1);
            String status;
            if (Boolean.TRUE.equals(cert.getIsRevoked())) {
                status = "Chứng chỉ không hợp lệ do vi phạm!";
            } else if (LocalDateTime.now().isAfter(expiryDate)) {
                status = "Đã hết hạn";
            } else {
                status = "Hợp lệ";
            }

            return CertificateLookupResponse.builder()
                    .certificateCode(cert.getCertificateCode())
                    .studentName(cert.getUser().getFullName())
                    .studentEmail(cert.getUser().getEmail())
                    .courseTitle(cert.getCourse().getTitle())
                    .issuedDate(cert.getIssuedDate())
                    .expiryDate(expiryDate)
                    .status(status)
                    .build();
        }).collect(Collectors.toList());
    }
}
