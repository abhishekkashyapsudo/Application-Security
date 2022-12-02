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
import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	
	public User(Long id , String name, String username, String password, ArrayList<Role> roles) {
		this.id = id;
		this.name = name;
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.enabled = true;
		this.accountNonLocked = true;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	@Column(unique = true)
	private String username;
	private String password;
	private String contact;
	private String address;
		
	
	private boolean enabled;
    
    @Column(name = "account_non_locked")
    private boolean accountNonLocked;
     
    @Column(name = "failed_attempt")
    private int failedAttempt;
     
    @Column(name = "lock_time")
    private Date lockTime;
	
	@ManyToMany(fetch = FetchType.EAGER)
	private Collection<Role> roles = new ArrayList<>();

	public static User fromDto(UserDto dto) {
		 return new User(null, dto.getName(), dto.getUsername(), dto.getPassword(), new ArrayList<>());

	}
	
	

	

}
