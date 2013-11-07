package com.jcrastelli.wikinearme;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity
		implements
		ConnectionCallbacks,
		OnConnectionFailedListener,
		LocationListener,
		OnMyLocationButtonClickListener{

	private GoogleMap mMap;
	private LocationClient mLocationClient;
	
	// These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
      return false;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        initmap();
        initLocationClient();
        mLocationClient.connect();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }
    
    private void initmap() {
    	// Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
            	mMap.setMyLocationEnabled(true);
            	mMap.setOnMyLocationButtonClickListener(this);
                setUpMap();
            }
        }
    }
    
    private void initLocationClient() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient( 
            		getApplicationContext(),
            		this,  // ConnectionCallbacks
            		this); // OnConnectionFailedListener
        }
    }
    
    /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  // LocationListener
        centerMap();
    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onDisconnected() {
        // Do nothing
    }

    /**
     * Implementation of {@link OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }
    
    private void setUpMap()
    {
    	centerMap();
    }
    
    private void centerMap()
    {
    	if (mLocationClient!=null)
    	{
	    	Location loc = mLocationClient.getLastLocation();
	    	LatLng latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
	    	mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16));
    	}
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}
}