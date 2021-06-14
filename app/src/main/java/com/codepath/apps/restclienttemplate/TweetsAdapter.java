package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

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

        //This itemview that is passed in is a representation of one row in the recycler view, a tweet.
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvRelativeTimeAgo = itemView.findViewById(R.id.tvRelativeTimeAgo);


        }

        public void bind(Tweet tweet) {
            //Take out the different attributes of the tweet and use it to fill out the views on screen in the viewholder above
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvRelativeTimeAgo.setText(tweet.getRelativeTimeAgo(tweet.createdAt));
            //fill in profile image from the url
            Glide.with(context).load(tweet.user.publicImageUrl).into(ivProfileImage);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if(position != RecyclerView.NO_POSITION){
                Log.i("rkrkrk", "What is happening");
                Tweet tweet = tweets.get(position);
                Intent i = new Intent(context, TweetDetailActivity.class);
                i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                context.startActivity(i);

            }
        }
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
