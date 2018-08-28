package com.yar.microservices.uaa.bootstarp;

import com.yar.microservices.uaa.domain.Authority;
import com.yar.microservices.uaa.domain.User;
import com.yar.microservices.uaa.repository.AuthorityRepository;
import com.yar.microservices.uaa.repository.UserRepository;
import com.yar.microservices.uaa.security.SecurityAuthority;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;


@Component
public class UserLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;


    @Value(value = "${spring.security.user.name}")
    private String adminUsername;

    @Value(value = "${spring.security.user.password}")
    private String adminPassword;

    @Value(value = "${spring.security.user.roles}")
    private String adminRole;

    public UserLoader(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        checkSecurityAuthorities();

        adminUserLoader();

    }


    private void adminUserLoader() {

        // check or add administrator user to db

        Optional<User> adminUser = userRepository.findOneByLogin(adminUsername);
        Optional<Authority> authority = authorityRepository.findById(this.adminRole);
        if (!adminUser.isPresent()) {
            User admin = new User();
            admin.setLogin(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setActivated(true);

            admin.setFirstName("administrator");
            admin.setLastName("stts");
            admin.setEmail("ehsanyar2012@gmail.com");
            admin.setCreatedBy(adminUsername);
            admin.setCreatedDate(Instant.now());

            if (authority.isPresent()) {
                admin.getAuthorities().add(authority.get());
            } else {
                Authority adminAuthority = new Authority();
                adminAuthority.setName(this.adminRole);
                authorityRepository.save(adminAuthority);
            }
            userRepository.saveAndFlush(admin);
        }
    }


    private void checkSecurityAuthorities() {
        // check or add security roles to db
        checkOrPersistSecurityRole(SecurityAuthority.ADMIN);
        checkOrPersistSecurityRole(SecurityAuthority.MANAGER);
        checkOrPersistSecurityRole(SecurityAuthority.USER);
        checkOrPersistSecurityRole(SecurityAuthority.ANONYMOUS);
    }

    private void checkOrPersistSecurityRole(SecurityAuthority securityAuthority) {
        Optional<Authority> authority = authorityRepository.findById(securityAuthority.getAuthority());
        if (!authority.isPresent()) {
            Authority newAuthority = new Authority();
            newAuthority.setName(securityAuthority.getAuthority());
            authorityRepository.save(newAuthority);
        }
    }
}
