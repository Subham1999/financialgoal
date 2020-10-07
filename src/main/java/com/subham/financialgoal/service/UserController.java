package com.subham.financialgoal.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.subham.financialgoal.dao.FinancialGoalRepository;
import com.subham.financialgoal.dao.TransactionRepository;
import com.subham.financialgoal.dao.UserRepository;
import com.subham.financialgoal.model.FinancialGoal;
import com.subham.financialgoal.model.Transaction;
import com.subham.financialgoal.model.User;
import com.subham.financialgoal.model.auth.AuthenticationRequest;
import com.subham.financialgoal.model.auth.AuthenticationResponse;
import com.subham.financialgoal.util.JwtUtil;

@RestController
public class UserController {
	
	@Autowired private AuthenticationManager authenticationManager;
	@Autowired private UserRepository userDb;
	@Autowired private FinancialGoalRepository goalDb;
	@Autowired private TransactionRepository txDb;
	@Autowired private UserDetailsServiceImplementation userDetailsServiceImplementation;
	@Autowired private JwtUtil jwtUtil;
	private static boolean addDemo = false;
	
	@GetMapping("/users")
	public List<User> getListOfUsers() {
		
		if(!addDemo) {
			User user = new User();
			user.setUsername("subham");
			user.setFirstname("Subham");
			user.setLastname("Santra");
			user.setMail("subhamsantra2016@gmail.com");
			user.setPassword("abc@123");
			user.addRoles(Arrays.asList("ROLE_ADMIN", "ROLE_USER"));
			user.setWallet(5000);
			userDb.save(user);
			addDemo = true;
		}
		
		
		return userDb.findAll();
	}
	
	@GetMapping("/users/{username}")
	public Optional<User> getUserByUsername(@PathVariable String username) {
		return userDb.findById(username);
	}
	
	@GetMapping("/users/{username}/goals")
	public List<FinancialGoal> getAllGoalsFindByUsername(@PathVariable String username) {
		return goalDb.findByUsername(username);
	}
	
	@GetMapping("/users/{username}/goals/status={status}")
	public List<FinancialGoal> getAllGoalsFindByUsernameAndStatus(@PathVariable String username, @PathVariable boolean status){
		List<FinancialGoal> list = new ArrayList<>();
		goalDb.findByUsername(username)
			.stream()
			.filter(goal -> goal.isStatus() == status)
			.forEachOrdered(goal -> list.add(goal));
		return list;
	}
	
	@GetMapping("/users/{username}/transactions")
	public List<Transaction> getAllTransactionsForUser(@PathVariable String username) {
		return txDb.findByUsername(username);
	}
	@GetMapping("/users/{username}/transactions/successfull")
	public List<Transaction> getAllValidTransactionForUser(@PathVariable String username) {
		List<Transaction> list = new ArrayList<>();
		txDb.findByUsername(username).stream().filter(tx -> tx.isSuccessful()).forEach(tx -> list.add(tx));
		return list;
	}
	@GetMapping("/users/{username}/transactions/unsuccessfull")
	public List<Transaction> getAllInvalidTransactionForUser(@PathVariable String username) {
		List<Transaction> list = new ArrayList<>();
		txDb.findByUsername(username).stream().filter(tx -> tx.isSuccessful() == false).forEach(tx -> list.add(tx));
		return list;
	}
	
	@PostMapping("/users")
	public User saveUserEntity(@RequestBody User user) {
		return userDb.save(user);
	}
	
	@PostMapping("/users/{username}/goals")
	public FinancialGoal saveGoalForUser(@RequestBody FinancialGoal goal, @PathVariable String username) {
		return goalDb.save(goal);
	}
	
	@PostMapping("/users/{username}/goals/{id}/saving?add")
	public void addSavingsMoneyToGoalForUser(@PathVariable int id, @PathVariable String username, @RequestBody Map<Object, Object> body) {
		int amountToBeAdded = Integer.parseInt((String) body.get("addmoney"));
		goalDb.findById(id).ifPresentOrElse((goal -> {
			goal.setAmount(goal.getAmount() + amountToBeAdded);
			Transaction tx = new Transaction();
			tx.setCredit(0);
			tx.setDebit((double) amountToBeAdded);
			tx.setUsername(username);
			tx.setDate(new Date());
			tx.setSuccessful(true);
			tx.setMessage("Money successfully added");
			txDb.save(tx);
			goalDb.save(goal);
		}), new Runnable() {
			@Override
			public void run() {
				Transaction tx = new Transaction();
				tx.setUsername(username);
				tx.setDate(new Date());
				tx.setSuccessful(false);
				tx.setMessage("Failed to add money, amount is Rs. %0.2f".formatted(amountToBeAdded));
				txDb.save(tx);
			}
		});
	}
	
	
	@PutMapping("/users/{username}/goals/{id}")
	public FinancialGoal updateGoalForUser(@RequestBody FinancialGoal goal, @PathVariable String username) {
		return goalDb.save(goal);
	}
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authRequest) throws Exception {
		try {
			authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		} catch(BadCredentialsException e) {
			throw new Exception("[Authentication Failed] username [" 
										+ authRequest.getUsername()
										+ "] password ["
										+ authRequest.getPassword() + "]");
		}
		final UserDetails userDetails = userDetailsServiceImplementation.loadUserByUsername(authRequest.getUsername());
		final String jwtToken = jwtUtil.generateToken(userDetails);
		return ResponseEntity.ok(new AuthenticationResponse(jwtToken));
	}
}

