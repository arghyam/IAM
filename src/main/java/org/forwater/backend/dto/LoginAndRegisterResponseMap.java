package org.forwater.backend.dto;

import org.forwater.backend.utils.Constants;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class LoginAndRegisterResponseMap extends RequestBaseDTO {


    @NotNull(message = Constants.FIELD_INVALID)
    private Map<String, Object> response;

    public Map<String, Object> getResponse() {
        return response;
    }

    public void setResponse(Map<String, Object> response) {
        this.response = response;
    }
}
