package com.mylinkshareapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.mylinkshareapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imgProfile,imgLogin;
    private TextView txtDetails;
    private Button btnLogout,btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeControls();
    }

    private void initializeControls(){
        imgLogin = (ImageView)findViewById(R.id.imgLogin);
        imgLogin.setOnClickListener(this);
        btnLogout = (Button)findViewById(R.id.btnLogout);
        btnShare = (Button)findViewById(R.id.btnShare);
        btnLogout.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        imgProfile = (ImageView)findViewById(R.id.imgProfile);
        txtDetails = (TextView)findViewById(R.id.txtDetails);

        //Default
        imgLogin.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.GONE);
        imgProfile.setVisibility(View.GONE);
        txtDetails.setVisibility(View.GONE);
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
            case R.id.btnShare:
                Intent io = new Intent(MainActivity.this,ApiActivity.class);
                startActivity(io);
                finish();
                break;
            default:
                break;

        }
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
                Log.e("NIKHIL",error.toString());
            }
        }, true);
    }


    private void handleLogout(){
        LISessionManager.getInstance(getApplicationContext()).clearSession();
        imgLogin.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.GONE);
        imgProfile.setVisibility(View.GONE);
        txtDetails.setVisibility(View.GONE);
    }


    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Add this line to your existing onActivityResult() method
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
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
                Log.e("NIKHIL",liApiError.getMessage());
            }
        });
    }
}
