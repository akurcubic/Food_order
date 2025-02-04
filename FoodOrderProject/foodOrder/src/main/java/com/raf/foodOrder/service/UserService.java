package com.raf.foodOrder.service;


import com.raf.foodOrder.configuration.ApplicationContextProvider;
import com.raf.foodOrder.model.CustomUserDetails;
import com.raf.foodOrder.model.Permission;
import com.raf.foodOrder.model.Role;
import com.raf.foodOrder.model.User;
import com.raf.foodOrder.repository.PermissionRepository;
import com.raf.foodOrder.repository.RoleRepository;
import com.raf.foodOrder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private PermissionRepository permissionRepository;
    private RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, PermissionRepository permissionRepository, RoleRepository roleRepository){

        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    public Optional<List<User>> findAll(){

        Optional<List<User>> users = Optional.of(userRepository.findAll());
        return users;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User myUser = this.userRepository.findByEmail(email);
        if (myUser == null)
            throw new UsernameNotFoundException("User with email: " + email + " not found.");

        Set<GrantedAuthority> authorities = myUser.getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toSet());

        return new CustomUserDetails(myUser.getId(), myUser.getEmail(), myUser.getPassword(), authorities, myUser.getRole().getRole());
    }

    public Optional<User> createUser(User user){

        User newUser;
        newUser =  userRepository.findByEmail(user.getEmail());

        if(newUser != null){
            return Optional.empty();
        }

        Set<Permission> permissions = new HashSet<>();
        user.getPermissions().stream()
                .map(Permission::getName)
                .forEach(permissionName -> {
                    Permission permission = permissionRepository.findByName(permissionName)
                            .orElseThrow(() -> new DataIntegrityViolationException("Permission '" + permissionName + "' not found."));
                    permissions.add(permission);
                });


        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);
        String hashedPassword = passwordEncoder.encode(user.getPassword());


        newUser = new User();

        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(hashedPassword);
        newUser.setPermissions(permissions);

        Role role = roleRepository.findByRole("CLIENT");

        newUser.setRole(role);

        userRepository.save(newUser);

        return Optional.of(newUser);

    }

    public User updateUser(int userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (updatedUser.getPermissions() != null) {
            /*Set<String> allowedPermissions = Set.of("can_create_users", "can_read_users", "can_update_users", "can_delete_users");
            if (!updatedUser.getPermissions().stream().allMatch(p -> allowedPermissions.contains(p.getName()))) {
                throw new IllegalArgumentException("Invalid permissions. Only 'can_create_users', 'can_read_users', 'can_update_users', and 'can_delete_users' are allowed.");
            }*/

            /*Set<Permission> permissions = new HashSet<>();
            for (String permissionName : updatedUser.getPermissions().stream().map(Permission::getName).toList()) {
                permissions.add(permissionRepository.findByName(permissionName)
                        .orElseThrow(() -> new DataIntegrityViolationException("Permission '" + permissionName + "' not found.")));
            }
            existingUser.setPermissions(permissions);*/

            Set<Permission> permissions = new HashSet<>();
            updatedUser.getPermissions().stream()
                    .map(Permission::getName)
                    .forEach(permissionName -> {
                        Permission permission = permissionRepository.findByName(permissionName)
                                .orElseThrow(() -> new DataIntegrityViolationException("Permission '" + permissionName + "' not found."));
                        permissions.add(permission);
                    });
            existingUser.setPermissions(permissions);

        }

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());

        return userRepository.save(existingUser);
    }

    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }

    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
