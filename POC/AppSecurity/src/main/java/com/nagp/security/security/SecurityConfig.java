package com.nagp.security.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nagp.security.filter.CustomAuthenticationFilter;
import com.nagp.security.filter.CustomAuthorizationFilter;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity

public class SecurityConfig extends WebSecurityConfigurerAdapter{
	@Value("${secret}")
	private String secret;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	
	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
		
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		CustomAuthenticationFilter authFilter = new CustomAuthenticationFilter(super.authenticationManager(), secret);
		
		http.formLogin()
		.successHandler(loginSuccessHandler)
		.failureHandler(loginFailureHandler)
		.defaultSuccessUrl("/ui/welcome", true)
		.loginProcessingUrl("/api/login");
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authorizeRequests().antMatchers( "/ui/register**", "/ui/login**", "/api/register**", "/api/login**", "/api/token/refresh**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/products/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/products/**").hasAuthority("ROLE_ADMIN");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/user/**").hasAuthority("ROLE_USER");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/ui/welcome/**").hasAuthority("ROLE_USER");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/user/save/**").hasAuthority("ROLE_ADMIN");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/role/save/**").hasAuthority("ROLE_ADMIN");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/seller/**").hasAuthority("ROLE_SELLER");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/admin/**").hasAuthority("ROLE_ADMIN");
		http.authorizeRequests().anyRequest().authenticated();
		http.addFilter(authFilter);
		http.addFilterBefore(new CustomAuthorizationFilter(secret), UsernamePasswordAuthenticationFilter.class);
		
		
	}
	
	
	
	
	@Autowired
    private CustomLoginFailureHandler loginFailureHandler;
     
    @Autowired
    private CustomLoginSuccessHandler loginSuccessHandler;
	

}
