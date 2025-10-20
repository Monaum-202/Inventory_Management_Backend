package com.monaum.Rapid_Global.util.response;

public class ApiResponseDTO extends BaseApiResponseDTO<Object> {

    public ApiResponseDTO(boolean success, String message) {
        super(success, message);
    }


}