package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private OnTweetClickListener listener;

    Context context;
    List<Tweet> tweets;

    public interface OnTweetClickListener {
        void onProfileImageClick(User user);
        void onLike(int pos, boolean isChecked);
        void onReply(int pos);
        void onRetweet(int pos, boolean isChecked);
    }


    public TweetsAdapter(Context context, OnTweetClickListener listener, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
        this.listener = listener;
    }

//    public TweetsAdapter(Context context, List<Tweet> tweets) {
//        this.context = context;
//        this.tweets = tweets;
//    }

    // Pass in the context and list of tweets. Used throughout the adapter and methods
    @NonNull
    @NotNull
    @Override
    //For each row, inflate a layout for a tweet
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate((R.layout.item_tweet), parent, false);
        //Wrap this view in a viewholder. This viewholder is referring to the inner class viewholder we just defined
        return new ViewHolder(view);
    }

    //Bind values based on the position
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        //Get the data
        Tweet tweet = tweets.get(position);
        //Bind the tweet with the viewholder that we jsut passed in
        //We make the bind method.
        holder.bind(tweet);


    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }



    //Define a viewholder

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvRelativeTimeAgo;
        ImageButton ibLike;
        ImageButton ibRetweet;
        ImageButton ibReply;

        //This itemview that is passed in is a representation of one row in the recycler view, a tweet.
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvRelativeTimeAgo = itemView.findViewById(R.id.tvRelativeTimeAgo);
            ibLike = itemView.findViewById(R.id.ibLike);
            ibReply = itemView.findViewById(R.id.ibReply);
            ibRetweet = itemView.findViewById(R.id.ibRetweet);
            //client = TwitterApp.getRestClient(context);

            //itemView.setOnClickListener(this);

        }

        public void bind(final Tweet tweet) {
            //Take out the different attributes of the tweet and use it to fill out the views on screen in the viewholder above
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvRelativeTimeAgo.setText(tweet.getRelativeTimeAgo(tweet.createdAt));
            //fill in profile image from the url
            Glide.with(context).load(tweet.user.publicImageUrl).circleCrop().into(ivProfileImage);
            ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
            ibReply.setImageResource(R.drawable.ic_vector_messages_stroke);
            ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);

            tvBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        Tweet tweet = tweets.get(position);
                        Intent i = new Intent(context, TweetDetailActivity.class);
                        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                        context.startActivity(i);
                    }


                }
            });

            ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("rkrkrk", "Like clicked");
                    if(!tweet.like){
                        listener.onLike(getLayoutPosition(), false);
                        ibLike.setImageResource(R.drawable.ic_vector_heart);
                    } else{
                        listener.onLike(getLayoutPosition(), true);
                        ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
                    }


//                    client.like(tweets.get(getLayoutPosition()), new JsonHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Headers headers, JSON json) {
//                            Log.i("rkrk", "" + tweet.id);
//                            tweets.get(getAdapterPosition()).like = false;
//                            ibLike.setImageResource(R.drawable.ic_vector_heart);
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//
//                        }
//                    });

//                    if(!tweet.like){
//                            listener.onLike(getLayoutPosition(), false);
//                            ibLike.setImageResource(R.drawable.ic_vector_heart);
//                    }
//                    else{
//                        listener.onLike(getLayoutPosition(), true);
//                        ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
//                    }

                }
            });

            ibRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!tweet.retweet){
                        listener.onRetweet(getLayoutPosition(), false);
                        ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
                    } else{
                        listener.onRetweet(getLayoutPosition(), true);
                        ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                    }

                }
            });

            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                Tweet tweet = tweets.get(position);
                Intent i = new Intent(context, TweetDetailActivity.class);
                i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                context.startActivity(i);
            }

        }


//        @Override
//        public void onClick(View view) {
//            int position = getAdapterPosition();
//
//            if(position != RecyclerView.NO_POSITION){
//                Tweet tweet = tweets.get(position);
//                Intent i = new Intent(context, TweetDetailActivity.class);
//                i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
//                context.startActivity(i);
//
//            }
//        }

    }
    //Refresh here

    public void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list){
        tweets.addAll(list);
        notifyDataSetChanged();
    }

}
