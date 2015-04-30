package com.pekam.location;//package com.pekam.location;
//
//import android.content.Intent;
//import android.location.LocationManager;
//
//public class MyLocation {
//	
//	
//	
//	
//	LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
//	boolean enabled = service
//	  .isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//	// check if enabled and if not send user to the GSP settings
//	// Better solution would be to display a dialog and suggesting to 
//	// go to the settings
//	if (!enabled) {
//	  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//	  startActivity(intent);
//	} 
//}
