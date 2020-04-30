package com.globalkinetic.assessment.service;

import com.globalkinetic.assessment.domain.Role;
import com.globalkinetic.assessment.domain.User;
import com.globalkinetic.assessment.dto.UserDTO;
import com.globalkinetic.assessment.repository.RoleRepository;
import com.globalkinetic.assessment.repository.UserRepository;
import com.globalkinetic.assessment.security.RolesConstants;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(final UserRepository userRepository,
                       final RoleRepository roleRepository,
                       final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(final UserDTO userDTO) {
        final User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhone(userDTO.getPhone());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }

        if (userDTO.getRoles() != null) {
            Set<Role> roles = userDTO.getRoles().stream()
                    .map(roleRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        userRepository.save(user);
        return user;
    }

    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.of(userRepository
                .findById(userDTO.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(user -> {
                    user.setLogin(userDTO.getLogin().toLowerCase());
                    user.setFirstName(userDTO.getFirstName());
                    user.setLastName(userDTO.getLastName());
                    user.setPhone(userDTO.getPhone());
                    if (userDTO.getEmail() != null) {
                        user.setEmail(userDTO.getEmail().toLowerCase());
                    }
                    if (StringUtils.isNotEmpty(userDTO.getPassword())) {
                        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                    }

                    Set<Role> currentUserRoles = user.getRoles();
                    currentUserRoles.clear();
                    userDTO.getRoles().stream()
                            .map(roleRepository::findById)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .forEach(currentUserRoles::add);
                    return user;
                })
                .map(UserDTO::new);
    }

    public void deleteUser(String login) {
        userRepository.findOneByLogin(login).ifPresent(userRepository::delete);
    }

    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByLoginNot(pageable, RolesConstants.ANONYMOUS).map(UserDTO::new);
    }
    public Optional<User> getUserWithRolesByLogin(String login) {
        return userRepository.findOneWithRolesByLogin(login);
    }
}

