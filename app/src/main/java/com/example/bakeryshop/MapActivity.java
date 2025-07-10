package com.example.bakeryshop;



import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final long LOCATION_UPDATE_INTERVAL = 10000L; // 10 seconds
    private static final long LOCATION_UPDATE_FASTEST_INTERVAL = 5000L; // 5 seconds
    private static final long LOCATION_TIMEOUT = 30000L; // 30 seconds timeout
    private static final float DEFAULT_ZOOM = 15f;
    private static final float CITY_ZOOM = 12f;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Geocoder geocoder;
    private Handler locationTimeoutHandler;
    private Runnable locationTimeoutRunnable;
    private ExecutorService executorService;

    // UI Components
    private EditText editTextAddress;
    private Button buttonUseCurrentLocation;
    private Button buttonSearch;
    private ImageButton buttonCurrentLocationIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initUI();
        initLocationServices();
        initMap();
        setupClickListeners();
    }

    private void initUI() {
        editTextAddress = findViewById(R.id.editTextAddress);
        buttonUseCurrentLocation = findViewById(R.id.buttonUseCurrentLocation);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonCurrentLocationIcon = findViewById(R.id.buttonCurrentLocationIcon);
    }

    private void initLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        locationTimeoutHandler = new Handler(Looper.getMainLooper());
        executorService = Executors.newSingleThreadExecutor();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                handleLocationResult(locationResult);
            }
        };
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupClickListeners() {
        buttonUseCurrentLocation.setOnClickListener(v -> getCurrentLocationAndFillAddress());

        buttonCurrentLocationIcon.setOnClickListener(v -> getCurrentLocationAndFillAddress());

        buttonSearch.setOnClickListener(v -> {
            String address = editTextAddress.getText().toString().trim();
            if (!address.isEmpty()) {
                searchAddress(address);
            } else {
                showToast("Vui lòng nhập địa chỉ");
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupMap();
        showDefaultLocation();
    }

    private void setupMap() {
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false); // We have our own button
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private void getCurrentLocationAndFillAddress() {
        if (checkLocationPermission()) {
            enableMyLocation();
            getCurrentLocation();
        } else {
            requestLocationPermission();
        }
    }

    private void enableMyLocation() {
        if (checkLocationPermission()) {
            try {
                if (mMap != null) {
                    mMap.setMyLocationEnabled(true);
                }
            } catch (SecurityException e) {
                Log.e(TAG, "Security exception when enabling location", e);
            }
        }
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void getCurrentLocation() {
        if (!checkLocationPermission()) {
            showDefaultLocation();
            return;
        }

        try {
            setLoadingState(true);
            setupLocationTimeout();

            LocationRequest locationRequest = new LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    LOCATION_UPDATE_INTERVAL)
                    .setMinUpdateIntervalMillis(LOCATION_UPDATE_FASTEST_INTERVAL)
                    .setMinUpdateDistanceMeters(10f)
                    .setMaxUpdateDelayMillis(15000)
                    .setWaitForAccurateLocation(true)
                    .build();

            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper());

            // Also try to get last known location as backup
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                handleLocationUpdate(location);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception exception) {
                            Log.e(TAG, "Failed to get last location", exception);
                        }
                    });

        } catch (SecurityException e) {
            Log.e(TAG, "Security exception when requesting location", e);
            setLoadingState(false);
            showToast("Không có quyền truy cập vị trí");
            showDefaultLocation();
        }
    }

    private void setupLocationTimeout() {
        locationTimeoutRunnable = new Runnable() {
            @Override
            public void run() {
                setLoadingState(false);
                fusedLocationClient.removeLocationUpdates(locationCallback);
                showToast("Timeout khi lấy vị trí. Vui lòng thử lại.");
                showDefaultLocation();
            }
        };
        locationTimeoutHandler.postDelayed(locationTimeoutRunnable, LOCATION_TIMEOUT);
    }

    private void clearLocationTimeout() {
        if (locationTimeoutRunnable != null) {
            locationTimeoutHandler.removeCallbacks(locationTimeoutRunnable);
            locationTimeoutRunnable = null;
        }
    }

    private void handleLocationResult(LocationResult locationResult) {
        Location location = locationResult.getLastLocation();
        if (location != null) {
            handleLocationUpdate(location);
        }
    }

    private void handleLocationUpdate(Location location) {
        clearLocationTimeout();
        setLoadingState(false);

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        // Stop location updates
        fusedLocationClient.removeLocationUpdates(locationCallback);

        // Update map
        updateMapWithLocation(currentLatLng, "Vị trí hiện tại của bạn");

        // Get address in background
        getAddressFromLocation(location.getLatitude(), location.getLongitude());
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            buttonUseCurrentLocation.setText("Đang lấy vị trí...");
            buttonUseCurrentLocation.setEnabled(false);
        } else {
            buttonUseCurrentLocation.setText("Sử dụng vị trí hiện tại");
            buttonUseCurrentLocation.setEnabled(true);
        }
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                String formattedAddress = formatVietnameseAddress(address);
                                editTextAddress.setText(formattedAddress);
                                showToast("Đã lấy địa chỉ thành công");

                                // Log for debugging
                                Log.d(TAG, "Address details: " + getAddressDetails(address));
                            } else {
                                showToast("Không thể lấy địa chỉ từ vị trí hiện tại");
                            }
                        }
                    });
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "Geocoding error", e);
                            showToast("Lỗi khi chuyển đổi địa chỉ: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

    private String getAddressDetails(Address address) {
        StringBuilder sb = new StringBuilder();
        sb.append("Số nhà: ").append(address.getSubThoroughfare()).append("\n");
        sb.append("Tên đường: ").append(address.getThoroughfare()).append("\n");
        sb.append("Phường/Xã: ").append(address.getSubLocality()).append("\n");
        sb.append("Quận/Huyện: ").append(address.getLocality()).append("\n");
        sb.append("Tỉnh/TP: ").append(address.getAdminArea()).append("\n");
        sb.append("Quốc gia: ").append(address.getCountryName()).append("\n");
        sb.append("Địa chỉ đầy đủ: ").append(address.getAddressLine(0)).append("\n");
        return sb.toString();
    }

    private String formatVietnameseAddress(Address address) {
        StringBuilder sb = new StringBuilder();

        // Street number and name
        StringBuilder streetInfo = new StringBuilder();
        if (address.getSubThoroughfare() != null) {
            streetInfo.append(address.getSubThoroughfare()).append(" ");
        }
        if (address.getThoroughfare() != null) {
            streetInfo.append(address.getThoroughfare());
        }

        if (streetInfo.length() > 0) {
            sb.append(streetInfo.toString());
        }

        // Ward/Commune
        if (address.getSubLocality() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(formatSubLocality(address.getSubLocality()));
        }

        // District
        if (address.getLocality() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(formatLocality(address.getLocality()));
        }

        // City/Province
        if (address.getAdminArea() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(formatAdminArea(address.getAdminArea()));
        }

        // If no detailed info, use full address
        if (sb.length() == 0) {
            String fullAddress = address.getAddressLine(0);
            sb.append(fullAddress != null ? fullAddress : "Địa chỉ không xác định");
        }

        return sb.toString();
    }

    private String formatSubLocality(String subLocality) {
        if (subLocality.toLowerCase().startsWith("phường") ||
                subLocality.toLowerCase().startsWith("xã")) {
            return subLocality;
        } else {
            return "P." + subLocality;
        }
    }

    private String formatLocality(String locality) {
        String lowerCase = locality.toLowerCase();
        if (lowerCase.startsWith("quận") ||
                lowerCase.startsWith("huyện") ||
                lowerCase.startsWith("thành phố")) {
            return locality;
        } else {
            return "Q." + locality;
        }
    }

    private String formatAdminArea(String adminArea) {
        String lowerCase = adminArea.toLowerCase();
        if (lowerCase.startsWith("thành phố") ||
                lowerCase.startsWith("tỉnh")) {
            return adminArea;
        } else {
            return "TP." + adminArea;
        }
    }

    private void searchAddress(String addressText) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Address> addresses = geocoder.getFromLocationName(addressText, 1);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                updateMapWithLocation(latLng, addressText);
                                showToast("Đã tìm thấy địa chỉ");
                            } else {
                                showToast("Không tìm thấy địa chỉ");
                            }
                        }
                    });
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "Address search error", e);
                            showToast("Lỗi khi tìm kiếm địa chỉ: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

    private void updateMapWithLocation(LatLng latLng, String title) {
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(title));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        }
    }

    private void showDefaultLocation() {
        // Default location: Can Tho, Vietnam
        LatLng defaultLocation = new LatLng(10.0452, 105.7469);
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(defaultLocation)
                    .title("Vị trí mặc định - Cần Thơ"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, CITY_ZOOM));
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
                getCurrentLocation();
            } else {
                showToast("Cần quyền truy cập vị trí để hiển thị vị trí hiện tại");
                showDefaultLocation();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearLocationTimeout();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}