package com.dashboard_gestao_auth.controller;

import com.dashboard_gestao_auth.domain.User;
import com.dashboard_gestao_auth.dto.CreateUserDTO;
import com.dashboard_gestao_auth.repository.RoleRepository;
import com.dashboard_gestao_auth.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody CreateUserDTO createUserDTO) {
        var basicRole = roleRepository.findByName("BASIC");
        var user = userRepository.findByUsername(createUserDTO.username());

        if (user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        var novoUser = new User();
        novoUser.setUsername(createUserDTO.username());
        novoUser.setPassword(this.bCryptPasswordEncoder.encode(createUserDTO.password()));
        novoUser.setRoles(Set.of(basicRole));

        userRepository.save(novoUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_BASIC')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(this.userRepository.findAll());
    }


    @GetMapping("/me")
    @PreAuthorize("hasAuthority('SCOPE_BASIC')")
    public ResponseEntity<User> me() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }

        User user = new User();
        user.setUsername(this.userRepository.findByUserId(UUID.fromString(jwt.getSubject())).get().getUsername());

        return ResponseEntity.ok(user);

    }

}
