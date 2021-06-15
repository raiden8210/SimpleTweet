package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcel;
import org.parceler.Parcels;


public class TweetDetailActivity extends AppCompatActivity {

    Tweet tweet;
    TextView tvBody;
    TextView tvScreenName;
    ImageView ivProfileImage;
    TextView name;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        tvBody = findViewById(R.id.tvBody);
        tvScreenName = findViewById(R.id.tvScreenName);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        name = findViewById(R.id.tvName);
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        name.setText(tweet.getUser().name);
        tvBody.setText(tweet.getBody());
        tvScreenName.setText(tweet.getUser().screenName);
        Glide.with(this).load(tweet.user.publicImageUrl).into(ivProfileImage);

    }
}