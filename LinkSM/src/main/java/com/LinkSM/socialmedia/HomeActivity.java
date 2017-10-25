package com.LinkSM.socialmedia;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.LinkSM.R;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareButton;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.security.MessageDigest;
 /*
 * Copyright (c) 2017. Created by Mohanraj.S,Innobot Systems on 13/10/17 for SocialMedias
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

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = HomeActivity.class.getSimpleName();
    public static final String PACKAGE_MOBILE_SDK_SAMPLE_APP = "com.LinkSM";
    private static final int PICK_GALLERY_IMAGE = 1;
    private static final int PICK_GALLERY_VIDEO = 2;
    private ImageView imgProfile,imgLogin;
    private TextView txtDetails;
    private Button btnLogout;
    ShareButton shareButton;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        //computePakageHash();
        initializeControls();
    }

    private void initializeControls(){
        imgLogin = (ImageView)findViewById(R.id.imgLogin);
        imgLogin.setOnClickListener(this);
        btnLogout = (Button)findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
        imgProfile = (ImageView)findViewById(R.id.imgProfile);
        txtDetails = (TextView)findViewById(R.id.txtDetails);
         shareButton = new ShareButton(HomeActivity.this);


        //Default
        imgLogin.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.GONE);
        imgProfile.setVisibility(View.GONE);
        txtDetails.setVisibility(View.GONE);
    }

    private void computePakageHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    PACKAGE_MOBILE_SDK_SAMPLE_APP,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("LinkSM_TAG_KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            Log.e("LinkSM_TAG:",e.getMessage());
        }
    }

    private void handleLogout(){
        LISessionManager.getInstance(getApplicationContext()).clearSession();
        imgLogin.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.GONE);
        imgProfile.setVisibility(View.GONE);
        txtDetails.setVisibility(View.GONE);
    }


    private void handleLogin(){
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                // Authentication was successful.  You can now do
                // other calls with the SDK.
                imgLogin.setVisibility(View.GONE);
                btnLogout.setVisibility(View.VISIBLE);
                imgProfile.setVisibility(View.VISIBLE);
                txtDetails.setVisibility(View.VISIBLE);
                fetchPersonalInfo();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                // Handle authentication errors
                Log.e("LinkSM_TAG:",error.toString());
            }
        }, true);
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Add this line to your existing onActivityResult() method
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_GALLERY_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uriPhoto = data.getData();
                    //Log.d(TAG, “Selected image path :”+uriPhoto.toString());

                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uriPhoto));
                        SharePhoto photo = new SharePhoto.Builder().setBitmap(bitmap)
                                .build();
                        SharePhotoContent content = new SharePhotoContent.Builder()
                                .addPhoto(photo).build();

                        shareButton.setShareContent(content);
                        shareButton.performClick();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            case PICK_GALLERY_VIDEO:
                if (resultCode == RESULT_OK) {
                    Uri uriVideo = data.getData();

                    try {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(uriVideo,
                                filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String videoPath = cursor.getString(columnIndex);
                            cursor.close();
                            //Log.d(TAG, “Selected video path :”+videoPath);

                            ShareVideo video = new ShareVideo.Builder()
                                    .setLocalUrl(uriVideo).build();

                            ShareVideoContent content = new ShareVideoContent.Builder()
                                    .setVideo(video).build();

                            shareButton.setShareContent(content);
                            shareButton.performClick();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            default:
                //callbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void fetchPersonalInfo(){
        String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,public-profile-url,picture-url,email-address,picture-urls::(original))";

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                // Success!
                try {
                    JSONObject jsonObject = apiResponse.getResponseDataAsJson();
                    String firstName = jsonObject.getString("firstName");
                    String lastName = jsonObject.getString("lastName");
                    String pictureUrl = jsonObject.getString("pictureUrl");
                    String emailAddress = jsonObject.getString("emailAddress");

                    Picasso.with(getApplicationContext()).load(pictureUrl).into(imgProfile);

                    StringBuilder sb = new StringBuilder();
                    sb.append("First Name: "+firstName);
                    sb.append("\n\n");
                    sb.append("Last Name: "+lastName);
                    sb.append("\n\n");
                    sb.append("Email: "+emailAddress);
                    txtDetails.setText(sb);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onApiError(LIApiError liApiError) {
                // Error making GET request!
                Log.e("LinkSM_TAG_Err:",liApiError.getMessage());
            }
        });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgLogin:
                handleLogin();
                break;
            case R.id.btnLogout:
                handleLogout();
                break;
        }
    }

    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_GALLERY_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uriPhoto = data.getData();
                    Log.d(TAG, “Selected image path :” + uriPhoto.toString());

                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uriPhoto));
                        SharePhoto photo = new SharePhoto.Builder().setBitmap(bitmap)
                                .build();
                        SharePhotoContent content = new SharePhotoContent.Builder()
                                .addPhoto(photo).build();

                        shareButton.setShareContent(content);
                        shareButton.performClick();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            case PICK_GALLERY_VIDEO:
                if (resultCode == RESULT_OK) {
                    Uri uriVideo = data.getData();

                    try {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(uriVideo,
                                filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String videoPath = cursor.getString(columnIndex);
                            cursor.close();
                            Log.d(TAG, “Selected video path :” + videoPath);

                            ShareVideo video = new ShareVideo.Builder()
                                    .setLocalUrl(uriVideo).build();

                            ShareVideoContent content = new ShareVideoContent.Builder()
                                    .setVideo(video).build();

                            shareButton.setShareContent(content);
                            shareButton.performClick();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            default:
                callbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }*/
}
