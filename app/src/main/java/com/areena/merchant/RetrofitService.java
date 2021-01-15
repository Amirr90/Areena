package com.areena.merchant;


import com.areena.merchant.Model.CheckSumModel;
import com.areena.merchant.Model.DemoClassDataModel;
import com.areena.merchant.Model.HomeModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitService {


    @GET("showAllTutions")
    Call<HomeModel> gettutioEnquiry(@Query("mId") String merchantId);

    @GET("getAllDemoClassRequest")
    Call<DemoClassDataModel> getDemoClassData(@Query("mId") String merchantId);

    @POST("getCheckSum")
    Call<CheckSumModel> getCheckSum(@Query("oId") String oId,
                                    @Query("custId") String custId,
                                    @Query("amount") String amount);



}
