package com.subham.financialgoal.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "goals")
public class FinancialGoal {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String username;
	private String name;
	private double amount;
	private double saving;
	private Date created;
	private Date deadline;
	private Date completed;
	private boolean status;
	
	
	public FinancialGoal() {
		super();
	}

	
	
	private FinancialGoal(int id, String username, String name, double amount, double saving, Date created,
			Date deadline, Date completed, boolean status) {
		this.id = id;
		this.username = username;
		this.name = name;
		this.amount = amount;
		this.saving = saving;
		this.created = created;
		this.deadline = deadline;
		this.completed = completed;
		this.status = status;
	}



	public double getSaving() {
		return saving;
	}
	public void setSaving(double saving) {
		this.saving = saving;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getDeadline() {
		return deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	public Date getCompleted() {
		return completed;
	}
	public void setCompleted(Date completed) {
		this.completed = completed;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FinancialGoal other = (FinancialGoal) obj;
		return id == other.id;
	}

	@Override
	public String toString() {
		return "FinancialGoal [id=" + id + ", username=" + username + ", name=" + name + ", amount=" + amount
				+ ", saving=" + saving + ", created=" + created + ", deadline=" + deadline + ", completed=" + completed
				+ ", status=" + status + "]";
	}
	
	public static class builder {
		/*
		 * required username(string), name(string), deadline(date);
		 * amount will be set to 0(by default)
		*/
		private String username = null;
		private String name = null;
		private double amount = 0;
		private double saving = 0;
		private Date created = new Date();
		private Date deadline = null;
		private Date completed = null;
		private boolean status = false;
		
		public builder username(String username) {
			this.username = username;
			return this;
		}
		
		public builder name(String name) {
			this.name = name;
			return this;
		}
		
		public builder amount(double amount) {
			this.amount = amount;
			return this;
		}
		
		public builder deadline(Date deadline) {
			this.deadline = deadline;
			return this;
		}
		
		public FinancialGoal build() {
			System.out.println(username + ", " + name + ", " + deadline);
			if (username == null || name == null || deadline == null) {
				return null;
			} else {
				return new FinancialGoal(0, username, name, amount, saving, created, deadline, completed, status);
			}
		}
		
	}
}
