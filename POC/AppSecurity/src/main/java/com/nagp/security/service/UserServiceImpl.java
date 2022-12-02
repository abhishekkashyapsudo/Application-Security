package com.nagp.security.service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.naming.AuthenticationException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nagp.security.models.Role;
import com.nagp.security.models.User;
import com.nagp.security.repo.RoleRepo;
import com.nagp.security.repo.UserRepo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private RoleRepo roleRepo;
	
	@Autowired
	private final PasswordEncoder passwordEncoder;
	
	public static final int MAX_FAILED_ATTEMPTS = 3;
    
    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours

    
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException  {
		User user = getUser(username);
		
		if(user == null) {
			log.error("User {} not found in the database", username);
			throw new UsernameNotFoundException(username);
		}
		else if(!user.isAccountNonLocked() && !unlockWhenTimeExpired(user)) {
			log.error("User {} account is locked currently", username);
			throw new UsernameNotFoundException("Your account is locked currently");
		}
	
		log.info("User {} found in the database", username);

		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(role -> 
			authorities.add(new SimpleGrantedAuthority(role.getName()))
		);
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
	}

	@Override
	public User saveUser(User user) {

		log.info("Saving new user {} to db", user.getName());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepo.save(user);
	}

	@Override
	public Role saveRole(Role role) {

		log.info("Saving new role {} to db", role.getName());
		return roleRepo.save(role);
	}

	@Override
	public void addRoleToUser(String username, String roleName) {
		User user = userRepo.findByUsername(username);
		Role role = roleRepo.findByName(roleName);
		log.info("Associating role {} to the user {}", username, roleName);

		if (user != null && role != null) {
			user.getRoles().add(role);
			userRepo.save(user);
		}

	}

	@Override
	public User getUser(String username) {
		log.info("Fetching user {}", username);

		return userRepo.findByUsername(username);
	}
	
	@Override
	public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        userRepo.updateFailedAttempts(newFailAttempts, user.getUsername());
    }
     
	@Override
    public void resetFailedAttempts(String username) {
    	userRepo.updateFailedAttempts(0, username);
    }
     
	@Override
    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
         
        userRepo.save(user);
    }
	
	
	@Override 
    public boolean unlockWhenTimeExpired(User user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();
         
        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);
             
            userRepo.save(user);
             
            return true;
        }
         
        return false;
    }

}
