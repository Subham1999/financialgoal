package com.subham.financialgoal.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.subham.financialgoal.dao.UserRepository;
import com.subham.financialgoal.model.User;
import com.subham.financialgoal.model.UserDetailsImplementation;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

	@Autowired private UserRepository userDb;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userDb.findByUsername(username);
		UserDetailsImplementation userDetailsImplementation = new UserDetailsImplementation();
		user.ifPresentOrElse(u -> {
			userDetailsImplementation.setUsername(u.getUsername());
			userDetailsImplementation.setPassword(u.getPassword());
		}, () -> {throw new UsernameNotFoundException("No user " + username);});
		return userDetailsImplementation;
	}

}
