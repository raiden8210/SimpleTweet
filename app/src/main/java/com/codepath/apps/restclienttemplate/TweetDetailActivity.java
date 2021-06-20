package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;


public class TweetDetailActivity extends AppCompatActivity implements TweetsAdapter.OnTweetClickListener{

    private TweetsAdapter.OnTweetClickListener listener;
    public static final int REQUEST_CODE = 20;
    Tweet tweet;
    TextView tvBody;
    TextView tvScreenName;
    ImageView ivProfileImage;
    TextView name;
    ImageButton ibRetweet;
    ImageButton ibReply;
    ImageButton ibLike;
    ImageView ivPostedImage;


    List<Tweet> tweets;
    TweetsAdapter adapter;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_tweet_detail);
        ActivityTweetDetailBinding binding = ActivityTweetDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        tvBody = findViewById(R.id.tvBody);
        tvScreenName = findViewById(R.id.tvScreenName);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ibRetweet = findViewById(R.id.ibRetweet);
        ibReply = findViewById(R.id.ibReply);
        ibLike = findViewById(R.id.ibLike);
        ivPostedImage = findViewById(R.id.ivPostedImage);

        client = TwitterApp.getRestClient(this);

        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, this, tweets);

        name = findViewById(R.id.tvName);
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        name.setText(tweet.getUser().name);
        tvBody.setText(tweet.getBody());
        tvScreenName.setText(tweet.getUser().screenName);
        Glide.with(this).load(tweet.user.publicImageUrl).into(ivProfileImage);

        if (tweet.mediaUrl != "") {
            ivPostedImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(tweet.mediaUrl).fitCenter().override(640, 360).transform(new RoundedCorners(25)).into(ivPostedImage);
        } else {
            ivPostedImage.setVisibility(View.GONE);
        }

        ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
        ibReply.setImageResource(R.drawable.ic_vector_messages_stroke);
        ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);

        ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tweet.retweet){
                    ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
                } else{
                    ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                }
            }
        });

        ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tweet.like){
                    ibLike.setImageResource(R.drawable.ic_vector_heart);
                } else{
                    ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
                }
            }
        });


    }

    @Override
    public void onLike(final int pos, boolean isChecked) {
        if(!isChecked){
            client.like(tweets.get(pos).id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    tweets.get(pos).like = true;
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                }
            });
        } else{
            client.unlike(tweets.get(pos).id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    tweets.get(pos).like = false;
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                }
            });
        }
    }

    @Override
    public void onReply(int pos) {

        Intent i = new Intent(this, ComposeActivity.class);
        i.putExtra("user", tweets.get(pos).user.screenName);
        i.putExtra("reply", true);
        startActivityForResult(i, REQUEST_CODE);

    }

    @Override
    public void onRetweet(final int pos, boolean isChecked) {

        if(!isChecked){
            client.retweet(tweets.get(pos).id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    tweets.get(pos).retweet = true;
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                }
            });
        } else{
            client.unretweet(tweets.get(pos).id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    tweets.get(pos).retweet = false;
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                }
            });
        }

    }
}