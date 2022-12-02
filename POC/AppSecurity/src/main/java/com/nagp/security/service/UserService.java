package com.nagp.security.service;



import com.nagp.security.models.Role;
import com.nagp.security.models.User;

public interface UserService {
	
	User saveUser(User user);
	Role saveRole(Role role);
	void addRoleToUser(String username, String roleName);
	User getUser(String username);
	void increaseFailedAttempts(User user); 
	void resetFailedAttempts(String email);
	void lock(User user);
    boolean unlockWhenTimeExpired(User user);

}
