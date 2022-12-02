package com.nagp.security.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.nagp.security.security.ValidPassword;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
	

	
	
	@Size(min = 3, max = 30)
	@NotBlank(message = "Name is mandatory")
	private String name;
	
	@Size(min = 3, max = 30)
	@NotBlank(message = "Username is mandatory")
	private String username;
	
	@Size(min = 3, max = 30)
	@NotBlank(message = "Password is mandatory")
	@ValidPassword
	private String password;
	
	@Size(min = 3, max = 30)
	private String contact;
	
	@Size(min = 3, max = 30)
	private String address;
	
	
	@Size(min = 3, max = 30)
	@NotBlank(message = "Confirm Password is mandatory")
	private transient String confirmPassword;
	
	private String captcha;
	private String hiddenCaptcha;
	private String realCaptcha;
	    
  
	
	

	

}
