package com.subham.financialgoal.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.subham.financialgoal.dao.FinancialGoalRepository;
import com.subham.financialgoal.dao.TransactionRepository;
import com.subham.financialgoal.dao.UserRepository;
import com.subham.financialgoal.model.ApiErrorMessage;
import com.subham.financialgoal.model.ApiErrors;
import com.subham.financialgoal.model.ErrorResponse;
import com.subham.financialgoal.model.FinancialGoal;
import com.subham.financialgoal.model.Transaction;
import com.subham.financialgoal.model.User;
import com.subham.financialgoal.model.UserDetailsImplementation;
import com.subham.financialgoal.model.auth.AuthenticationRequest;
import com.subham.financialgoal.model.auth.AuthenticationResponse;
import com.subham.financialgoal.util.JwtUtil;

@RestController
@CrossOrigin
public class UserController {
	
	@Autowired private AuthenticationManager authenticationManager;
	@Autowired private UserRepository userDb;
	@Autowired private FinancialGoalRepository goalDb;
	@Autowired private TransactionRepository txDb;
	@Autowired private UserDetailsServiceImplementation userDetailsServiceImplementation;
	@Autowired private JwtUtil jwtUtil;
	
	@GetMapping("/users")
	public List<User> getListOfUsers() {
		return userDb.findAll();
	}
	
