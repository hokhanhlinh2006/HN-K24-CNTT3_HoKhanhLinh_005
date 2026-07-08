package com.elearning;

import com.elearning.models.entities.Certificate;
import com.elearning.models.entities.Course;
import com.elearning.models.entities.User;
import com.elearning.models.repositories.CertificateRepository;
import com.elearning.models.repositories.CourseRepository;
import com.elearning.models.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CertificateRepository certificateRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("====== STARTING DATABASE SEEDING ======");

        // Seed Admin User
        User admin = userRepository.findByEmail("admin@gmail.com").orElse(null);
        if (admin == null) {
            admin = new User();
            admin.setFullName("System Admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("123456789"));
            admin.setRole("ADMIN");
            admin = userRepository.save(admin);
            System.out.println("Seeded admin: admin@gmail.com / 123456789 (ADMIN)");
        }

        // Seed Student User
        User student = userRepository.findByEmail("student@gmail.com").orElse(null);
        if (student == null) {
            student = new User();
            student.setFullName("Nguyen Van Student");
            student.setEmail("student@gmail.com");
            student.setPassword(passwordEncoder.encode("123456789"));
            student.setRole("STUDENT");
            student = userRepository.save(student);
            System.out.println("Seeded student: student@gmail.com / 123456789 (STUDENT)");
        }

        // Seed Courses
        Course course1 = null;
        Course course2 = null;
        Course course3 = null;

        if (courseRepository.count() == 0) {
            course1 = new Course();
            course1.setTitle("Spring Boot Professional Certificate");
            course1.setDescription("Premium Spring Boot course");
            course1.setInstructor(admin);
            course1 = courseRepository.save(course1);

            course2 = new Course();
            course2.setTitle("Docker & Kubernetes Masterclass");
            course2.setDescription("Learn containerization");
            course2.setInstructor(admin);
            course2 = courseRepository.save(course2);

            course3 = new Course();
            course3.setTitle("Mastering Java 17");
            course3.setDescription("Advanced Java techniques");
            course3.setInstructor(admin);
            course3 = courseRepository.save(course3);

            System.out.println("Seeded 3 courses successfully.");
        } else {
            // Find existing courses if any
            var courses = courseRepository.findAll();
            if (courses.size() >= 3) {
                course1 = courses.get(0);
                course2 = courses.get(1);
                course3 = courses.get(2);
            }
        }

        // Seed Certificates
        if (certificateRepository.count() == 0 && student != null && course1 != null) {
            // 1. Valid certificate
            Certificate validCert = new Certificate();
            validCert.setCertificateCode("CERT-VALID-001");
            validCert.setUser(student);
            validCert.setCourse(course1);
            validCert.setIssuedDate(LocalDateTime.now()); // Issued now, expires in 1 year
            validCert.setIsRevoked(false);
            certificateRepository.save(validCert);
            System.out.println("Seeded valid certificate: CERT-VALID-001");

            // 2. Expired certificate
            if (course2 != null) {
                Certificate expiredCert = new Certificate();
                expiredCert.setCertificateCode("CERT-EXPIRED-002");
                expiredCert.setUser(student);
                expiredCert.setCourse(course2);
                // Issued 1 year and 5 days ago, expired by 5 days
                expiredCert.setIssuedDate(LocalDateTime.now().minusYears(1).minusDays(5));
                expiredCert.setIsRevoked(false);
                certificateRepository.save(expiredCert);
                System.out.println("Seeded expired certificate: CERT-EXPIRED-002");
            }

            // 3. Revoked certificate
            if (course3 != null) {
                Certificate revokedCert = new Certificate();
                revokedCert.setCertificateCode("CERT-REVOKED-003");
                revokedCert.setUser(student);
                revokedCert.setCourse(course3);
                revokedCert.setIssuedDate(LocalDateTime.now().minusMonths(6));
                revokedCert.setIsRevoked(true);
                revokedCert.setRevokedReason("Cheating detected in exam");
                revokedCert.setRevokedAt(LocalDateTime.now().minusMonths(3));
                certificateRepository.save(revokedCert);
                System.out.println("Seeded revoked certificate: CERT-REVOKED-003");
            }
        }

        System.out.println("====== DATABASE SEEDING COMPLETED ======");
    }
}
