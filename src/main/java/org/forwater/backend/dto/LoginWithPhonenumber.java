package org.forwater.backend.dto;

import org.forwater.backend.utils.Constants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LoginWithPhonenumber {

        @NotNull(message = Constants.FIELD_INVALID)
        @NotEmpty
        public String phoneNumber;

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

}
