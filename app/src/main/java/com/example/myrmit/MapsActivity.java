package com.example.myrmit;

import androidx.fragment.app.FragmentActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";

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

        LatLng rmitLoc = new LatLng(10.7285, 106.6940);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rmitLoc, (float)16.4));
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        PolygonOptions building2 = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.72948, 106.6959), new LatLng(10.72948, 106.6967), new LatLng(10.72915, 106.6967), new LatLng(10.72915, 106.6959)).strokeWidth(2);
        PolygonOptions building1 = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.7294, 106.6946), new LatLng(10.7294, 106.6957), new LatLng(10.7291, 106.6957), new LatLng(10.7291, 106.6946)).strokeWidth(2);
        PolygonOptions parkingLot1 = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.7299, 106.6961), new LatLng(10.7299, 106.6968), new LatLng(10.72965, 106.6968), new LatLng(10.72965, 106.6961)).strokeWidth(2);
        PolygonOptions parkingLot2 = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.7298, 106.6949), new LatLng(10.7298, 106.69567), new LatLng(10.72957, 106.69567), new LatLng(10.72957, 106.6949)).strokeWidth(2);
        PolygonOptions cafeteria = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.7298, 106.6944), new LatLng(10.7298, 106.6948), new LatLng(10.72957, 106.6948), new LatLng(10.72957, 106.6944)).strokeWidth(2);
        PolygonOptions basketballCourt = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.72975, 106.69415), new LatLng(10.72975, 106.6943), new LatLng(10.72954, 106.6943), new LatLng(10.72954, 106.69415)).strokeWidth(2);
        PolygonOptions building9 = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.7298, 106.6933), new LatLng(10.7298, 106.6937), new LatLng(10.72965, 106.6937), new LatLng(10.72965, 106.6935), new LatLng(10.72955, 106.6935), new LatLng(10.72955, 106.6933)).strokeWidth(2);
        PolygonOptions building8 = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.7294, 106.6933), new LatLng(10.7294, 106.6935), new LatLng(10.72925, 106.6935), new LatLng(10.72925, 106.6937), new LatLng(10.7291, 106.6937), new LatLng(10.7291, 106.6933)).strokeWidth(2);
        PolygonOptions sportHall = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.72965, 106.6923), new LatLng(10.72965, 106.6924), new LatLng(10.72975, 106.6924), new LatLng(10.72975, 106.693), new LatLng(10.72965, 106.693), new LatLng(10.72965, 106.6931),new LatLng(10.7292, 106.6931), new LatLng(10.7292, 106.6930), new LatLng(10.7291, 106.6930), new LatLng(10.7291, 106.6924), new LatLng(10.7292, 106.6924), new LatLng(10.7292, 106.6923)).strokeWidth(2);
        PolygonOptions footballField = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.7303, 106.6912), new LatLng(10.7303, 106.6922), new LatLng(10.7293, 106.6922), new LatLng(10.7293, 106.6912)).strokeWidth(2);
        PolygonOptions tennisCourt = new PolygonOptions().fillColor(R.color.land_mark).add(new LatLng(10.7307, 106.69225), new LatLng(10.7307, 106.69267), new LatLng(10.7303, 106.69267), new LatLng(10.7303, 106.69225)).strokeWidth(2);

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
    }
}