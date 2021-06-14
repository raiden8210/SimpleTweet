package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";
    public static final int REQUEST_CODE = 20;

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;

    SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //Will return to us a client, an instance of TwitterClient
        client = TwitterApp.getRestClient(this);

        //Find the recycler vew
        rvTweets = findViewById(R.id.rvTweets);
        // Initialize the lkist of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        // Recycler view setup: layout manager and the adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimeLineAsync(0);
            }
        });


        populateHomeTimeline();


    }

    private void fetchTimeLineAsync(int i) {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                adapter.clear();
                populateHomeTimeline();
                adapter.addAll(tweets);
//                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);

                //If the below code is added, which is the exact same as the populateHomeTimeline method, then there is no screen flash. Otherwise, there is.
//                Log.i(TAG, "onSuccess"+json.toString());
//                //Call method in the Tweet Model
//                JSONArray jsonArray = json.jsonArray;
//                try {
//                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
//                    adapter.notifyDataSetChanged();
//                    swipeContainer.setRefreshing(false);
//                } catch (JSONException e) {
//                    Log.e(TAG, "Json exception");
//                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu, this adds items to the action bar if it is present
        //This method returns a boolean
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.compose){
            //compose icon has been selected
            //Navigate to the compose activity where you can make a tweet
            Intent i = new Intent(this, ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            //Get data from the intent(the tweet)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet")); //returns tweet object, we have access to it in the Timeline Activity now
            //Update the recycler view with this new tweet
            //Modify data source of tweets
            tweets.add(0, tweet);
            //Update the adapter
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                //Properly getting JSON data back from the API
                Log.i(TAG, "onSuccess"+json.toString());
                //Call method in the Tweet Model
                JSONArray jsonArray = json.jsonArray;
                try {
                   tweets.addAll(Tweet.fromJsonArray(jsonArray));
                   adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure" + response, throwable);
            }
        });

    }
}