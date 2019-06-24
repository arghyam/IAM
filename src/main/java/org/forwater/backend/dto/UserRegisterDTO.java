package org.forwater.backend.dto;

import org.forwater.backend.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties
public class UserRegisterDTO {

    @NotEmpty
    @NotNull(message = Constants.FIELD_INVALID)
    public String name;

    public String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
