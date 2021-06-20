package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    //Use Android Snackbar
    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 280;

    public static final String EXTRA_REPLY = "isReplying";
    public static final String EXTRA_REPLY_TO_ID = "tweetIdToReplyTo";
    public static final String EXTRA_REPLY_TO_USERNAME = "tweetUsernameToReplyTo";

    EditText etCompose;
    Button btnTweet;
    User user;
    TextView tvScreenName;
    boolean isReplying;
    TwitterClient client;
    long tweetIdtoReplyTo = -1;
    String tweetUsernametoReplyTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_compose);
        ActivityComposeBinding binding = ActivityComposeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        tvScreenName = findViewById(R.id.tvScreenName);
        client = TwitterApp.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        isReplying = getIntent().getBooleanExtra(ComposeActivity.EXTRA_REPLY, false);

        if(isReplying) {
            tweetIdtoReplyTo = getIntent().getLongExtra(ComposeActivity.EXTRA_REPLY_TO_ID, -1);
            tweetUsernametoReplyTo = getIntent().getStringExtra(ComposeActivity.EXTRA_REPLY_TO_USERNAME);
            etCompose.setText("@" + tweetUsernametoReplyTo + " ");
        }

        // Set a click listener on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Make sure tweet is not too short or long and notify the user
                final String tweetContent = etCompose.getText().toString();

                if(tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if(tweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Your tweet cannot be longer than 140 characters", Toast.LENGTH_LONG).show();
                    return;
                }

                client.publishTweet(tweetContent, tweetIdtoReplyTo, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                         Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says:"+ tweet.body);
                            Intent i = new Intent();
                            //Issue here with AS and complex data, doesn't know how to pass arbitrary variable data here
                            //Making tweet object parcelable by put extra
                            i.putExtra("tweet", Parcels.wrap(tweet));
                            //Set result code and bundle data for response
                            setResult(RESULT_OK, i);
                            //Closes activity
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });




                // Make API call once button is clicked to Twitter to publish tweet

            }
        });


    }

}