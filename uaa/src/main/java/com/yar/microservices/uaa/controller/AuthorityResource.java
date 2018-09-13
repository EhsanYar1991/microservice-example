package com.yar.microservices.uaa.controller;


import com.yar.microservices.uaa.bundle.MessageCodes;
import com.yar.microservices.uaa.bundle.MessageService;
import com.yar.microservices.uaa.domain.Authority;
import com.yar.microservices.uaa.domain.User;
import com.yar.microservices.uaa.dto.AuthorityDTO;
import com.yar.microservices.uaa.dto.GeneralResponse;
import com.yar.microservices.uaa.dto.UserAuthoritiesDTO;
import com.yar.microservices.uaa.dto.UserListDTO;
import com.yar.microservices.uaa.repository.AuthorityRepository;
import com.yar.microservices.uaa.repository.UserRepository;
import com.yar.microservices.uaa.security.AuthoritiesConstants;
import com.yar.microservices.uaa.service.mapper.UserMapper;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/")
public class AuthorityResource {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final UserMapper userMapper;
    private final MessageService messageService;


    public AuthorityResource(UserRepository userRepository, AuthorityRepository authorityRepository, UserMapper userMapper, MessageService messageService) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.userMapper = userMapper;
        this.messageService = messageService;
    }


    @GetMapping("addAuthority/{authority}")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<?> addAuthority(@PathVariable("authority") String authority) {

        Optional<Authority> byId = authorityRepository.findById(authority);
        if (byId.isPresent()) {
            AuthorityDTO authorityDTO = new AuthorityDTO(authority);
            authorityDTO.setActionCode(MessageCodes.AUTHORITY_IS_EXIST);
            authorityDTO.setActionMessage(messageService.getMessage(MessageCodes.AUTHORITY_IS_EXIST));
            return new ResponseEntity<>(authorityDTO, HttpStatus.OK);
        }

        Authority newAuthority = new Authority();
        newAuthority.setName(authority);

        authorityRepository.save(newAuthority);

        AuthorityDTO authorityDTO = new AuthorityDTO(authority);
        authorityDTO.setActionCode(MessageCodes.PROCESS_SUCCESSFUL);
        authorityDTO.setActionMessage(messageService.getMessage(MessageCodes.PROCESS_SUCCESSFUL));

        return ResponseEntity.ok(authorityDTO);
    }

    @DeleteMapping("deleteAuthority/{authority}")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<?> deleteAuthority(@PathVariable("authority") String authority) {

        Optional<Authority> byId = authorityRepository.findById(authority);
        if (!byId.isPresent()) {

            GeneralResponse response = new GeneralResponse();
            response.setActionCode(MessageCodes.AUTHORITY_IS_NOT_EXIST);
            response.setActionMessage(messageService.getMessage(MessageCodes.AUTHORITY_IS_NOT_EXIST));

            return ResponseEntity.ok(response);
        }

        authorityRepository.delete(byId.get());

        GeneralResponse response = new GeneralResponse();
        response.setActionCode(MessageCodes.PROCESS_SUCCESSFUL);
        response.setActionMessage(messageService.getMessage(MessageCodes.PROCESS_SUCCESSFUL));

        return ResponseEntity.ok(response);
    }


    @GetMapping("getUsersByAuthority/{authority}")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<?> getAllUsersByAuthority(@PathVariable("authority") String authority) {

        Optional<Authority> authorityById = authorityRepository.findById(authority);
        if (!authorityById.isPresent()) {

            GeneralResponse response = new GeneralResponse();
            response.setActionCode(MessageCodes.AUTHORITY_IS_NOT_EXIST);
            response.setActionMessage(messageService.getMessage(MessageCodes.AUTHORITY_IS_NOT_EXIST));

            return ResponseEntity.ok(response);
        }

        List<User> allByAuthoritiesContains = userRepository.findAllByAuthoritiesContains(authorityById.get());
        if (allByAuthoritiesContains.isEmpty()) {
            GeneralResponse response = new GeneralResponse();
            response.setActionCode(MessageCodes.NO_USER_FOUND);
            response.setActionMessage(messageService.getMessage(MessageCodes.NO_USER_FOUND));

            return ResponseEntity.ok(response);
        }

        Hibernate.initialize(allByAuthoritiesContains);

        UserListDTO userListDTO = new UserListDTO();
        userListDTO.setUserDTOS(userMapper.usersToUserDTOs(allByAuthoritiesContains));
        userListDTO.setActionCode(MessageCodes.PROCESS_SUCCESSFUL);
        userListDTO.setActionMessage(messageService.getMessage(MessageCodes.PROCESS_SUCCESSFUL));

        return ResponseEntity.ok(userListDTO);
    }

    @GetMapping("getUserAuthoritiesByUserId/{userId}")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<?> getUserAuthoritiesByUserId(@PathVariable("userId") Long userId) {

        Optional<User> userById = userRepository.findById(userId);
        if (!userById.isPresent()) {
            GeneralResponse response = new GeneralResponse();
            response.setActionCode(MessageCodes.NO_USER_FOUND);
            response.setActionMessage(messageService.getMessage(MessageCodes.NO_USER_FOUND));
            return ResponseEntity.ok(response);
        }

        return returnUserAuthoritiesResponse(userById.get());
    }


    @GetMapping("getUserAuthoritiesByLogin/{login}")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<?> getUserAuthoritiesByLogin(@PathVariable("login") String login) {

        Optional<User> userById = userRepository.findOneByLogin(login.toLowerCase());
        if (!userById.isPresent()) {
            GeneralResponse response = new GeneralResponse();
            response.setActionCode(MessageCodes.NO_USER_FOUND);
            response.setActionMessage(messageService.getMessage(MessageCodes.NO_USER_FOUND));
            return ResponseEntity.ok(response);
        }

        return returnUserAuthoritiesResponse(userById.get());
    }


    private ResponseEntity<?> returnUserAuthoritiesResponse(User user) {
        UserAuthoritiesDTO userAuthoritiesDTO = new UserAuthoritiesDTO();
        userAuthoritiesDTO.setAuthorityDTOs(userMapper.authoritiesToAuthoritiesValueSet(user.getAuthorities()));
        userAuthoritiesDTO.setActionCode(MessageCodes.PROCESS_SUCCESSFUL);
        userAuthoritiesDTO.setActionMessage(messageService.getMessage(MessageCodes.PROCESS_SUCCESSFUL));

        return ResponseEntity.ok(userAuthoritiesDTO);
    }

}
