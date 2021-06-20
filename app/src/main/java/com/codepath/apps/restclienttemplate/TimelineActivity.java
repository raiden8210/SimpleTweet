package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

import static com.facebook.stetho.inspector.network.PrettyPrinterDisplayType.JSON;

public class TimelineActivity extends AppCompatActivity implements TweetsAdapter.OnTweetClickListener {

    public static final String TAG = "TimelineActivity";
    public static final int REQUEST_CODE = 20;

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    ImageView ivProfilePicture;
    private Long lowestid;

    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_timeline);
        ActivityTimelineBinding binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Will return to us a client, an instance of TwitterClient
        client = TwitterApp.getRestClient(this);
        rvTweets = findViewById(R.id.rvTweets);
        ivProfilePicture = findViewById(R.id.ivProfileImage);
        swipeContainer = findViewById(R.id.swipeContainer);

        tweets = new ArrayList<>();
        //Find the recycler vew
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Initialize the list of tweets and adapter

        adapter = new TweetsAdapter(this, this, tweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // Recycler view setup: layout manager and the adapter
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(adapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimeLineAsync(0);
            }
        });


        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi();
            }
        };

        rvTweets.addOnScrollListener(scrollListener);
        rvTweets.addItemDecoration(new DividerItemDecoration(rvTweets.getContext(), DividerItemDecoration.VERTICAL));

        populateHomeTimeline();
    }

    private void loadNextDataFromApi() {
        long max_id = tweets.get(tweets.size() - 1).id;
        client.getNext(new JsonHttpResponseHandler(){
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                JSONArray jsonArray = json.jsonArray;
                                try {
                                    List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);
                                    int position = tweets.size()-1;
                                    adapter.addAll(tweets);
                                    adapter.notifyItemInserted(position);
                                } catch(JSONException e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        }, max_id);
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


    //On the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.compose){
            //compose icon has been selected
            //Navigate to the compose activity where you can make a tweet
            Intent i = new Intent(this, ComposeActivity.class);
            i.putExtra("isReplying", false);
            startActivityForResult(i, REQUEST_CODE);
            return true;
        }

        if(item.getItemId() == R.id.homeClickTop){
            rvTweets.smoothScrollToPosition(0);
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
        // navigate to ComposeActivity in order to reply to someone else's tweet
        // (coming from the "reply" ImageButton)
        Tweet tweetThatWasClicked = tweets.get(pos);
        long idOfTweetToReplyTo = tweetThatWasClicked.id;

        Intent i = new Intent(this, ComposeActivity.class);
        i.putExtra("user", tweets.get(pos).user.screenName);
        i.putExtra(ComposeActivity.EXTRA_REPLY, true);
        i.putExtra(ComposeActivity.EXTRA_REPLY_TO_ID, idOfTweetToReplyTo);
        i.putExtra(ComposeActivity.EXTRA_REPLY_TO_USERNAME, tweetThatWasClicked.user.screenName);
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