package org.forwater.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public class RegistryRequest {

    private String id;
    private String ver;
    private Long ets;
    private RequestParams params;
    private Map<String, Object> request;
    @JsonIgnore
    private String requestMapString;
    @JsonIgnore
    private JsonNode requestMapNode;

    public RegistryRequest() {
        this.ver = "1.0";
        this.ets = System.currentTimeMillis();
    }

    public RegistryRequest(RequestParams params, Map<String, Object> request, String id, String requestMapString) {
        this.ver = "1.0";
        this.ets = System.currentTimeMillis();
        this.params = params;
        this.request = request;
        this.requestMapString = requestMapString;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public Long getEts() {
        return ets;
    }

    public void setEts(Long ets) {
        this.ets = ets;
    }

    public RequestParams getParams() {
        return params;
    }

    public void setParams(RequestParams params) {
        this.params = params;
    }


    public Map<String, Object> getRequest() {
        return request;
    }

    public void setRequest(Map<String, Object> request) {
        this.request = request;
    }

    public String getRequestMapString() {
        return requestMapString;
    }

    public void setRequestMapString(String requestMapString) {
        this.requestMapString = requestMapString;
    }

    public JsonNode getRequestMapNode() {
        return requestMapNode;
    }

    public void setRequestMapNode(JsonNode requestMapNode) {
        this.requestMapNode = requestMapNode;
    }
}
