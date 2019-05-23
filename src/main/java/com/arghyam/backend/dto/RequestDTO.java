package com.arghyam.backend.dto;

import com.arghyam.backend.utils.Constants;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class RequestDTO extends RequestBaseDTO {

    @NotNull(message = Constants.FIELD_INVALID)
    private Map<String, Object> request;

    public Map<String, Object> getRequest() {
        return request;
    }

    public void setRequest(Map<String, Object> request) {
        this.request = request;
    }
}
