package com.pekam.androidservice;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.pekam.R;


public class TrackmanService extends Activity {
	
	    Button btnStart, btnStop, btnBind, btnUnbind, btnUpby1, btnUpby10;
	    TextView  textStrLOG;
	    Messenger mService = null;
	    boolean mIsBound;
	    final Messenger mMessenger = new Messenger(new IncomingHandler());

	    class IncomingHandler extends Handler {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case MyService.MSG_SET_INT_VALUE:

	                break;
	            case MyService.MSG_SET_STRING_VALUE:
	                String str1 = msg.getData().getString("str1");

	                break;
                case MyService.MSG_SET_STRING_LOG:
                        String strLog = msg.getData().getString("strLOG");
                    String strLog2 = textStrLOG.getText().toString();
                    strLog = strLog + "<br>"  +strLog2;
                    textStrLOG.setText(Html.fromHtml(strLog));
                        break;
	            default:
	                super.handleMessage(msg);
	            }
	        }
	    }


	    private ServiceConnection mConnection = new ServiceConnection() {

	        public void onServiceConnected(ComponentName className, IBinder service) {
	            mService = new Messenger(service);

	            try {
	                Message msg = Message.obtain(null, MyService.MSG_REGISTER_CLIENT);
	                msg.replyTo = mMessenger;
	                mService.send(msg);
	            } catch (RemoteException e) {
	                // In this case the service has crashed before we could even do anything with it
	            }
	        }

	        public void onServiceDisconnected(ComponentName className) {
	            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
	            mService = null;

	        }
	    };


    //UI
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.trackman);

            textStrLOG = (TextView)findViewById(R.id.textStrLOG);



	        restoreMe(savedInstanceState);
            startService(new Intent(TrackmanService.this, MyService.class));
            doBindService();
	    }

	    @Override
	    protected void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	         outState.putString("textStrLOG", textStrLOG.getText().toString());
	    }
	    private void restoreMe(Bundle state) {
	        if (state!=null) {

	            textStrLOG.setText(state.getString("textStrLOG"));
	        }
	    }

	    private OnClickListener btnStartListener = new OnClickListener() {
	        public void onClick(View v){

	        }
	    };

	    private void sendMessageToService(int intvaluetosend) {
	        if (mIsBound) {
	            if (mService != null) {
	                try {
	                    Message msg = Message.obtain(null, MyService.MSG_SET_INT_VALUE, intvaluetosend, 0);
	                    msg.replyTo = mMessenger;
	                    mService.send(msg);
	                } catch (RemoteException e) {
	                }
	            }
	        }
	    }


	    void doBindService() {
            if (this.isMyServiceRunning(MyService.class)) {
                bindService(new Intent(this, MyService.class), mConnection, Context.BIND_AUTO_CREATE);
                mIsBound = true;

            }
	    }
	    void doUnbindService() {
	        if (mIsBound) {
	            // If we have received the service, and hence registered with it, then now is the time to unregister.
	            if (mService != null) {
	                try {
	                    Message msg = Message.obtain(null, MyService.MSG_UNREGISTER_CLIENT);
	                    msg.replyTo = mMessenger;
	                    mService.send(msg);
	                } catch (RemoteException e) {
	                    // There is nothing special we need to do if the service has crashed.
	                }
	            }
	            // Detach our existing connection.
	            unbindService(mConnection);
	            mIsBound = false;

	        }
	    }

	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        try {
	            doUnbindService();
	        } catch (Throwable t) {
	            Log.e("TabBarActivity", "Failed to unbind from the service", t);
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
	}
