package com.example.bakeryshop;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class Local extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Button buttonBack;
    private Button buttonMapType;
    private int currentMapType = GoogleMap.MAP_TYPE_HYBRID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        // Thiết lập toolbar với nút back
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Vị trí FPT University");
            }
        }



        // Khởi tạo map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Xử lý nút back trên toolbar
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set map type to hybrid (type 3)
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Bật các tính năng UI cho map
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);    // Hiển thị nút zoom +/-
        uiSettings.setZoomGesturesEnabled(true);    // Cho phép zoom bằng cử chỉ
        uiSettings.setScrollGesturesEnabled(true);  // Cho phép kéo thả map
        uiSettings.setTiltGesturesEnabled(true);    // Cho phép nghiêng map
        uiSettings.setRotateGesturesEnabled(true);  // Cho phép xoay map
        uiSettings.setMapToolbarEnabled(true);      // Hiển thị toolbar của map
        uiSettings.setCompassEnabled(true);         // Hiển thị la bàn
        uiSettings.setMyLocationButtonEnabled(true); // Nút vị trí hiện tại

        // Hiển thị vị trí FPT University
        showFPTUniversity();
    }

    private void showFPTUniversity() {
        // Tọa độ chính xác của FPT University Cần Thơ
        LatLng latLng1 = new LatLng(10.014351444089163, 105.73195414291693);
        LatLng latLng2 = new LatLng(10.0131325944814, 105.73308205124621);
        LatLng latLng3 = new LatLng(10.011835104384474, 105.73156486482098);
        LatLng latLng4 = new LatLng(10.012149647914622, 105.73133529055927);
        LatLng latLng5 = new LatLng(10.012542826898608, 105.73055673436738);
        LatLng latLng6 = new LatLng(10.012778734060348, 105.73019740074037);
        LatLng latLng7 = new LatLng(10.013053958865623, 105.73046690096064);

        // Tạo polyline với các điểm tọa độ
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(latLng1)
                .add(latLng2)
                .add(latLng3)
                .add(latLng4)
                .add(latLng5)
                .add(latLng6)
                .add(latLng7)
                .add(latLng1) // Quay lại điểm đầu để tạo thành polygon
                .color(Color.RED)
                .width(5f);

        // Thêm polyline vào map
        mMap.addPolyline(polylineOptions);

        // Thêm marker cho FPT University Cần Thơ
        LatLng fptLocation = new LatLng(10.013093276675898, 105.73158482780026);
        mMap.addMarker(new MarkerOptions()
                .position(fptLocation)
                .title("FPT University Cần Thơ")
                .snippet("Khu Công nghệ cao, Đường Nguyễn Văn Cừ, Cần Thơ"));

        // Di chuyển camera đến vị trí FPT University với zoom phù hợp
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fptLocation, 17f));
    }

    private void toggleMapType() {
        if (mMap != null) {
            switch (currentMapType) {
                case GoogleMap.MAP_TYPE_HYBRID:
                    currentMapType = GoogleMap.MAP_TYPE_NORMAL;
                    buttonMapType.setText("Normal");
                    break;
                case GoogleMap.MAP_TYPE_NORMAL:
                    currentMapType = GoogleMap.MAP_TYPE_SATELLITE;
                    buttonMapType.setText("Satellite");
                    break;
                case GoogleMap.MAP_TYPE_SATELLITE:
                    currentMapType = GoogleMap.MAP_TYPE_TERRAIN;
                    buttonMapType.setText("Terrain");
                    break;
                case GoogleMap.MAP_TYPE_TERRAIN:
                    currentMapType = GoogleMap.MAP_TYPE_HYBRID;
                    buttonMapType.setText("Hybrid");
                    break;
            }
            mMap.setMapType(currentMapType);
        }
    }
}