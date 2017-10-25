/*
    Copyright 2014 LinkedIn Corp.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.mylinkshareapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.mylinkshareapp.R;


public class ApiActivity extends Activity {

    private static final String host = "api.linkedin.com";
    private static final String topCardUrl = "https://" + host + "/v1/people/~:(first-name,last-name,public-profile-url)";
    private static final String shareUrl = "https://" + host + "/v1/people/~/shares";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);
        final Button makeApiCall = (Button) findViewById(R.id.makeApiCall);
        makeApiCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                apiHelper.getRequest(ApiActivity.this, topCardUrl, new ApiListener() {
                    @Override
                    public void onApiSuccess(ApiResponse s) {
                        ((TextView) findViewById(R.id.response)).setText(s.toString());
                    }

                    @Override
                    public void onApiError(LIApiError error) {
                        ((TextView) findViewById(R.id.response)).setText(error.toString());
                    }
                });
            }
        });

        final Button makePostApiCall = (Button) findViewById(R.id.makePostApiCall);
        makePostApiCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText shareComment = (EditText) findViewById(R.id.shareComment);
                String shareJsonText = "{ \n" +
                        "   \"comment\":\"" + shareComment.getText() + "\"," +
                        "   \"visibility\":{ " +
                        "      \"code\":\"anyone\"" +
                        "   }," +
                        "   \"content\":{ " +
                        "      \"title\":\"US resumes fast processing of H-1B visa\"," +
                        "      \"description\":\"US resumes fast processing of H-1B visas after five months\"," +
                        "      \"submitted-url\":\"http://www.businesstoday.in/sectors/it/us-resumes-fast-processing-of-h-1b-visas-after-five-months/story/260570.html\"," +
                        "      \"submitted-image-url\":\"http://media2.intoday.in/btmt/images/stories/visa_660_091917015129.jpg\"" +
                        "   }" +
                        "}";
                APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                apiHelper.postRequest(ApiActivity.this, shareUrl, shareJsonText, new ApiListener() {
                    @Override
                    public void onApiSuccess(ApiResponse apiResponse) {
                        ((TextView) findViewById(R.id.response)).setText("Success"+"-"+apiResponse.toString());
                    }

                    @Override
                    public void onApiError(LIApiError error) {
                        ((TextView) findViewById(R.id.response)).setText("Error"+"-"+error.toString());
                    }
                });
            }
        });

    }

}
