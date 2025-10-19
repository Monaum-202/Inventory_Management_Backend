package com.monaum.Rapid_Global.exception;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
	private int status;
	private String error;
	private String message;
	Map<String, String> fieldErrors;
}
