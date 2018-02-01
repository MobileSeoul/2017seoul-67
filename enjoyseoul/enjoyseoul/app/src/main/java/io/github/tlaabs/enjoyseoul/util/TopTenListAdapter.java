package io.github.tlaabs.enjoyseoul.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.tlaabs.enjoyseoul.R;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class TopTenListAdapter extends RecyclerView.Adapter<TopTenListViewHolder> {
    Context context;
    HashMap<String, Integer> genreColorMap;
    List<EventItem> items = new ArrayList<EventItem>();

    public TopTenListAdapter(Context context, HashMap<String, Integer> genreColorMap) {
        this.genreColorMap = genreColorMap;
        this.context = context;
    }

    @Override
    public TopTenListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.top_event_item, parent, false
        );
        TopTenListViewHolder vh = new TopTenListViewHolder(v);
        vh.setContext(context);
        return vh;
    }

    @Override
    public void onBindViewHolder(TopTenListViewHolder holder, int position) {
        GradientDrawable gradient;
        EventItem item = items.get(position);
        holder.id = item.getId();
        Glide
                .with(context)
                .asBitmap()
                .load(item.getImgSrc())
                .into(holder.itemImg);

        //genre
        String genre = item.getGenre();
        holder.genreAlert.setText(genre);

        String[] colors = context.getResources().getStringArray(R.array.genre_colors);
        int gen = genreColorMap.get(genre);
        gradient = (GradientDrawable) holder.genreAlert.getBackground();
        gradient.setColor(Color.parseColor(colors[gen]));

        //genre end
        //ratingbar color
        LayerDrawable stars = (LayerDrawable)holder.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(context.getResources().getColor(R.color.colorStar), PorterDuff.Mode.SRC_IN);
        stars.getDrawable(1).setColorFilter(context.getResources().getColor(R.color.colorDarkGray), PorterDuff.Mode.SRC_IN);
        stars.getDrawable(0).setColorFilter(context.getResources().getColor(R.color.colorDarkGray), PorterDuff.Mode.SRC_IN);

        //view reset
        if(item.getRating() == null) {
            holder.ratingBar.setRating(0);
            holder.rating.setText("평가 없음");
        }else{
            holder.ratingBar.setRating(Float.parseFloat(item.getRating()));
            holder.rating.setText(item.getRating() + " ("+item.getRating_n()+"명)");
        }
        if(!item.isLoadRating()) {
            item.setLoadRating(true);
            GetRatingTask task = new GetRatingTask(position, holder);
            task.execute();
        }

        //fee
        String fee = item.getFee();

        if (fee.equals("0")) {
            int color = ContextCompat.getColor(context, R.color.feeNFree);
            gradient = (GradientDrawable) holder.feeAlert.getBackground();
            gradient.setColor(color);
            holder.feeAlert.setText("유료");
        } else if (fee.equals("1")) {
            int color = ContextCompat.getColor(context, R.color.feeFree);
            gradient = (GradientDrawable) holder.feeAlert.getBackground();
            gradient.setColor(color);
            holder.feeAlert.setText("무료");
        }
        //fee end
        holder.title.setText(item.getTitle());
        holder.period.setText(item.getStartDate() + "~" + item.getEndDate());
        holder.region.setText(item.getRegion());

        //rank
        gradient = (GradientDrawable) holder.rankDiv.getBackground();
        gradient.setColor(context.getResources().getColor(R.color.colorMainPink));
        holder.rank.setText((position+1) + "");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void add(EventItem data) {
        items.add(data);
    }

    private class GetRatingTask extends AsyncTask<Integer, Integer, String> {
        int position;
        TopTenListViewHolder holder;

        public GetRatingTask(int position, TopTenListViewHolder holder) {
            this.position = position;
            this.holder = holder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
            //params -> position
            try {
                URL url = new URL(context.getString(R.string.server));

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setDefaultUseCaches(false);

                POSTParams postParams = new POSTParams();
                postParams.addParam("method", "get_rating");
                postParams.addParam("cultcode", (items.get(position)).getId());



                String param = postParams.toString();

                OutputStream os = conn.getOutputStream();
                os.write(param.getBytes("UTF-8"));
                os.flush();
                os.close();

                InputStream is = null;
                BufferedReader in = null;
                String data = "";

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 1024);

                String line = null;
                StringBuffer buff = new StringBuffer();

                while ((line = in.readLine()) != null) {
                    buff.append(line);
                }
                data = buff.toString().trim();
                return data;

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s == null) {
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("rating");
                JSONObject jo = jsonArray.getJSONObject(0);
                double ratingSum_org = jo.getDouble("sum");
                int n = jo.getInt("count");
                double ratingSum = ratingSum_org / n;
                holder.ratingBar.setRating((float) ratingSum / 2);
                ratingSum = Double.parseDouble(String.format("%.1f", ratingSum));
                items.get(position).setRating(ratingSum+"");
                items.get(position).setRating_n(n);

                holder.rating.setText(ratingSum + " ("+n+"명)");


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
