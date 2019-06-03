package com.arghyam.backend.dto;

import com.arghyam.backend.entity.DischargeData;

public class RequestDischarge {

    DischargeData dischargeData;

    public DischargeData getDischargeData() {
        return dischargeData;
    }

    public void setDischargeData(DischargeData dischargeData) {
        this.dischargeData = dischargeData;
    }
}
