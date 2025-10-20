/**
 * Author      : monaum hossain
 * Created on  : 5/21/2025 at 10:54 AM
 */

package com.monaum.Rapid_Global.util.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseApiResponseDTO<T> {
    private boolean success;
    private String message;

    public BaseApiResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

