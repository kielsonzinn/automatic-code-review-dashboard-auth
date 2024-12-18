package com.dashboard_gestao_auth.controller;

import com.dashboard_gestao_auth.domain.Role;
import com.dashboard_gestao_auth.dto.LoginRequest;
import com.dashboard_gestao_auth.dto.LoginResponse;
import com.dashboard_gestao_auth.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
public class TokenController {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final JwtEncoder jwtEncoder;

    public TokenController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest dto) {

        var user = userRepository.findByUsername(dto.username());
        if (user.isEmpty() || !bCryptPasswordEncoder.matches(dto.password(), user.get().getPassword())) {
            throw new BadCredentialsException("username or password invalid");
        }

        var now = Instant.now();
        var expiresIn = 300L;

        var scopes = user.get().getRoles().stream().map(Role::getName).collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder().issuer("com.dashboard_gestao_auth").subject(user.get().getUserId().toString()).issuedAt(now).expiresAt(now.plusSeconds(expiresIn)).claim("scope", scopes).build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        var loginResponse = new LoginResponse(jwtValue, expiresIn);
        return ResponseEntity.ok(loginResponse);
    }

}
