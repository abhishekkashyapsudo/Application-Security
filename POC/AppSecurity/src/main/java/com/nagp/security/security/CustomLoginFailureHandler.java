package com.nagp.security.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.nagp.security.models.User;
import com.nagp.security.service.UserService;
import com.nagp.security.service.UserServiceImpl;

@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
     
    @Autowired
    private UserService userService;
     
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
    		org.springframework.security.core.AuthenticationException exception) throws IOException, ServletException {
    	String username = request.getParameter("username");
        User user = userService.getUser(username);
        
        if (user != null) {
            if (user.isEnabled() && user.isAccountNonLocked()) {
                if (user.getFailedAttempt() < UserServiceImpl.MAX_FAILED_ATTEMPTS - 1) {
                    userService.increaseFailedAttempts(user);
                    exception = new LockedException("Incorrect Username/Password combination. Please try again");
                } else {
                    userService.lock(user);
                    exception = new LockedException("Your account has been locked due to 3 failed attempts."
                            + " It will be unlocked after 24 hours.");
                }
            } else if (!user.isAccountNonLocked()) {
                if (userService.unlockWhenTimeExpired(user)) {
                    exception = new LockedException("Your account has been unlocked. Please try to login again.");
                }
            } 
        }
        request.getSession().setAttribute("error", exception.getMessage());
        super.setDefaultFailureUrl("/ui/login");
        super.onAuthenticationFailure(request, response, exception);
    }
   
 
}