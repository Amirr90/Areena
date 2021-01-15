package com.areena.merchant.Model;

import java.util.List;

public class HomeModel {

    boolean staus;
    String error_msg;
    List<TutionDataModel>tutionData;
    String merchantType;

    public String getMerchantType() {
        return merchantType;
    }

    public boolean isStaus() {
        return staus;
    }

    public String getError_msg() {
        return error_msg;
    }

    public List<TutionDataModel> getTutionData() {
        return tutionData;
    }
}
