package com.nagp.security.controller;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.nagp.security.models.Role;
import com.nagp.security.models.User;
import com.nagp.security.models.UserDto;
import com.nagp.security.service.UserService;
import com.nagp.security.util.CaptchaUtil;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class UserController {
	
	
	@Value("${secret}")
	private String secret;
	
	@Autowired
	private UserService service;
	
	
	@GetMapping("/users")
	public ResponseEntity<User> getUsers(){
		return ResponseEntity.ok().body(new User(null, "Abhishek Kashyap", "abhishek", "1234", new ArrayList<>()));
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody UserDto dto, HttpServletRequest request){
		String captchaAnswer = (String) request.getSession().getAttribute("captcha_answer");
		if(!dto.getCaptcha().equals(captchaAnswer)) {
			log.error("Captcha Incorrect...!!!");
			throw new RuntimeException("Captcha Incorrect...!!!");
		}
		try {
			User user = User.fromDto(dto);
			

			
			service.addRoleToUser(user.getUsername(), "ROLE_USER");
			service.saveUser(user);
		}
		catch(DataIntegrityViolationException e) {
			
			log.error("Data Integrity Exception", e);
			throw new RuntimeException("A user with the passed Id already exists");
		}
		catch(Exception  e) {
			
			log.error("Error occured while creating user", e);
			throw new RuntimeException("User can't be created right now.");

		}
		return ResponseEntity.ok().build();
		
	}
	
	
	@PostMapping("/user/save")
	public ResponseEntity<User> saveUser(@Valid @RequestBody User user){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
		return ResponseEntity.created(uri).body(service.saveUser(user));
	} 
	
	@PostMapping("/role/save")
	public ResponseEntity<Role> saveRole(@RequestBody Role role){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
		return ResponseEntity.created(uri).body(service.saveRole(role));
	} 
	
	@PostMapping("/role/addToUser")
	public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form){
		service.addRoleToUser(form.getUsername(), form.getRoleName());
		return ResponseEntity.ok().build();
	} 
	
	@GetMapping("/token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			try {
				String refreshToken = authorizationHeader.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(refreshToken);
				String username = decodedJWT.getSubject();
				
				User user = service.getUser(username);
				
				
				String accessToken = JWT.create()
						.withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 60 * 100))
						.withIssuer(request.getRequestURL().toString())
						.withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
						.sign(algorithm);
				
				response.setHeader("access_token", accessToken);
				response.setHeader("refresh_token", refreshToken);
			} catch (Exception e) {
				log.error("Error logging in: {}", e.getMessage());
				response.setHeader("error", e.getMessage());
				response.sendError(403);
			}
		} else {
			throw new RuntimeException("Refresh Token is missing");
		}
	} 
	
	
	@Data
	class RoleToUserForm {
		private String username;
		private String roleName;
	}

}
