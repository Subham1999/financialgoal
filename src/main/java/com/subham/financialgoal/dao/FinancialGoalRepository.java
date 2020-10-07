package com.subham.financialgoal.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.subham.financialgoal.model.FinancialGoal;

public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Integer> {
	public List<FinancialGoal> findByUsername(String username);
	public List<FinancialGoal> findByStatus(boolean status);
}
