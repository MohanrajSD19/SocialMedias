package com.mytweetshareapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetcomposer.TweetUploadService;

import java.io.File;


 /*
 * Copyright (c) 2017. Created by Mohanraj.S,Innobot Systems on 24/10/17 for SocialMedias
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
TextView textView;
    Button button_share;
    //Uri imageUri= null;
    Uri imageUri= Uri.parse("content://media/external/images/media/15");
    String username="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_layout);
        username= getIntent().getStringExtra("username");
        /*startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/external/images/media/16")));*/
        initViews();
        setImage();
        //setTweetComposer();


    }

    private void initViews() {
        textView =(TextView)findViewById(R.id.textView);
        textView.setText(username);
        button_share= (Button)findViewById(R.id.button_share);
        button_share.setOnClickListener(this);
    }

    private void shareTweetpost() {
        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
        final Intent intent = new ComposerActivity.Builder(HomeActivity.this)
                .session(session)
                .image(imageUri)
                .text("Love where you work")
                .hashtags("#twitter integration in android app")
                .createIntent();
        startActivity(intent);
    }

    private void setTweetComposer() {
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text("just setting up my Twitter Kit.")
                .image(imageUri);
        builder.show();
    }

    private void setImage(){
        try {
            imageUri = FileProvider.getUriForFile(HomeActivity.this,
                    BuildConfig.APPLICATION_ID + ".file_provider",
                    new File("/path/to/image"));
        }catch(NullPointerException e){e.printStackTrace();}
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_share:
                shareTweetpost();
                break;
            default:
                break;
        }
    }
}
