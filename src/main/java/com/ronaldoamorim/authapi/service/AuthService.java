package com.ronaldoamorim.authapi.service;

import com.ronaldoamorim.authapi.domain.*;
import com.ronaldoamorim.authapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {


    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void register(RegisterDTO registerDTO) {

        Optional<User> userExist = this.userRepository.findByLogin(registerDTO.login());

        if(userExist.isPresent()) {
            throw new RuntimeException("User already exists");
        }
        User user = new User();

        user.setLogin(registerDTO.login());
        user.setPassword(passwordEncoder.encode(registerDTO.password()));
        user.setRole(registerDTO.role());

        this.userRepository.save(user);
    }

    public String login(LoginDTO loginDTO) {

        User user = userRepository.findByLogin(loginDTO.login())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials");
        }

        return tokenService.generateToken(user);
    }
}
