package org.forwater.backend.dto;

import org.forwater.backend.entity.Springs;

import java.util.List;

public class PaginatedResponse {

    List<SpringsWithFormattedTime> springs;

    int totalSprings;

    public List<SpringsWithFormattedTime> getSprings() {
        return springs;
    }

    public void setSprings(List<SpringsWithFormattedTime> springs) {
        this.springs = springs;
    }

    public int getTotalSprings() {
        return totalSprings;
    }

    public void setTotalSprings(int totalSprings) {
        this.totalSprings = totalSprings;
    }
}
