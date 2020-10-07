package com.subham.financialgoal.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.subham.financialgoal.model.UserDetailsImplementation;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		
		UserDetailsImplementation user = new UserDetailsImplementation(username, username);
		
		return user;
	}

}
