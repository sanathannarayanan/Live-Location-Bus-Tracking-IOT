package com.my.snmaps;
import android.os.*;
import android.content.*;
import java.util.*;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.maps.*;
import android.location.*;
import com.google.firebase.database.*;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;

public class MainActivity extends AppCompatActivity {
	
	Timer timer = new Timer();
	FirebaseDatabase firebase = FirebaseDatabase.getInstance();
	String result = "";    // Latitude as String (will be fetched from Firebase)
	String result1 = "";   // Longitude as String (will be fetched from Firebase)
	double fla = 0;        // Flag for Removing Old Markers in Map
	MapView mapview1;
	GoogleMapController mc;
	LocationManager m;
	DatabaseReference f = firebase.getReference("test1");
	ChildEventListener f_child_listener;
	TimerTask t,t1;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initialize(savedInstanceState);
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1000);  // For Location Permission
		}
		else {
			initializeLogic();
		}
	}

	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle savedInstanceState) {
		mapview1 = (MapView) findViewById(R.id.mapview1);
		mapview1.onCreate(savedInstanceState);
		m = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mc = new GoogleMapController(mapview1, new OnMapReadyCallback() {

			public void onMapReady(GoogleMap googleMap) {
				mc.setGoogleMap(googleMap);
				fla = 0;              // Initially setting flag to 0, later when flag==1 -> Remove the old pointer, Add new Pointer

				//https://medium.com/@yossisegev/understanding-activity-runonuithread-e102d388fe93

				t1 = new TimerTask() {

					public void run() {
						runOnUiThread(new Runnable() {

							public void run() {
								mc.moveCamera(Double.parseDouble(result), Double.parseDouble(result1));  // Move Screen to the Latitude, Longitude Initially
								mc.zoomTo(14);    // Screen Zoom Level = 14
								t = new TimerTask() {

									public void run() {
										runOnUiThread(new Runnable() {

											public void run() {
												if (fla == 1) {
													mc.setMarkerVisible("ma", false);     // Remove Old Pointer
												}
												mc.moveCamera(Double.parseDouble(result), Double.parseDouble(result1));  // Move Screen to the Latitude, Longitude
												mc.addMarker("ma", Double.parseDouble(result), Double.parseDouble(result1));  // Add a new Marker
												mc.setMarkerInfo("ma", "My position", result.concat(".".concat(result1)));  // Set Marker Info
												mc.setMarkerIcon("ma", R.drawable.bus17_70x70);  // Set Marker Icon
												mc.setMarkerVisible("ma", true);  // Make the New Marker Visible in Screen
												fla = 1;  // Change flag to 1, to remove old marker in next cycle
											}
										});
									}
								};
								timer.scheduleAtFixedRate(t, (int)(1500), (int)(1500));  //Timer after 1.5 sec & for every 1.5 sec repeat throughout
							}
						});
					}
				};
				timer.schedule(t1, (int)(3000));  // Due to slow internet connections, Delay fetching from Firebase by 3 seconds to give ample time
			}
		});
		
		f_child_listener = new ChildEventListener() {
			public void onChildAdded(DataSnapshot sp1, String sp2) {
				f.addListenerForSingleValueEvent(new ValueEventListener() {
					public void onDataChange(DataSnapshot dataSnapshot) {
						result = dataSnapshot.child("b").child("a").getValue().toString(); // From Firebase a = Latitude, c = Longitude
						result1 = dataSnapshot.child("b").child("c").getValue().toString(); // From Firebase a = Latitude, c = Longitude
					}
					public void onCancelled(DatabaseError databaseError) {
					}
				});
			}

			public void onChildChanged(DataSnapshot sp1, String sp2) {
				f.addListenerForSingleValueEvent(new ValueEventListener() {
					public void onDataChange(DataSnapshot dataSnapshot) {
						result = dataSnapshot.child("b").child("a").getValue().toString();  // From Firebase a = Latitude, c = Longitude
						result1 = dataSnapshot.child("b").child("c").getValue().toString(); // From Firebase a = Latitude, c = Longitude
					}
					public void onCancelled(DatabaseError databaseError) {
					}
				});
			}

			public void onChildMoved(DataSnapshot sp1, String sp2) {
				
			}
			

			public void onChildRemoved(DataSnapshot sp1) {

			}
			

			public void onCancelled(DatabaseError sp1) {

			}
		};
		f.addChildEventListener(f_child_listener);
	}


	void initializeLogic() {
	}

	public void onDestroy() {
		super.onDestroy();
		mapview1.onDestroy();
	}

	public void onStart() {
		super.onStart();
		mapview1.onStart();
	}

	public void onPause() {
		super.onPause();
		mapview1.onPause();
	}

	public void onResume() {
		super.onResume();
		mapview1.onResume();
	}

	public void onStop() {
		super.onStop();
		mapview1.onStop();
	}

}
