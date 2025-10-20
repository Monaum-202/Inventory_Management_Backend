package com.monaum.Rapid_Global.util.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class ApiErrorResponseDTO extends BaseApiResponseDTO<Object> {

    private Map<String, List<String>> errors;

    public ApiErrorResponseDTO(String message, Map<String, List<String>> errors) {
        super(false, message);
        this.errors = errors;
    }

}
