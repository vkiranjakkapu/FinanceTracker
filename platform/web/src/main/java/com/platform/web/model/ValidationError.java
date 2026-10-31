package com.platform.web.model;

public record ValidationError(
		String field,
		Object rejectedValue,
		String message) {
}