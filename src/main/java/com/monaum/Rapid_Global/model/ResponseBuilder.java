package com.monaum.Rapid_Global.model;

import java.time.LocalDateTime;

import com.monaum.Rapid_Global.enums.ResponseStatusType;
import org.springframework.http.ResponseEntity;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

public class ResponseBuilder {
	public static <T> ResponseEntity<SuccessResponse<T>> build(ResponseStatusType type, T data) {
		SuccessResponse<T> response = SuccessResponse.<T>builder()
				.status(type.getHttpStatus().value())
				.message(type.getMessage())
				.data(data)
				.timestamp(LocalDateTime.now())
				.build();

		return new ResponseEntity<>(response, type.getHttpStatus());
	}

	// Optional: builder with custom message override
	public static <T> ResponseEntity<SuccessResponse<T>> build(ResponseStatusType type, String customMessage, T data) {
		SuccessResponse<T> response = SuccessResponse.<T>builder()
				.status(type.getHttpStatus().value())
				.message(customMessage)
				.data(data)
				.timestamp(LocalDateTime.now())
				.build();

		return new ResponseEntity<>(response, type.getHttpStatus());
	}
}
