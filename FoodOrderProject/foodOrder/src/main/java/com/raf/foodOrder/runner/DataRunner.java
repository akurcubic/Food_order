package com.raf.foodOrder.runner;


import com.raf.foodOrder.model.Dish;
import com.raf.foodOrder.model.Permission;
import com.raf.foodOrder.model.Role;
import com.raf.foodOrder.model.User;
import com.raf.foodOrder.repository.DishRepository;
import com.raf.foodOrder.repository.PermissionRepository;
import com.raf.foodOrder.repository.RoleRepository;
import com.raf.foodOrder.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Profile({"default"})
@Component
public class DataRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final PermissionRepository permissionRepository;
    private final DishRepository dishRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public DataRunner(UserRepository userRepository, PasswordEncoder passwordEncoder, PermissionRepository permissionRepository, DishRepository dishRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
        this.dishRepository = dishRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        logger.info("DataRunner: Starting execution.");

        if (userRepository.findByEmail("admin@gmail.com") == null) {
            logger.info("DataRunner: Creating 'admin' user.");
            User user = new User();

            Role admin = new Role();
            admin.setRole("ADMIN");;

            Role client = new Role();
            client.setRole("CLIENT");

            roleRepository.save(admin);
            roleRepository.save(client);

            user.setEmail("admin@gmail.com");

            user.setFirstName("admin");
            user.setLastName("adminovic");

            String hashedPassword = passwordEncoder.encode("admin");
            user.setPassword(hashedPassword);

            Permission one = new Permission("can_create_users");
            Permission two = new Permission("can_read_users");
            Permission three = new Permission("can_update_users");
            Permission four = new Permission("can_delete_users");
            Permission five = new Permission("can_search_order");
            Permission six = new Permission("can_place_order");
            Permission seven = new Permission("can_cancel_order");
            Permission eight = new Permission("can_track_order");
            Permission nine = new Permission("can_schedule_order");

            permissionRepository.save(one);
            permissionRepository.save(two);
            permissionRepository.save(three);
            permissionRepository.save(four);
            permissionRepository.save(five);
            permissionRepository.save(six);
            permissionRepository.save(seven);
            permissionRepository.save(eight);
            permissionRepository.save(nine);

            Set<Permission> permissions = new HashSet<>();
            permissions.add(one);
            permissions.add(two);
            permissions.add(three);
            permissions.add(four);
            permissions.add(five);
            permissions.add(six);
            permissions.add(seven);
            permissions.add(eight);
            permissions.add(nine);

            Dish burger = new Dish("Burger");
            Dish pizza = new Dish("Pizza");
            Dish pasta = new Dish("Pasta");
            Dish sandwich = new Dish("Sandwich");
            Dish hotDog = new Dish("Hot dog");

            dishRepository.save(burger);
            dishRepository.save(pizza);
            dishRepository.save(pasta);
            dishRepository.save(sandwich);
            dishRepository.save(hotDog);

            user.setPermissions(permissions);

            user.setRole(admin);

            userRepository.save(user);
            logger.info("DataRunner: 'admin' user saved successfully.");
        } else {
            logger.info("DataRunner: 'admin' user already exists.");
        }
    }
}

