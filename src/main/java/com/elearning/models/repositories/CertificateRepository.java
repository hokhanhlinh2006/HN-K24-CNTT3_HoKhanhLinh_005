package com.elearning.models.repositories;

import com.elearning.models.entities.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByCertificateCode(String certificateCode);
    List<Certificate> findByUserEmail(String email);
    boolean existsByCertificateCode(String certificateCode);
}
