package org.forwater.backend.dto;

import org.forwater.backend.entity.DischargeData;

public class RequestDischarge {

    DischargeData dischargeData;

    public DischargeData getDischargeData() {
        return dischargeData;
    }

    public void setDischargeData(DischargeData dischargeData) {
        this.dischargeData = dischargeData;
    }
}
