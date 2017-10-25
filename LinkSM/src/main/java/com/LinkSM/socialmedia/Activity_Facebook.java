package com.LinkSM.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.LinkSM.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


 /*
 * Copyright (c) 2017. Created by Mohanraj.S,Innobot Systems on 16/10/17 for SocialMedias
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Activity_Facebook extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = Activity_Facebook.this.getClass().getName();
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private LoginButton loginButton;
    private RelativeLayout rlProfileArea;
    private TextView tvName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //genrateKeyHash();
        setContentView(R.layout.facebook_layout);
        initParameters();
        initViews();
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null) {
                    Log.d(TAG, "User logged out successfully");
                    rlProfileArea.setVisibility(View.GONE);
                }
            }
        };
    }

   /* private void genrateKeyHash() {
        try {
            PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(
                    "com.yourappname.app",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", "KeyHash:" + Base64.encodeToString(md.digest(),
                        Base64.DEFAULT));
                Toast.makeText(getApplicationContext().getApplicationContext(), Base64.encodeToString(md.digest(),
                        Base64.DEFAULT), Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }*/

    public void initParameters() {
        accessToken = AccessToken.getCurrentAccessToken();
        callbackManager = CallbackManager.Factory.create();
    }

    public void initViews() {
        loginButton = (LoginButton) findViewById(R.id.activity_main_btn_login);
        rlProfileArea = (RelativeLayout) findViewById(R.id.activity_main_rl_profile_area);
        tvName = (TextView) findViewById(R.id.activity_main_tv_name);

        loginButton.setReadPermissions(Arrays.asList(new String[]{"email", "user_birthday", "user_hometown"}));

        if (accessToken != null) {
            getProfileData();
        } else {
            rlProfileArea.setVisibility(View.GONE);
        }
// Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "User login successfully");
                getProfileData();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "User cancel login");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Problem for login");
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void getProfileData() {
        try {
            accessToken = AccessToken.getCurrentAccessToken();
            rlProfileArea.setVisibility(View.VISIBLE);
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            Log.d(TAG, "Graph Object :" + object);
                            try {
                                String name = object.getString("name");
                                tvName.setText("Welcome, " + name);

                                Log.d(TAG, "Name :" + name);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,birthday,gender,email");
            request.setParameters(parameters);
            request.executeAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    @Override
    public void onClick(View view) {

    }
}
