package com.globalkinetic.assessment.controllers;

import com.globalkinetic.assessment.domain.User;
import com.globalkinetic.assessment.dto.UserDTO;
import com.globalkinetic.assessment.repository.UserRepository;
import com.globalkinetic.assessment.security.AuthenticationUtils;
import com.globalkinetic.assessment.security.RolesConstants;
import com.globalkinetic.assessment.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    private SessionRegistry sessionRegistry;

    public UserController(final UserService userService,
                          final UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers(Pageable pageable) {
        if (AuthenticationUtils.isCurrentUserAnAdmin()) {
            final Page<UserDTO> page = userService.getAllManagedUsers(pageable);
            page.stream().forEach(dto -> dto.setPassword(""));
            return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/users/{login}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String login) {
        if (AuthenticationUtils.isCurrentUserAnAdmin()
                || AuthenticationUtils.isLoggedInUser(login)){
            final Optional<UserDTO> userDTO = userService.getUserWithRolesByLogin(login).map(UserDTO::new);
            return userDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK)).orElseGet(() -> ResponseEntity.noContent().build());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }


    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) throws URISyntaxException {
        if (userDTO.getId() == null
            && !userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()
            && !userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
                final Set<String> defaultRole = new HashSet<>();
                defaultRole.add(RolesConstants.USER);
                userDTO.setRoles(defaultRole);
                final User newUser = userService .createUser(userDTO);
                return ResponseEntity
                        .created(new URI("/api/users/" + newUser.getLogin()))
                        .body(newUser);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping("/users")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO) {
        if (AuthenticationUtils.isCurrentUserAnAdmin()
                || AuthenticationUtils.isLoggedInUser(userDTO.getLogin())) {
            userService.updateUser(userDTO);

            return ResponseEntity.ok(userDTO);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @DeleteMapping("/users/{login}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable String login) {
        if (AuthenticationUtils.isCurrentUserAnAdmin()) {
            userService.deleteUser(login);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/loggedUsers")
    public ResponseEntity<List<Object>> listLoggedInUsers() {
        final List<Object> allPrincipals = sessionRegistry.getAllPrincipals();

        return ResponseEntity.ok(allPrincipals);
    }
}
