package com.escns.smombie;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.escns.smombie.DAO.Record;
import com.escns.smombie.DAO.User;
import com.escns.smombie.DAO.UserJoinRecord;
import com.escns.smombie.Interface.ApiService;
import com.escns.smombie.Manager.DBManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hyo99 on 2016-08-10.
 */

public class LoginActivity extends Activity {

    private DBManager mDbManger;
    private Retrofit mRetrofit;
    private ApiService mApiService;
    private SharedPreferences pref;

    private String mFbId;           // 페이스북 ID
    private String mFbName;         // 페이스북 이름
    private String mFbEmail;        // 페이스북 이메일
    private String mFbGender;       // 페이스북 성별
    private int mFbAge;             // 페이스북 나이

    private boolean isAutoLogin;

    ImageView mLoginBackground;
    LoginButton mLoginButtonInvisible;      // 페이스북 로그인 버튼
    ImageView mLoginButtonVisible;          // 커스텀 로그인 버튼

    CallbackManager callbackManager;        // 콜백

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 페이스북 sdk 초기화
        FacebookSdk.sdkInitialize(getApplicationContext());
        MultiDex.install(this);

        setContentView(R.layout.activity_login);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.bg_loading);
        progressBar.setVisibility(View.INVISIBLE);

        // DB 생성
        mDbManger = new DBManager(this);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        mRetrofit = new Retrofit.Builder().baseUrl(mApiService.API_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        mApiService = mRetrofit.create(ApiService.class);

        pref = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
        isAutoLogin = false;

        mLoginBackground = (ImageView) findViewById(R.id.login_background);

        final ImageView loginFacebook = (ImageView) findViewById(R.id.login_button_visible);
        Picasso.with(this).load(R.drawable.bg_login_compressed).fit().into(mLoginBackground, new Callback() {
            @Override
            public void onSuccess() {
                //progressBar.setVisibility(View.GONE);
                loginFacebook.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {

            }
        });

        // 로그인 응답을 처리할 콜백 관리자를 만듬
        callbackManager = CallbackManager.Factory.create();

        mLoginButtonInvisible = (LoginButton) findViewById(R.id.login_button_invisible);
        mLoginButtonVisible = (ImageView) findViewById(R.id.login_button_visible);

        // 페이스북에서 제공할 데이터 권한
        mLoginButtonInvisible.setReadPermissions(Arrays.asList("public_profile","email"));

        mLoginButtonInvisible.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), "로그인 중....", Toast.LENGTH_SHORT).show();
                mLoginButtonInvisible.setVisibility(View.INVISIBLE);

                //GraphRequest 클래스에는 지정된 액세스 토큰의 사용자 데이터를 가져오는 newMeRequest 메서드가 있다
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse graphResponse) {
                                try {
                                    String strAge, strRAge;
                                    mFbId = object.getString("id");
                                    mFbName = object.getString("name");
                                    mFbEmail = object.getString("email");

                                    try {
                                        mFbGender = object.getString("gender");
                                        if (mFbGender.compareTo("male") == 0) {
                                            mFbGender = "남자";
                                        } else {
                                            mFbGender = "여자";
                                        }
                                    } catch (JSONException e) {
                                        mFbGender = "남자";
                                    }

                                    try {
                                        strAge = object.getString("birthday");
                                        strRAge = strAge.replace("/", "");
                                        mFbAge = 2016 - (Integer.parseInt(strRAge) % 10000);
                                    } catch (JSONException e) {
                                        mFbAge = 20;
                                    }

                                    Log.d("tag", "User ID : " + mFbId);
                                    Log.d("tag", "User Name : " + mFbName);
                                    Log.d("tag", "User Email : " + mFbEmail);
                                    Log.d("tag", "User Gender : " + mFbGender);
                                    Log.d("tag", "User Age : " + mFbAge);

                                    checkUserIdText(mFbId);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,gender,birthday,email,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "로그인을 취소 하였습니다!", Toast.LENGTH_SHORT).show();
                Log.d("fb_login_sdk", "callback cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "에러가 발생하였습니다", Toast.LENGTH_SHORT).show();
                Log.d("fb_login_sdk", "callback onError");
            }
        });

        // 이미 로그인 상태면 loginButton 자동실행
        if(isLogin()) {
            com.facebook.login.LoginManager.getInstance().logOut();
            isAutoLogin = true;
            mLoginButtonInvisible.callOnClick();
            //mLoginButtonInvisible.performClick();
            //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            //startActivity(intent);
            //finish();
        }

        mLoginButtonVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginButtonInvisible.callOnClick();
            }
        });

        ((Button) findViewById(R.id.button_NoAccount)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFbId="1111";

                checkUserIdText(mFbId);
            }
        });
    }

    /**
     * Facebook SDK 로그인 또는 공유와 통합한 모든 액티비티와 프래그먼트에서
     * onActivityResult를 callbackManager에 전달해야 한다
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 현재 로그인 상태를 검사하는 함수
     * @return 로그인 중이면 1을 아니면 0을 반환
     */
    public boolean isLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public void checkUserIdText(final String id_text) {
        Call<User> selectUserIdText = mApiService.selectUserIdText(id_text);
        selectUserIdText.enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.i("tag", "checkUserIdText onResponse id_text : " + id_text);
                moveToMain(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.i("tag", "checkUserIdText onFailure id_text : " + id_text);
                insertUserIdText(id_text);
            }
        });
    }

    public void insertUserIdText(final String id_text) {
        Call<String> insertUser = mApiService.insertUser(id_text, mFbName, mFbEmail, mFbGender, mFbAge, 0, MainActivity.DEFAULT_GOAL, 0, 0, 0, 0);
        insertUser.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("tag", "insertUserIdText onResponse");
                checkUserIdText(mFbId);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("tag", "insertUserIdText onFailure");
                Log.i("tag", t.getMessage());
            }
        });
    }

    public void moveToMain(User user) {

        MakeUserInfo(user);

        Log.i("tag", user.toString());
        Call<List<Record>> selectRecord = mApiService.selectRecord(user.getmIdInt());
        selectRecord.enqueue(new retrofit2.Callback<List<Record>>() {
            @Override
            public void onResponse(Call<List<Record>> call, Response<List<Record>> response) {
                mDbManger.dropRecordTable();
                mDbManger.CreateRecordTable();
                List<Record> list = response.body();
                if(list!=null) {
                    for(Record r : list) {
                        Log.i("tag", r.toString());
                        mDbManger.insertRecord(r);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Record>> call, Throwable t) {

            }
        });

        Call<List<UserJoinRecord>> selectRecordAll = mApiService.selectRecordAll();
        selectRecordAll.enqueue(new retrofit2.Callback<List<UserJoinRecord>>() {
            @Override
            public void onResponse(Call<List<UserJoinRecord>> call, Response<List<UserJoinRecord>> response) {

                MakeStatisticInfo(response);
            }

            @Override
            public void onFailure(Call<List<UserJoinRecord>> call, Throwable t) {

            }
        });

        if(!isAutoLogin) {
            pref.edit().putBoolean("switch", true).commit();
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //intent.putExtra("AutoLogin",isAutoLogin);
        startActivity(intent);
        finish();
    }

    private void MakeStatisticInfo(Response<List<UserJoinRecord>> response) {
        int sumMale = 0;
        int sumFemale = 0;
        int sum10s = 0;
        int sum20s = 0;
        int sum30s = 0;
        int sum40s = 0;
        int sum50s = 0;
        int cntMale = 0;
        int cntFemale = 0;
        int cnt10s = 0;
        int cnt20s = 0;
        int cnt30s = 0;
        int cnt40s = 0;
        int cnt50s = 0;

        for(UserJoinRecord ujr : response.body()) {
            int age = ujr.getAGE();
            if(age >= 50) {
                cnt50s++;
                sum50s += ujr.getAVGDIST();
            } else if(age >= 40) {
                cnt40s++;
                sum40s += ujr.getAVGDIST();
            } else if(age >= 30) {
                cnt30s++;
                sum30s += ujr.getAVGDIST();
            } else if(age >= 20) {
                cnt20s++;
                sum20s += ujr.getAVGDIST();
            } else if(age >= 10) {
                cnt10s++;
                sum10s += ujr.getAVGDIST();
            }
            boolean isMale = ujr.getGENDER().compareTo("남자") == 0;
            if(isMale) {
                cntMale++;
                sumMale += ujr.getAVGDIST();
            } else {
                cntFemale++;
                sumFemale = ujr.getAVGDIST();
            }
        }

        float avgMale;
        float avgFemale;
        float avg10s;
        float avg20s;
        float avg30s;
        float avg40s;
        float avg50s;

        if(cntMale!=0) avgMale = sumMale/cntMale;
        else avgMale = 0;
        if(cntFemale!=0) avgFemale = sumFemale/cntFemale;
        else avgFemale = 0;
        if(cnt10s!=0) avg10s = sum10s/cnt10s;
        else avg10s = 0;
        if(cnt20s!=0) avg20s = sum20s/cnt20s;
        else avg20s = 0;
        if(cnt30s!=0) avg30s = sum30s/cnt30s;
        else avg30s = 0;
        if(cnt40s!=0) avg40s = sum40s/cnt40s;
        else avg40s = 0;
        if(cnt50s!=0) avg50s = sum50s/cnt50s;
        else avg50s = 0;

        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("AVGMALE", avgMale);
        editor.putFloat("AVGFEMALE", avgFemale);
        editor.putFloat("AVG10S", avg10s);
        editor.putFloat("AVG20S", avg20s);
        editor.putFloat("AVG30S", avg30s);
        editor.putFloat("AVG40S", avg40s);
        editor.putFloat("AVG50S", avg50s);
        editor.commit();

        Log.i("tag", "avgMale : " + avgMale);
        Log.i("tag", "avgFemale : " + avgFemale);
        Log.i("tag", "avg10s : " + avg10s);
        Log.i("tag", "avg20s : " + avg20s);
        Log.i("tag", "avg30s : " + avg30s);
    }

    private void MakeUserInfo(User user) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("USER_ID_INT", user.getmIdInt());
        editor.putString("USER_ID_TEXT", user.getmIdStr());
        editor.putString("NAME", user.getmName());
        editor.putString("EMAIL", user.getmEmail());
        editor.putString("GENDER", user.getmGender());
        editor.putInt("AGE", user.getmAge());
        editor.putInt("POINT", user.getmPoint());
        editor.putInt("GOAL", user.getmGoal());
        editor.putInt("REWORD", user.getmReword());
        editor.putInt("SUCCESSCNT", user.getmSuccessCnt());
        editor.putInt("FAILCNT", user.getmFailCnt());
        editor.putInt("AVGDIST", user.getmAvgDist());
        editor.commit();
    }
}
