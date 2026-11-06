package com.college.placement.service;

import com.college.placement.dto.request.LoginRequest;
import com.college.placement.dto.request.RegisterCompanyRequest;
import com.college.placement.dto.request.RegisterStudentRequest;
import com.college.placement.dto.response.AuthResponse;
import com.college.placement.entity.*;
import com.college.placement.exception.DuplicateResourceException;
import com.college.placement.repository.CompanyRepository;
import com.college.placement.repository.StudentRepository;
import com.college.placement.repository.UserRepository;
import com.college.placement.security.JwtUtil;
import com.college.placement.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse registerStudent(RegisterStudentRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .enabled(true)
                .build();
        user = userRepository.save(user);

        Student student = Student.builder()
                .user(user)
                .fullName(request.getFullName())
                .department(request.getDepartment())
                .cgpa(request.getCgpa())
                .backlogs(request.getBacklogs())
                .phone(request.getPhone())
                .graduationYear(request.getGraduationYear())
                .build();
        studentRepository.save(student);

        String token = jwtUtil.generateToken(new UserPrincipal(user));

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .name(student.getFullName())
                .build();
    }

    @Transactional
    public AuthResponse registerCompany(RegisterCompanyRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.COMPANY)
                .enabled(true)
                .build();
        user = userRepository.save(user);

        Company company = Company.builder()
                .user(user)
                .companyName(request.getCompanyName())
                .description(request.getDescription())
                .website(request.getWebsite())
                .industry(request.getIndustry())
                .build();
        companyRepository.save(company);

        String token = jwtUtil.generateToken(new UserPrincipal(user));

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .name(company.getCompanyName())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User vanished after authentication"));

        String token = jwtUtil.generateToken(new UserPrincipal(user));

        String displayName = switch (user.getRole()) {
            case STUDENT -> studentRepository.findByUserId(user.getId())
                    .map(Student::getFullName).orElse(user.getEmail());
            case COMPANY -> companyRepository.findByUserId(user.getId())
                    .map(Company::getCompanyName).orElse(user.getEmail());
            case ADMIN -> "Admin";
        };

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .name(displayName)
                .build();
    }
}
