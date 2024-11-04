package com.teamlixy.teamlixy.service;

import com.teamlixy.teamlixy.controller.UsernameAlreadyExistsException;
import com.teamlixy.teamlixy.model.User;
import com.teamlixy.teamlixy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username already taken.");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        Set<String> roles = new HashSet<>();
        roles.add("USER");
        user.setRoles(roles);

        userRepository.save(user);
    }

    public void saveUser(User user) {
        User existingUser = userRepository.findByUsername(user.getUsername())
                .orElse(null);
        if (existingUser == null || !existingUser.getPassword().equals(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userRepository.save(user);
    }


    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user = userRepository.findByUsername(username)
               .orElseThrow(() -> new UsernameNotFoundException("User not found"));

       return org.springframework.security.core.userdetails.User.builder()
               .username(user.getUsername())
               .password(user.getPassword())
               .authorities(getAuthorities(user))
               .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public void updateUserProfile(User user) {
        User existingUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Update fields without modifying the password
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());

        userRepository.save(existingUser);
    }

    public void  resetPassword(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if(userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        User user = userOptional.get();

        String tempPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));

        //save the updated user with the new password
        userRepository.save(user);

        //ideally, send this password via email
        System.out.println("Temporary password for "+ username + ":" + tempPassword);
    }

    private String generateTemporaryPassword(){
        //generate a random 8-character password
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }
}
