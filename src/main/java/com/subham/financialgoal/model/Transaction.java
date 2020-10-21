package com.subham.financialgoal.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transaction")
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String username; 
	private Date date;
	private double credit;
	private double debit;
	private boolean successful;
	private String message;
	private Transaction(int id, String username, Date date, double credit, double debit, String message, boolean successful) {
		this.id = id;
		this.username = username;
		this.date = date;
		this.credit = credit;
		this.debit = debit;
		this.message = message;
		this.successful = successful;
	}
	public Transaction() {	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isSuccessful() {
		return successful;
	}
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
	}
	public double getDebit() {
		return debit;
	}
	public void setDebit(double debit) {
		this.debit = debit;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "Transaction [id=" + id + ", username=" + username + ", date=" + date + ", credit=" + credit + ", debit="
				+ debit + ", successful=" + successful + ", message=" + message + "]";
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
		Transaction other = (Transaction) obj;
		return id == other.id;
	}
	public static class builder{
		private int id = 0;
		private String username; 
		private Date date = new Date();
		private double credit = 0;
		private double debit = 0;
		private boolean successful;
		private String message;
		private boolean isCredit = false;
		
		public builder successful() {
			successful = true;
			return this;
		}
		
		public builder failed() {
			successful = false;
			return this;
		}
		
		public builder username(String username) {
			this.username = username;
			return this;
		}
		
		public builder message(String message) {
			this.message = message;
			return this;
		}
		
		public builder credit(double credit) {
			this.credit = credit;
			this.isCredit = true;
			return this;
		}		
		
		public builder debit(double debit) {
			if (!isCredit) this.debit = debit;
			return this;
		}
		
		public Transaction build() {
			return new Transaction(id, username, date, credit, debit, message, successful);
		}
	}
}
