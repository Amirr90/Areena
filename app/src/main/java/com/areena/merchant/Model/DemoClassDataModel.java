package com.areena.merchant.Model;

import java.util.List;

public class DemoClassDataModel {
    boolean staus;
    String error_msg;
    String merchantType;
    List<DemoClassData>demoClassData;

    public String getMerchantType() {
        return merchantType;
    }

    public boolean isStaus() {
        return staus;
    }

    public String getError_msg() {
        return error_msg;
    }

    public List<DemoClassData> getDemoClassData() {
        return demoClassData;
    }
}
