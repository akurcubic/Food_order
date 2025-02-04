package com.raf.foodOrder.controller;

import com.raf.foodOrder.model.User;
import com.raf.foodOrder.security.CheckSecurity;
import com.raf.foodOrder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService){

        this.userService = userService;
    }

    @GetMapping
    @CheckSecurity(permissions = {"can_read_users"})
    public ResponseEntity<?> getAllUsers(){

        Optional<List<User>> users = userService.findAll();
        if(users.isPresent()){
            return ResponseEntity.ok(users);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @CheckSecurity(permissions = {"can_create_users"}, message = "You cannot create users")
    public ResponseEntity<?> createUser(@RequestBody User user) {

        try{
            Optional<User> newUser = userService.createUser(user);
            if(newUser.isPresent()) {
                return new ResponseEntity<>(newUser, HttpStatus.CREATED);
            }
            else{
                return new ResponseEntity<>("User with this e-mail already exist!", HttpStatus.CONFLICT);
            }

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{userId}")
    @CheckSecurity(permissions = {"can_update_users"})
    public ResponseEntity<User> updateUser(@PathVariable int userId, @RequestBody User updatedUser) {
        User updated = userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @CheckSecurity(permissions = {"can_delete_users"})
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable int id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }


    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(
            @PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }
}
