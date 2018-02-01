package io.github.tlaabs.enjoyseoul.util;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.github.tlaabs.enjoyseoul.R;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class ReViewAdapter extends RecyclerView.Adapter<ReviewListHolder>{
    Context context;
    ArrayList<ReviewItem> items = new ArrayList<ReviewItem>();

    public ReViewAdapter(Context context){
        this.context = context;
    }
    @Override
    public ReviewListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        try {
            v = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.review_list_item, parent, false
            );
        }catch(Exception e){
            e.printStackTrace();
        }

        ReviewListHolder vh = new ReviewListHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ReviewListHolder holder, int position) {
        ReviewItem item = items.get(position);
        holder.ratingView.setText(item.getRating());
        Float jumsu = Float.parseFloat(item.getRating());
        jumsu /= 2.0f;
        holder.ratingBar.setRating(jumsu);
        //ratingbar color
        LayerDrawable stars = (LayerDrawable)holder.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(context.getResources().getColor(R.color.colorStar), PorterDuff.Mode.SRC_IN);
        stars.getDrawable(1).setColorFilter(context.getResources().getColor(R.color.colorDarkGray), PorterDuff.Mode.SRC_IN);
        stars.getDrawable(0).setColorFilter(context.getResources().getColor(R.color.colorDarkGray), PorterDuff.Mode.SRC_IN);

        holder.contentView.setText(item.getContent());
        holder.authorView.setText("작성자 : " + item.getAuthor());
        holder.timeView.setText(item.getTime());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void add(ReviewItem data) {
        items.add(data);
        notifyDataSetChanged();
    }

    public void dataClear(){
        items.clear();
        notifyDataSetChanged();
    }
}
