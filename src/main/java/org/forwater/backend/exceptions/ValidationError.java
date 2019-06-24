package org.forwater.backend.exceptions;

public class ValidationError {

    private String field;
    private String message;

    /**
     * Instantiates a new Validation error.
     *
     * @param field the field
     * @param message the message
     */
    public ValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    /**
     * Gets field.
     *
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * Sets field.
     *
     * @param field the field
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return this.field+message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
