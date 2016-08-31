package com.escns.smombie.Interface;

import com.escns.smombie.DAO.Record;
import com.escns.smombie.DAO.User;
import com.escns.smombie.DAO.UserJoinRecord;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016-08-16.
 * Retrofir2 통신을 위한 API 인터페이스
 */

public interface ApiService {
    public static final String API_URL = "http://27.1.219.66:9001";

    @FormUrlEncoded
    @POST("/insert_user.php")
    Call<String> insertUser (@Field("USER_ID_TEXT") String user_id_text, @Field("NAME") String name, @Field("EMAIL") String email, @Field("GENDER") String gender, @Field("AGE") int age, @Field("POINT") int point, @Field("GOAL") int goal, @Field("REWORD") int reword, @Field("SUCCESSCNT") int successcnt, @Field("FAILCNT") int failcnt, @Field("AVGDIST") int avgdist);

    @FormUrlEncoded
    @POST("/update_user.php")
    Call<String> updateUser (@Field("USER_ID_INT") int user_id_int, @Field("NAME") String name, @Field("EMAIL") String email, @Field("GENDER") String gender, @Field("AGE") int age, @Field("POINT") int point, @Field("GOAL") int goal, @Field("REWORD") int reword, @Field("SUCCESSCNT") int successcnt, @Field("FAILCNT") int failcnt, @Field("AVGDIST") int avgdist);

    @GET("/select_user.php")
    Call<User> selectUser (@Query("USER_ID_INT") int user_id_int);

    @GET("/select_user_id_text.php")
    Call<User> selectUserIdText (@Query("USER_ID_TEXT") String user_id_text);

    @FormUrlEncoded
    @POST("/insert_record.php")
    Call<String> insertRecord (@Field("USER_ID_INT") int user_id_int, @Field("YEAR") int year, @Field("MONTH") int month, @Field("DAY") int day, @Field("HOUR") int hour, @Field("DIST") int dist);

    @GET("/select_record.php")
    Call<List<Record>> selectRecord(@Query("USER_ID_INT") int user_id_int);

    @GET("/select_record_all.php")
    Call<List<UserJoinRecord>> selectRecordAll();

}
