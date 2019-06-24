package org.forwater.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties
public class NodeEntity {

    Map<String, String> springs;

    public Map<String, String> getSprings() {
        return springs;
    }

    public void setSprings(Map<String, String> springs) {
        this.springs = springs;
    }
}
