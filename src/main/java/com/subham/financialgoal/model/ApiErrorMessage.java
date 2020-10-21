package com.subham.financialgoal.model;

import java.util.Map;

public class ApiErrorMessage {
	public static Map.Entry<ApiErrors, String> get(ApiErrors error) {
		switch(error) {
			case DATE_FORMAT: 			return Map.entry(error, "accepted date format is dd/mm/yyyy");
			case INVALID_USER_GOAL: 	return Map.entry(error, "no such user goal exists");
			case INVALID_USERNAME: 		return Map.entry(error, "no user exists with this username");
			case USERNAME_AMBIGUITY:	return Map.entry(error, "username is ambiguous");
			case NULL_USERNAME:			return Map.entry(error, "username provided is null object");
			case NULL_DATE_STRING:		return Map.entry(error, "date required");
			case SERVER_ERROR: 			return Map.entry(error, "server error");
			case NULL_USER_GOAL_NAME: 	return Map.entry(error, "goal name is not provided");
			default: 					return Map.entry(error, "!!!");
		}
	}
}
