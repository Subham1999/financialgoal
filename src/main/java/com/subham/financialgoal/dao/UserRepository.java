package com.subham.financialgoal.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.subham.financialgoal.model.User;

public interface UserRepository extends JpaRepository<User, String> {
	public Optional<User> findByUsername(String username);
	public List<User> findByFirstname(String firstname);
	public List<User> findByLastname(String lastname);
}
