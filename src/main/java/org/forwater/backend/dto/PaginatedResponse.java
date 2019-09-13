package org.forwater.backend.dto;

import java.util.List;

public class PaginatedResponse {

    List<GetAllSpringsWithFormattedTime> springs;

    int totalSprings;

    public List<GetAllSpringsWithFormattedTime> getSprings() {
        return springs;
    }

    public void setSprings(List<GetAllSpringsWithFormattedTime> springs) {
        this.springs = springs;
    }

    public int getTotalSprings() {
        return totalSprings;
    }

    public void setTotalSprings(int totalSprings) {
        this.totalSprings = totalSprings;
    }
}
