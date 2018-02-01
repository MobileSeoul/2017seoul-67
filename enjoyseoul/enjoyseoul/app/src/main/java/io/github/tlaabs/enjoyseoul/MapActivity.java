package io.github.tlaabs.enjoyseoul;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import io.github.tlaabs.enjoyseoul.util.MapItemInfo;
/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class MapActivity extends FragmentActivity implements OnMapReadyCallback{
    ArrayList<MapItemInfo> list;
    private GoogleMap mMap;
    String title = null;

    ImageView backBtn;
    TextView appTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        backBtn = (ImageView)findViewById(R.id.back_btn);
        appTitle = (TextView)findViewById(R.id.app_title);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        appTitle.setText(title);

        getPosData();

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getPosData(){
        Intent intent = getIntent();
        list = (ArrayList<MapItemInfo>)intent.getSerializableExtra("LatLngs");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        for(int i = 0 ; i < list.size(); i++){
            MapItemInfo item = list.get(i);
            LatLng latLng = new LatLng(item.getY(),item.getX());
            mMap.addMarker(new MarkerOptions().position(latLng).title(item.getPlaceName()));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(18)
                    .build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }
}
