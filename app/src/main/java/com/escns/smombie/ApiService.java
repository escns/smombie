package com.escns.smombie;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016-08-16.
 */

public interface ApiService {
    public static final String API_URL = "https://smombie2-hajaekwon.c9users.io";

    @GET("/api/currentPoint")
    Call<ResponseBody>getCurrentPoint(@Query("currentPoint") int point);

}
