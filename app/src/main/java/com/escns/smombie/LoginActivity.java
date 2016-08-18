package com.escns.smombie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by hyo99 on 2016-08-10.
 */

public class LoginActivity extends Activity {

    String mFbId;                           // 페이스북 ID
    String mFbName;                         // 페이스북 이름
    String mFbEmail;                        // 페이스북 이메일

    ImageView mLoginBackground;
    LoginButton mLoginButtonInvisible;      // 페이스북 로그인 버튼
    ImageView mLoginButtonVisible;          // 커스텀 로그인 버튼

    CallbackManager callbackManager;        // 콜백

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // 페이스북 sdk 초기화
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);


        mLoginBackground = (ImageView) findViewById(R.id.login_background);
        Picasso.with(this).load(R.drawable.bg_login).fit().into(mLoginBackground);


        // 로그인 응답을 처리할 콜백 관리자를 만듦
        callbackManager = CallbackManager.Factory.create();

        mLoginButtonInvisible = (LoginButton) findViewById(R.id.login_button_invisible);
        mLoginButtonVisible = (ImageView) findViewById(R.id.login_button_visible);

        mLoginButtonVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginButtonInvisible.callOnClick();
            }
        });

        // 페이스북에서 제공할 데이터 권한
        mLoginButtonInvisible.setReadPermissions(Arrays.asList("public_profile","email"));

        // 이미 로그인 상태면 loginButton 자동실행
        if(isLogin()) {
            com.facebook.login.LoginManager.getInstance().logOut();
            mLoginButtonInvisible.performClick();
        }

        mLoginButtonInvisible.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_SHORT).show();
                mLoginButtonInvisible.setVisibility(View.INVISIBLE);

                //GraphRequest 클래스에는 지정된 액세스 토큰의 사용자 데이터를 가져오는 newMeRequest 메서드가 있다
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                                try {
                                    Log.d("tag", "Input Profile Data");
                                    mFbId = jsonObject.getString("id");
                                    mFbName = jsonObject.getString("name");
                                    mFbEmail = jsonObject.getString("email");

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("id", mFbId);
                                    intent.putExtra("name", mFbName);
                                    intent.putExtra("email", mFbEmail);
                                    startActivity(intent);
                                    finish();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture.type(large)");
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

        ((Button) findViewById(R.id.button_NoAccount)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                mFbId = "12345";
                mFbName = "이름";
                mFbEmail = "이메일주소";
                intent.putExtra("id", mFbId);
                intent.putExtra("name", mFbName);
                intent.putExtra("email", mFbEmail);
                startActivity(intent);
                finish();
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
}
