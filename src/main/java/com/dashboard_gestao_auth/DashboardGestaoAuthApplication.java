package com.dashboard_gestao_auth;

import com.dashboard_gestao_auth.domain.Role;
import com.dashboard_gestao_auth.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DashboardGestaoAuthApplication implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DashboardGestaoAuthApplication(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(DashboardGestaoAuthApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (roleRepository.findAll().size() < 2) {
            var adminRole = new Role();
            var basicRole = new Role();
            adminRole.setName("ADMIN");
            basicRole.setName("BASIC");
            roleRepository.save(adminRole);
            roleRepository.save(basicRole);
            System.out.println("Roles ADMIN e BASIC inseridas na base de dados.");
        }

    }

}
