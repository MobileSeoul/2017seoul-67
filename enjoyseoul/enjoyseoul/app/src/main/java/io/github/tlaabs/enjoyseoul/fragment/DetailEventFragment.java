package io.github.tlaabs.enjoyseoul.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.tlaabs.enjoyseoul.R;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class DetailEventFragment extends Fragment{

    //    Layout
    TextView periodView;
    TextView timeView;
    TextView regionView;
    TextView placeView;
    TextView targetView;
    TextView feeView;
    TextView inquiryView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_detail_event,null);
        periodView = (TextView)v.findViewById(R.id.period);
        timeView = (TextView)v.findViewById(R.id.time);
        regionView = (TextView)v.findViewById(R.id.region);
        placeView = (TextView)v.findViewById(R.id.place);
        targetView = (TextView)v.findViewById(R.id.target);
        feeView = (TextView)v.findViewById(R.id.fee);
        inquiryView = (TextView)v.findViewById(R.id.inquiry);

        Bundle extra = getArguments();
        periodView.setText(extra.getString("period"));
        timeView.setText(extra.getString("time"));
        regionView.setText(extra.getString("region"));
        placeView.setText(extra.getString("place"));
        targetView.setText(extra.getString("target"));
        feeView.setText(extra.getString("detailFee"));
        inquiryView.setText(extra.getString("inquiry"));

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
