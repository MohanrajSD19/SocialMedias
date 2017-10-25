package com.myfbsharing2;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    TextView txtName, txtURL, txtGender,txtBd;
    Button btnShare;

    private ShareDialog shareDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shareDialog = new ShareDialog(this);

        imageView = (ImageView) findViewById(R.id.imgPhoto);
        txtName = (TextView) findViewById(R.id.txtName);
        txtURL = (TextView) findViewById(R.id.txtURL);
        txtGender = (TextView) findViewById(R.id.txtGender);
        txtBd = (TextView) findViewById(R.id.txtBd);

        //Another way to share content
        btnShare = (Button) findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Hello Guys")
                            .setContentDescription(
                                    "Coder who learned and share")
                            .setContentUrl(Uri.parse("http://instinctcoder.com"))
                            .setImageUrl(Uri.parse("https://scontent-sin1-1.xx.fbcdn.net/hphotos-xap1/v/t1.0-9/12936641_845624472216348_1810921572759298872_n.jpg?oh=72421b8fa60d05e68c6fedbb824adfbf&oe=577949AA"))
                            .build();

                    shareDialog.show(linkContent);
                }
            }
        });


        GetUserInfo();

        //Like
        LikeView likeView = (LikeView) findViewById(R.id.likeView);
        likeView.setLikeViewStyle(LikeView.Style.STANDARD);
        likeView.setAuxiliaryViewPosition(LikeView.AuxiliaryViewPosition.INLINE);

        likeView.setObjectIdAndType(
                "http://instinctcoder.com",
                LikeView.ObjectType.OPEN_GRAPH);



        //Share Dialog
        //You cannot preset the shared link in design time, if you do so, the fb share button will
        //look disabled. You need to set in the code as below
        ShareButton fbShareButton = (ShareButton) findViewById(R.id.fb_share_button);
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle("Hello Guys")
                .setContentDescription(
                        "Coder who learned and share")
                .setContentUrl(Uri.parse("http://instinctcoder.com"))
                .setImageUrl(Uri.parse("https://scontent-sin1-1.xx.fbcdn.net/hphotos-xap1/v/t1.0-9/12936641_845624472216348_1810921572759298872_n.jpg?oh=72421b8fa60d05e68c6fedbb824adfbf&oe=577949AA"))

                .build();
        fbShareButton.setShareContent(content);
    }

    private void GetUserInfo(){
        //this code will help us to obtain information from facebook, if
        //need some other field which not show here, please refer to https://developers.facebook.com/docs/graph-api/using-graph-api/
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        try{
                            String gender = object.getString("gender");
                            String birthday = object.getString("birthday");
                            String name = object.getString("name");
                            String id = object.getString("id");

                            txtName.setText(name);
                            txtURL.setText(id);
                            txtGender.setText(gender);
                            txtBd.setText(birthday);
                            if (object.has("picture")) {
                                String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                Picasso.with(MainActivity.this).load(profilePicUrl).into(imageView);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,gender,name,birthday,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();

    }




}
