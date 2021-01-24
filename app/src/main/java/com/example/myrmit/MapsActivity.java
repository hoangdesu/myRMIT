package com.example.myrmit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.myrmit.model.FirebaseHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private FacilityCardAdapter facilityCardAdapter;
    private ViewPager viewPager;
    private List<Facility> facilities = new ArrayList<Facility>();;
    private FirebaseHandler firebaseHandler = new FirebaseHandler();
    String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Add a marker in Sydney and move the camera

        LatLng rmitLoc = new LatLng(10.7296, 106.693);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(rmitLoc).bearing(90).zoom((float) 16.8).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        PolygonOptions building2 = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.72948, 106.6959), new LatLng(10.72948, 106.6967), new LatLng(10.72915, 106.6967), new LatLng(10.72915, 106.6959));
        PolygonOptions building1 = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.7294, 106.6946), new LatLng(10.7294, 106.6957), new LatLng(10.7291, 106.6957), new LatLng(10.7291, 106.6946));
        PolygonOptions parkingLot1 = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.7299, 106.6961), new LatLng(10.7299, 106.6968), new LatLng(10.72965, 106.6968), new LatLng(10.72965, 106.6961));
        PolygonOptions parkingLot2 = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.7298, 106.6949), new LatLng(10.7298, 106.69567), new LatLng(10.72957, 106.69567), new LatLng(10.72957, 106.6949));
        PolygonOptions cafeteria = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.7298, 106.6944), new LatLng(10.7298, 106.6948), new LatLng(10.72957, 106.6948), new LatLng(10.72957, 106.6944));
        PolygonOptions basketballCourt = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.72975, 106.69415), new LatLng(10.72975, 106.6943), new LatLng(10.72954, 106.6943), new LatLng(10.72954, 106.69415));
        PolygonOptions building9 = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.7298, 106.6933), new LatLng(10.7298, 106.6937), new LatLng(10.72965, 106.6937), new LatLng(10.72965, 106.6935), new LatLng(10.72955, 106.6935), new LatLng(10.72955, 106.6933));
        PolygonOptions building8 = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.7294, 106.6933), new LatLng(10.7294, 106.6935), new LatLng(10.72925, 106.6935), new LatLng(10.72925, 106.6937), new LatLng(10.7291, 106.6937), new LatLng(10.7291, 106.6933));
        PolygonOptions sportHall = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.72965, 106.6923), new LatLng(10.72965, 106.6924), new LatLng(10.72975, 106.6924), new LatLng(10.72975, 106.693), new LatLng(10.72965, 106.693), new LatLng(10.72965, 106.6931),new LatLng(10.7292, 106.6931), new LatLng(10.7292, 106.6930), new LatLng(10.7291, 106.6930), new LatLng(10.7291, 106.6924), new LatLng(10.7292, 106.6924), new LatLng(10.7292, 106.6923));
        PolygonOptions footballField = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.7303, 106.6912), new LatLng(10.7303, 106.6922), new LatLng(10.7293, 106.6922), new LatLng(10.7293, 106.6912));
        PolygonOptions tennisCourt = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.7307, 106.69225), new LatLng(10.7307, 106.69267), new LatLng(10.7303, 106.69267), new LatLng(10.7303, 106.69225));

        mMap.addPolygon(building1);
        mMap.addPolygon(building2);
        mMap.addPolygon(parkingLot1);
        mMap.addPolygon(parkingLot2);
        mMap.addPolygon(cafeteria);
        mMap.addPolygon(basketballCourt);
        mMap.addPolygon(building9);
        mMap.addPolygon(building8);
        mMap.addPolygon(sportHall);
        mMap.addPolygon(footballField);
        mMap.addPolygon(tennisCourt);


        int height = 70;
        int width = 70;
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        MarkerOptions building2Marker = new MarkerOptions().position(new LatLng(10.72948, 106.6959)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Building 2");
        MarkerOptions building1Marker = new MarkerOptions().position(new LatLng(10.7291, 106.6946)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Building 1");
        MarkerOptions building8Marker = new MarkerOptions().position(new LatLng(10.72925, 106.6937)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Building 8");
        MarkerOptions building9Marker = new MarkerOptions().position(new LatLng(10.7298, 106.6933)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Building 9");
        mMap.addMarker(building2Marker);
        mMap.addMarker(building1Marker);
        mMap.addMarker(building8Marker);
        mMap.addMarker(building9Marker);

        bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.parking);
        b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        MarkerOptions parkingLot1Marker = new MarkerOptions().position(new LatLng(10.7299, 106.6963)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Parking Lot 1");
        MarkerOptions parkingLot2Marker = new MarkerOptions().position(new LatLng(10.7298, 106.6953)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Parking Lot 2");
        mMap.addMarker(parkingLot1Marker);
        mMap.addMarker(parkingLot2Marker);

        bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.tennis);
        b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        MarkerOptions tennisCourtMarker = new MarkerOptions().position(new LatLng(10.7306, 106.69245)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Tennis Court");
        mMap.addMarker(tennisCourtMarker);

        bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.football);
        b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        MarkerOptions footballFieldMarker = new MarkerOptions().position(new LatLng(10.7296, 106.69175)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Football Field");
        mMap.addMarker(footballFieldMarker);

        bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.sports);
        b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        MarkerOptions sportHallMarker = new MarkerOptions().position(new LatLng(10.7293, 106.69275)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Sport Hall");
        mMap.addMarker(sportHallMarker);

        bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.net);
        b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        MarkerOptions basketballCourtMarker = new MarkerOptions().position(new LatLng(10.72979, 106.6942)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Basketball Court");
        mMap.addMarker(basketballCourtMarker);

        bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.food);
        b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        MarkerOptions cafeteriaMarker = new MarkerOptions().position(new LatLng(10.72965, 106.6946)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Cafeteria");
        mMap.addMarker(cafeteriaMarker);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (int i = 0; i < facilities.size(); i++) {
                    if (facilities.get(i).getTitle().equals(marker.getTitle())) {
                        viewPager.setCurrentItem(i);
                    }
                }
                return true;
            }
        });

        firebaseHandler.getFacilities().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String title, openHour, image;
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    title = Objects.requireNonNull(documentSnapshot.getData().get("title")).toString();
                    openHour = Objects.requireNonNull(documentSnapshot.getData().get("open")).toString();
                    image = Objects.requireNonNull(documentSnapshot.getData().get("image")).toString();
                    facilities.add(new Facility(image,title,openHour));
                }

                System.out.println("facilities size: " + facilities.size());
                facilityCardAdapter.notifyDataSetChanged();
            }


        });

        facilityCardAdapter = new FacilityCardAdapter(facilities, MapsActivity.this);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(facilityCardAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}