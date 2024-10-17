package com.example.onlinefoodordering.controller;


import com.example.onlinefoodordering.config.JwtProvider;
import com.example.onlinefoodordering.model.Cart;
import com.example.onlinefoodordering.model.USER_ROLE;
import com.example.onlinefoodordering.model.User;
import com.example.onlinefoodordering.repository.CartRepository;
import com.example.onlinefoodordering.repository.UserRepository;
import com.example.onlinefoodordering.request.LoginRequest;
import com.example.onlinefoodordering.response.AuthResponse;
import com.example.onlinefoodordering.service.CustomerUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/auth")
public class AuthController {

     @Autowired
     private UserRepository userRepository;
     @Autowired
     private PasswordEncoder passwordEncoder;
     @Autowired
     private JwtProvider jwtProvider;
     @Autowired
     private CustomerUserDetailsService userDetailsService;
     @Autowired
     private CartRepository cartRepository;
     @Autowired
     private CustomerUserDetailsService customerUserDetailsService;


     @PostMapping("/signup")
     public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {
          User isEmail = userRepository.findByEmail(user.getEmail());
          if (isEmail != null){
               throw new Exception("Email is already user with another account");
          }

          User createdUser = new User();
          createdUser.setEmail(user.getEmail());
          createdUser.setUsername(user.getUsername());
          createdUser.setUserRole(user.getUserRole());
          createdUser.setPassword(passwordEncoder.encode(user.getPassword()));

          User savedUser = userRepository.save(createdUser);

          Cart cart = new Cart();
          cart.setCustomer(savedUser);
          cartRepository.save(cart);

          Authentication authentication = new UsernamePasswordAuthenticationToken(
                  user.getEmail(),
                  user.getPassword());
          SecurityContextHolder.getContext().setAuthentication(authentication);

          String jwt = jwtProvider.generateToken(authentication);

          AuthResponse authResponse = new AuthResponse();
          authResponse.setMessage("Register success");
          authResponse.setJwt(jwt);
          authResponse.setRole(savedUser.getUserRole());
          return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
     }


     @PostMapping("/signIn")
     public ResponseEntity<AuthResponse> signIn(@RequestBody LoginRequest request){
          String username = request.getEmail();
          String password = request.getPassword();

          Authentication authentication = authenticate(username, password);
          Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
          String role = authorities.isEmpty() ? null:authorities.iterator().next().getAuthority();

          String jwt = jwtProvider.generateToken(authentication);

          AuthResponse authResponse = new AuthResponse();
          authResponse.setJwt(jwt);
          authResponse.setMessage("Login success");
          authResponse.setRole(USER_ROLE.valueOf(role));

          return new ResponseEntity<>(authResponse, HttpStatus.OK);

     }

     private Authentication authenticate(String username, String password) {

          UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);

          if (userDetails==null){
               throw new BadCredentialsException("Invalid username...");
          }

          if (!passwordEncoder.matches(password, userDetails.getPassword())){
               throw new BadCredentialsException("Invalid password...");
          }
          return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
     }


}
