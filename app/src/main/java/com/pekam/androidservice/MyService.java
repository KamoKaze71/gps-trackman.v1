package com.pekam.androidservice;


import java.sql.Timestamp;
import com.google.gson.Gson;
import com.pekam.R;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pekam.myandroidtheme.*;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.android.gms.location.*;
import com.google.android.gms.location.LocationListener;
import com.pekam.restapi.HttpRequestTask;
import com.pekam.util.InternetConnectionDetector;
import com.pekam.entities.TblGps;

public class MyService  extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener  {
	   
		private NotificationManager nm;
	    private Timer timer = new Timer();
	    private int counter = 0;
        private int incrementby = 1;
	    private GoogleApiClient googleApiClient;
        private LocationRequest mLocationRequest = new LocationRequest();
        private String strLOG="LOG";
    public static  ArrayList<TblGps> gps = new ArrayList<TblGps>();

    private  HttpRequestTask http;
	    ArrayList<Messenger> mClients = new ArrayList<Messenger>(); // Keeps track of all current registered clients.
	    int mValue = 0; // Holds last value set by a client.
	    static final int MSG_REGISTER_CLIENT = 1;
	    static final int MSG_UNREGISTER_CLIENT = 2;
	    static final int MSG_SET_INT_VALUE = 3;
	    static final int MSG_SET_STRING_VALUE = 4;
        static final int MSG_SET_STRING_LOG =5;
        static final HttpRequestTask htt = new HttpRequestTask();
	    final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.


    @Override
    public void onDestroy() {
        SharedPreferences  mPrefs;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(gps);
        prefsEditor.putString("MyObject", json);
        prefsEditor.commit();
        super.onDestroy();
    }

    //GoogleApiClient
    @Override
    public void onLocationChanged(Location location) {
        Location mCurrentLocation = location;

        try {

             TblGps gps1=new TblGps();
            gps1.setLat(mCurrentLocation.getLatitude());
            gps1.setLon(mCurrentLocation.getLongitude());
            gps1.setDate(new Timestamp(new Date().getTime()));
            gps1.setProvider(mCurrentLocation.getProvider());
            gps1.setUserid(1);
            gps1.setDeviceID("1");

           gps.add(gps1);

            sendLogMessageToUI(gps1.getDate()+ gps1.getProvider()+"  "+ gps1.getId()+" "+ gps1.getLat()+"//" + gps1.getLon());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
      }
    }

    @Override
    public void onConnectionFailed(ConnectionResult bundle) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("onConnected", "GoogleApiClient" );
        try {
            Toast.makeText(this, "Location  service connected", Toast.LENGTH_SHORT).show();

            createLocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, mLocationRequest, this);
            LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        } catch (Throwable t) { //you should always ultimately catch all exceptions in timer tasks.
            Log.e("Google APi Connected", "Google APi Connected Failed.", t);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

	//Service
	    @Override
	    public void onCreate() {
	        super.onCreate();
	        Log.i("MyService", "Service Started.");
	        showNotification();
	        timer.scheduleAtFixedRate(new TimerTask(){ public void run() {onTimerTick();}}, 5000, 1900L);

            try {
                googleApiClient =  new GoogleApiClient.Builder(this)

                       .addApi(LocationServices.API)
                       .addConnectionCallbacks(this)
                       .addOnConnectionFailedListener(this)

                       .build();

                googleApiClient.connect();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }

	    @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
	        Log.i("MyService", "Received start id " + startId + ": " + intent);
	        return START_STICKY; // run until explicitly stopped.
	    }

        @Override
        public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    public boolean isRunning()
	    {
	        return isMyServiceRunning(this.getClass());
	    }


	private void onTimerTick() {
	        Log.i("TimerTick", "Timer doing work." + counter);
	        try {
	            counter += incrementby;
	            sendMessageToUI(counter);
           //     LocationServices.FusedLocationApi.setMockMode(googleApiClient, true);
              //  double latitude = LocationServices.FusedLocationApi.getLastLocation(googleApiClient).getLatitude();

                InternetConnectionDetector cd = new InternetConnectionDetector(getApplicationContext());
                Boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent){
                    http = new HttpRequestTask();
                    http.execute(gps);
                }

            } catch (Throwable t) { //you should always ultimately catch all exceptions in timer tasks.
	            Log.e("TimerTick", "Timer Tick Failed.", t);            
	        }
	    }
    private  boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            //GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
    private void showNotification() {
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.service_started);
        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text, System.currentTimeMillis());
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, TabBarActivity.class), 0);
        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.service_label), text, contentIntent);
        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        nm.notify(R.string.service_started, notification);
    }
    protected void createLocationRequest() {

        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private void sendMessageToUI(int intvaluetosend) {
        for (int i=mClients.size()-1; i>=0; i--) {
            try {
                // Send data as an Integer
                mClients.get(i).send(Message.obtain(null, MSG_SET_INT_VALUE, intvaluetosend, 0));

                //Send data as a String
                Bundle b = new Bundle();
                b.putString("str1", "ab" + intvaluetosend + "cd");
                Message msg = Message.obtain(null, MSG_SET_STRING_VALUE);
                msg.setData(b);
                mClients.get(i).send(msg);

            } catch (RemoteException e) {
                // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }
    }
    private void sendLogMessageToUI(String strLOG) {
        for (int i=mClients.size()-1; i>=0; i--) {
            try {
                // Send data as an Integer
                mClients.get(i).send(Message.obtain());
                //Send data as a String
                Bundle b = new Bundle();
                b.putString("strLOG", strLOG);
                Message msg = Message.obtain(null, MSG_SET_STRING_LOG);
                msg.setData(b);
                mClients.get(i).send(msg);

            } catch (RemoteException e) {
                // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }
    }
    class IncomingHandler extends Handler { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_SET_INT_VALUE:
                    incrementby = msg.arg1;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
	}