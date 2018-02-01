package io.github.tlaabs.enjoyseoul.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import io.github.tlaabs.enjoyseoul.DetailEventActivity;
import io.github.tlaabs.enjoyseoul.R;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class ListViewHolder extends RecyclerView.ViewHolder{
    Context context;
    String id = "";
    ImageView itemImg;
    TextView genreAlert;
    TextView feeAlert;
    TextView title;
    RatingBar ratingBar;
    TextView rating;
    TextView period;
    TextView region;

    public void setContext(Context context){
        this.context = context;
    }
    public ListViewHolder(View itemView){
        super(itemView);
        itemImg = (ImageView)itemView.findViewById(R.id.item_img);
        genreAlert = (TextView)itemView.findViewById(R.id.genre_alert);
        feeAlert = (TextView)itemView.findViewById(R.id.fee_alert);
        title = (TextView)itemView.findViewById(R.id.title);
        ratingBar = (RatingBar)itemView.findViewById(R.id.rating_bar);
        rating = (TextView)itemView.findViewById(R.id.rating);
        period = (TextView)itemView.findViewById(R.id.period);
        region = (TextView)itemView.findViewById(R.id.region);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailEventActivity.class);
                i.putExtra("eventID",id);
                context.startActivity(i);
            }
        });

    }
}