	@GetMapping("/users/{username}")
	public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
		if (! this.checkUsernameOnPath(username)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String("username {" + username + "} doesn't exists"));
		}
		return ResponseEntity.ok(userDb.findById(username).get());
	}
	
	@GetMapping("/users/{username}/goals")
	public ResponseEntity<?> getAllGoalsFindByUsername(@PathVariable String username) {
		if (! this.checkUsernameOnPath(username)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String("username {" + username + "} doesn't exists"));
		}
		return ResponseEntity.ok(goalDb.findByUsername(username));
	}
	
	@GetMapping("/users/{username}/goals/status={status}")
	public ResponseEntity<?> getAllGoalsFindByUsernameAndStatus(@PathVariable String username, @PathVariable boolean status){
		if (! this.checkUsernameOnPath(username)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String("username {" + username + "} doesn't exists"));
		}
		List<FinancialGoal> list = new ArrayList<>();
		goalDb.findByUsername(username)
			.stream()
			.filter(goal -> goal.isStatus() == status)
			.forEachOrdered(goal -> list.add(goal));
		return ResponseEntity.ok(list);
	}
	
	@GetMapping("/users/{username}/transactions")
	public ResponseEntity<?> getAllTransactionsForUser(@PathVariable String username) {
		if (! this.checkUsernameOnPath(username)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String("username {" + username + "} doesn't exists"));
		}
		return ResponseEntity.ok(txDb.findByUsername(username));
	}
	@GetMapping("/users/{username}/transactions/successfull")
	public ResponseEntity<?> getAllValidTransactionForUser(@PathVariable String username) {
		if (! this.checkUsernameOnPath(username)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String("username {" + username + "} doesn't exists"));
		}
		List<Transaction> list = new ArrayList<>();
		txDb.findByUsername(username).stream().filter(tx -> tx.isSuccessful()).forEach(tx -> list.add(tx));
		return ResponseEntity.ok(list);
	}
	@GetMapping("/users/{username}/transactions/unsuccessfull")
	public ResponseEntity<?> getAllInvalidTransactionForUser(@PathVariable String username) {
		if (this.checkUsernameOnPath(username) ) {
			List<Transaction> list = new ArrayList<>();
			txDb.findByUsername(username).stream().filter(tx -> tx.isSuccessful() == false).forEach(tx -> list.add(tx));
			return ResponseEntity.ok(list);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String("username {" + username + "} doesn't exists"));
		}
	}
	
	@PostMapping("/users/{username}/addmoney")
	public ResponseEntity<?> addMoneyToUser(@PathVariable String username, @RequestBody Map<Object, Object> requestBody) {
		if (! this.checkUsernameOnPath(username)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String("username {" + username + "} doesn't exists"));
		}
		double money = (double) (Integer) requestBody.getOrDefault("addmoney", 0);
		Optional<User> data = userDb.findByUsername(username);
		if (data.isPresent()) {
			User user = data.get();
			user.setWallet(user.getWallet() + money);
			userDb.save(user);
			
			Transaction tx = new Transaction.builder()
					.username(username)
					.credit(money)
					.successful()
					.message("Money successfully added to wallet")
					.build();
			txDb.save(tx);
			
			return ResponseEntity.ok(tx);
		} else {
			Transaction tx = new Transaction.builder()
					.username(username)
					.failed()
					.message("Unsuccessfull")
					.build();
			txDb.save(tx);
			return ResponseEntity.ok(tx);
		}
	}
	
	@PostMapping("/users/{username}/goals")
	public ResponseEntity<?> saveGoalForUser(@RequestBody Map<Object, Object> map, @PathVariable String username) throws ParseException {
		if (! this.checkUsernameOnPath(username)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String("username {" + username + "} doesn't exists"));
		}
		String usernameFromMap = (String) map.get("username");
		String name = (String) map.get("name");
		String deadlineStr = (String) map.get("deadline");
		Date deadline;
		try {
			deadline = new SimpleDateFormat("dd/MM/yyyy").parse(deadlineStr);
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorResponse()
							.username(username)
							.addError(ApiErrorMessage.get(ApiErrors.DATE_FORMAT))
							.addError(!username.equals(usernameFromMap), ApiErrorMessage.get(ApiErrors.USERNAME_AMBIGUITY))
							.get());
		}
		
		System.out.println("Deadline " + deadline);
		
		if(usernameFromMap != null && name != null && deadline != null && usernameFromMap.equals(username) ) {
			FinancialGoal goal = new FinancialGoal.builder()
					.username(usernameFromMap)
					.name(name)
					.deadline(deadline)
					.amount((double) (Integer) map.getOrDefault("amount", 0))
					.build();
			if (goal == null) {
				return ResponseEntity.badRequest().body("goal cannot be created");
			} else {
				goal = goalDb.save(goal);
				Map<Object, Object> responseBody = new HashMap<>();
				responseBody.put("created_goal", goal);
				return ResponseEntity.ok(responseBody);
			}
		} else {
			return ResponseEntity.badRequest()
					.body(new ErrorResponse()
						.username(username)
						.addError(username == null, ApiErrorMessage.get(ApiErrors.NULL_USERNAME))
						.addError(usernameFromMap == null, ApiErrorMessage.get(ApiErrors.NULL_USERNAME))
						.addError(name == null, ApiErrorMessage.get(ApiErrors.NULL_USER_GOAL_NAME))
						.addError(deadlineStr == null, ApiErrorMessage.get(ApiErrors.INVALID_USER_GOAL))
						.addError(!username.equals(usernameFromMap), ApiErrorMessage.get(ApiErrors.USERNAME_AMBIGUITY))
						.get()
					);	
		}
	}
	
	@PostMapping("/users/{username}/goals/{id}/saving")
	public ResponseEntity<?> addSavingsMoneyToGoalForUser(@PathVariable int id, @PathVariable String username, @RequestBody Map<Object, Object> body) {
		if (! this.checkUsernameOnPath(username)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String("username {" + username + "} doesn't exists"));
		}
		int amountToBeAdded = (Integer) body.get("addmoney");
		System.out.println("addmoney " + amountToBeAdded);
		Optional<FinancialGoal> data = goalDb.findById(id);
		if (data.isPresent()) {
			FinancialGoal goal = data.get();
			goal.setAmount(goal.getAmount() + (double) amountToBeAdded);
			goalDb.save(goal);
			Transaction tx = new Transaction.builder()
					.username(username)
					.debit(amountToBeAdded)
					.successful()
					.message("Money added to goal [" + id + "]")
					.build();
			txDb.save(tx);
			return ResponseEntity.ok(tx);
		} else {
			Transaction tx = new Transaction.builder()
					.username(username)
					.failed()
					.message("Goal [" + id + "] for user [" + username + "] not found")
					.build();
			txDb.save(tx);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(tx);
		}
	}
	
	
	@PutMapping("/users/{username}/goals/{id}")
	public ResponseEntity<?> updateGoalForUser(@RequestBody FinancialGoal goal, @PathVariable String username) {
		if (! this.checkUsernameOnPath(username)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String("username {" + username + "} doesn't exists"));
		}
		return ResponseEntity.ok(goalDb.save(goal));
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
		ResponseEntity<AuthenticationResponse> res = ResponseEntity.ok(new AuthenticationResponse(jwtToken));
		return res;
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUserAndGenerateToken(@RequestBody User user) {
		User newUser = null;
		Optional<User> oldUser = userDb.findByUsername(user.getUsername());
		if(oldUser.isEmpty()) {
			newUser = userDb.save(user);
			final String jwtToken = jwtUtil.generateToken(new UserDetailsImplementation(newUser.getUsername(), newUser.getPassword()));
			return ResponseEntity.ok(new AuthenticationResponse(jwtToken));
		} else {
			return ResponseEntity.badRequest().body(new ErrorResponse()
														.addError(ApiErrorMessage.get(ApiErrors.INVALID_USERNAME)).get());
		}
	}
	
	private boolean checkUsernameOnPath(String username) {
		if (userDb.findById(username).isEmpty())  return false;
		return true;
	}
}

