package io.github.tlaabs.enjoyseoul.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.tlaabs.enjoyseoul.R;
import io.github.tlaabs.enjoyseoul.util.ReViewAdapter;
import io.github.tlaabs.enjoyseoul.util.ReviewItem;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class EventReviewFragment extends Fragment{
    ReViewAdapter mAdapter;
    RecyclerView mRecycleView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_review_event,null);
        mRecycleView = (RecyclerView)v.findViewById(R.id.review_list);

        mAdapter = new ReViewAdapter(getActivity());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycleView.setLayoutManager(mLayoutManager);

        mRecycleView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public void addData(ReviewItem item){
        mAdapter.add(item);
    }
    public void removeAllData(){mAdapter.dataClear();}
}
