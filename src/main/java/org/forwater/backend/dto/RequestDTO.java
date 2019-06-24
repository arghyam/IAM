package org.forwater.backend.dto;

import org.forwater.backend.utils.Constants;

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
