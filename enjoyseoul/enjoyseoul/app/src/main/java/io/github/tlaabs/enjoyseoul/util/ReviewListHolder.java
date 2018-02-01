package io.github.tlaabs.enjoyseoul.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import io.github.tlaabs.enjoyseoul.R;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class ReviewListHolder extends RecyclerView.ViewHolder{
    RatingBar ratingBar;
    TextView ratingView;
    TextView contentView;
    TextView authorView;
    TextView timeView;

    public ReviewListHolder(View itemView) {
        super(itemView);
        ratingBar = (RatingBar)itemView.findViewById(R.id.rating_bar);
        ratingView = (TextView)itemView.findViewById(R.id.rating);
        contentView = (TextView)itemView.findViewById(R.id.content);
        authorView = (TextView)itemView.findViewById(R.id.author);
        timeView = (TextView)itemView.findViewById(R.id.time);
    }
}
