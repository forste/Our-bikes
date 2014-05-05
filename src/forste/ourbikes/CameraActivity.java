package forste.ourbikes;

import forste.ourbikes.chat.Messaging;
import forste.ourbikes.chat.Messaging.MessageReceiver;
import forste.ourbikes.chat.interfaces.IAppManager;
import forste.ourbikes.chat.services.IMService;
import forste.ourbikes.chat.tools.FriendController;
import forste.ourbikes.chat.types.FriendInfo;
import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.os.Build;

public class CameraActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mPreview;
    
    //messaging
    private EditText messageText;
	private EditText messageHistoryText;
	private Button sendMessageButton;
	private IAppManager imService;
	private FriendInfo friend;
	private static final int MESSAGE_CANNOT_BE_SENT = 0;
	private boolean messagingViewVisible = false;
	
	private ServiceConnection mConnection = new ServiceConnection() {
	      
		public void onServiceConnected(ComponentName className, IBinder service) {          
            imService = ((IMService.IMBinder)service).getService();          
        }
        public void onServiceDisconnected(ComponentName className) {          
        	imService = null;
            Toast.makeText(CameraActivity.this, R.string.local_service_stopped,
                    Toast.LENGTH_SHORT).show();
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

	     // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        
        final Button toggleVisibilityButton = new Button(this);
        toggleVisibilityButton.setLayoutParams(new LayoutParams(
        		ViewGroup.LayoutParams.WRAP_CONTENT,
        		ViewGroup.LayoutParams.WRAP_CONTENT));
        preview.addView(toggleVisibilityButton);
//        
//        button = new Button(this);
//        button.setText(R.string.cancel);
//        button.setLayoutParams(new LayoutParams(
//        		ViewGroup.LayoutParams.WRAP_CONTENT,
//        		ViewGroup.LayoutParams.WRAP_CONTENT));
//        preview.addView(button);
        
        LayoutInflater inflator = LayoutInflater.from(this);
        final View messagingView = inflator.inflate(R.layout.messaging_screen, null);
        preview.addView(messagingView);
        
        messageHistoryText = (EditText) findViewById(R.id.messageHistory);
		
		messageText = (EditText) findViewById(R.id.message);
		
		messageText.requestFocus();			
		
		sendMessageButton = (Button) findViewById(R.id.sendMessageButton);
		
//		Bundle extras = this.getIntent().getExtras();
//		
		friend = new FriendInfo();
//		friend.userName = extras.getString(FriendInfo.USERNAME);
//		friend.ip = extras.getString(FriendInfo.IP);
//		friend.port = extras.getString(FriendInfo.PORT);
//		String msg = extras.getString(FriendInfo.MESSAGE);
		friend.userName = "stephan";
		friend.ip = "";
		friend.port = "";
		String msg = "hello";
//		/msg = null;
		
		if (msg != null) 
		{
			this.appendToMessageHistory(friend.userName , msg);
			((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel((friend.userName+msg).hashCode());
			messagingView.setVisibility(View.VISIBLE);
			toggleVisibilityButton.setText("<< Hide");
		} else {
			messagingView.setVisibility(View.INVISIBLE);
			toggleVisibilityButton.setText(">> Show help");
		}
		
		toggleVisibilityButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(messagingView.getVisibility() == View.VISIBLE) {
					messagingView.setVisibility(View.INVISIBLE);
					toggleVisibilityButton.setText(">> Show help");
				} else {
					messagingView.setVisibility(View.VISIBLE);
					toggleVisibilityButton.setText("<< Hide");
				}
			}
		});

		
		sendMessageButton.setOnClickListener(new OnClickListener(){
			CharSequence message;
			Handler handler = new Handler();
			public void onClick(View arg0) {
				message = messageText.getText();
				if (message.length()>0) 
				{		
					appendToMessageHistory(imService.getUsername(), message.toString());
								
					messageText.setText("");
					Thread thread = new Thread(){					
						public void run() {
							if (!imService.sendMessage(friend.userName, message.toString()))
							{
								
								handler.post(new Runnable(){	

									public void run() {
										showDialog(MESSAGE_CANNOT_BE_SENT);										
									}
									
								});
							}
						}						
					};
					thread.start();
										
				}
				
			}});
		
		messageText.setOnKeyListener(new OnKeyListener(){
			public boolean onKey(View v, int keyCode, KeyEvent event) 
			{
				if (keyCode == 66){
					sendMessageButton.performClick();
					return true;
				}
				return false;
			}
			
			
		});
	}
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
//	    try {
	        c = Camera.open(); // attempt to get a Camera instance
//	    }
//	    catch (Exception e){
//	        // Camera is not available (in use or does not exist)
//	    }
	    return c; // returns null if camera is unavailable
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_camera,
					container, false);
			return rootView;
		}
	}
	
	/*
	 * 
	 * 
	 * ********** messaging
	 */
	

	@Override
	protected Dialog onCreateDialog(int id) {
		int message = -1;
		switch (id)
		{
		case MESSAGE_CANNOT_BE_SENT:
			message = R.string.message_cannot_be_sent;
		break;
		}
		
		if (message == -1)
		{
			return null;
		}
		else
		{
			return new AlertDialog.Builder(CameraActivity.this)       
			.setMessage(message)
			.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked OK so do some stuff */
				}
			})        
			.create();
		}
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(messageReceiver);
		unbindService(mConnection);
		
		FriendController.setActiveFriend(null);
		
	}

	@Override
	protected void onResume() 
	{		
		super.onResume();
		bindService(new Intent(CameraActivity.this, IMService.class), mConnection , Context.BIND_AUTO_CREATE);
				
		IntentFilter i = new IntentFilter();
		i.addAction(IMService.TAKE_MESSAGE);
		
		registerReceiver(messageReceiver, i);
		
		FriendController.setActiveFriend(friend.userName);		
		
		
		
	}
	
	

	public class  MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) 
		{		
			Bundle extra = intent.getExtras();
			String username = extra.getString(FriendInfo.USERNAME);			
			String message = extra.getString(FriendInfo.MESSAGE);
			
			if (username != null && message != null)
			{
//				if (friend.userName.equals(username)) {
					appendToMessageHistory(username, message);					
//				}
//				else {
//					if (message.length() > 15) {
//						message = message.substring(0, 15);
//					}
//					Toast.makeText(CameraActivity.this,  username + " says '"+
//													message + "'",
//													Toast.LENGTH_SHORT).show();		
//				}
			}			
		}
		
	};
	private MessageReceiver messageReceiver = new MessageReceiver();
	
	private void appendToMessageHistory(String username, String message) {
		if (username != null && message != null) {
			messageHistoryText.append(username + ":\n");								
			messageHistoryText.append(message + "\n");	
		}
	}
	

}
