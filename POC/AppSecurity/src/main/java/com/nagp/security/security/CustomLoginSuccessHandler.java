package com.nagp.security.security;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nagp.security.service.UserService;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
 
    @Autowired
    private UserService userService;
    
    @Value("${secret}")
    private String secret;
     
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
    		Authentication authentication) throws IOException, ServletException {
    	// TODO Auto-generated method stub
		User user = (User) authentication.getPrincipal();
       
        userService.resetFailedAttempts(user.getUsername());
        request.getSession().removeAttribute("error");
		Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
		String accessToken = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 60 * 100))
				.withIssuer(request.getRequestURL().toString())
				.withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(algorithm);
		
		String refreshToken = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 60))
				.withIssuer(request.getRequestURL().toString())
				.sign(algorithm);
		
		response.setHeader("access_token", accessToken);
		response.setHeader("refresh_token", refreshToken);

        super.onAuthenticationSuccess(request, response, authentication);    
    }
    
   
   
     
}