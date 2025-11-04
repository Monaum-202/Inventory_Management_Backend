package com.monaum.Rapid_Global.util.response;

public class ApiResponseDTO<T> extends BaseApiResponseDTO<T> {

    public ApiResponseDTO(boolean success, String message, T data) {
        super(success, message, data);
    }

    public ApiResponseDTO(boolean success, String message) {
        super(success, message);
    }
}