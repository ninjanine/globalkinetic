package com.globalkinetic.assessment.controllers;

import com.globalkinetic.assessment.controllers.viewmodel.LoginVM;
import com.globalkinetic.assessment.dto.UserDTO;
import com.globalkinetic.assessment.security.AuthenticationUtils;
import com.globalkinetic.assessment.security.jwt.JWTFilter;
import com.globalkinetic.assessment.security.jwt.JWTTokenProvider;
import com.globalkinetic.assessment.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JWTTokenProvider tokenProvider;
    private final UserService userService;
    @Autowired
    private SessionRegistry sessionRegistry;

    public LoginController(final JWTTokenProvider tokenProvider,
                           final AuthenticationManagerBuilder authenticationManagerBuilder,
                           final UserService userService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody LoginVM loginVM) throws Exception {

        final UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());
        final Authentication authentication
                = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = tokenProvider.createToken(authentication);
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + token);
        final Optional<UserDTO> userDTO = userService.getUserWithRolesByLogin(loginVM.getUsername()).map(UserDTO::new);

        if (userDTO.isPresent()) {
            userDTO.get().setToken(token);
            userDTO.get().setPassword("");
            return new ResponseEntity<>(userDTO.get(), httpHeaders, HttpStatus.OK);
        } else {
            UserDTO newUserDTO = new UserDTO();
            newUserDTO.setFirstName(loginVM.getUsername());
            newUserDTO.setToken(token);
            return new ResponseEntity<>(newUserDTO, httpHeaders, HttpStatus.OK);
        }

    }

    @GetMapping("/logout/{login}")
    public void logout(@PathVariable String login) {
        if (AuthenticationUtils.isLoggedInUser(login)) {
            sessionRegistry.removeSessionInformation(login);
        }
    }

}
