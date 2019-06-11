package com.arghyam.backend.dto;

import com.arghyam.backend.entity.Springs;

import java.util.List;

public class PaginatedResponse {

    List<Springs> springs;

    int totalSprings;

    public List<Springs> getSprings() {
        return springs;
    }

    public void setSprings(List<Springs> springs) {
        this.springs = springs;
    }

    public int getTotalSprings() {
        return totalSprings;
    }

    public void setTotalSprings(int totalSprings) {
        this.totalSprings = totalSprings;
    }
}
