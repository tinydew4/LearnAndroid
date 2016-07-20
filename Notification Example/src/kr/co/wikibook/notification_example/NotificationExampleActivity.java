package kr.co.wikibook.notification_example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationManager;

public class NotificationExampleActivity extends Activity {
	
	static final int DIALOG_ID = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btRunToast = (Button)findViewById(R.id.bt_run_toast);
        btRunToast.setOnClickListener(
        	new OnClickListener() {
        		public void onClick(View view) {
            		Toast toast = Toast.makeText(NotificationExampleActivity.this,
            					"갓 구운 토스트의 매력에 빠져보세요.", 
            			Toast.LENGTH_SHORT);
            		toast.show();
        		}
        	}
        );
        
        Button btAddNotification = (Button)findViewById(R.id.bt_add_notification);
        btAddNotification.setOnClickListener(
        	new OnClickListener() {
        		public void onClick(View view) {
        			int notification_icon = R.drawable.wikibooks_logo_trans;
        			CharSequence tickerText = "안녕하세요?";
        			Context context = getApplicationContext();
        			CharSequence contentTitle = "세 메시지 알림";
        			CharSequence contentText = "안드로이드의 세계에 오신 것을 환영합니다.";
        			
        			Intent notificationIntent = new Intent(
        				NotificationExampleActivity.this,
        				NotificationActivity.class);
        			PendingIntent contentIntent = PendingIntent.getActivity(
        				NotificationExampleActivity.this,
        				0,
        				notificationIntent,
        				0);
        			
        			Notification notification = new Notification(notification_icon, tickerText, 0);
        			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        			
        			String ns = Context.NOTIFICATION_SERVICE;
        			NotificationManager mNotificationManager = (NotificationManager)getSystemService(ns);
        			mNotificationManager.notify(1, notification);
        		}
        	}
        );
        
        Button btRunDialog = (Button)findViewById(R.id.bt_run_dialog);
        btRunDialog.setOnClickListener(
        	new OnClickListener() {
        		public void onClick(View view) {
        			showDialog(DIALOG_ID);
        		}
        	}
        );        
    }
    
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog = null;
    	
    	switch (id) {
    		case DIALOG_ID:    			
    			TextView textview = new TextView(this);
    			textview.setText("다이얼로그에 원하는 뷰를 붙여 " +
    					"화면에 나타나게 할 수 있습니다.");
    			textview.setPadding(30, 30, 30, 30);

    			dialog = new Dialog(this);
    			dialog.setTitle("알림");
    			dialog.setContentView(textview);
    			break;
    	}
    	
    	return dialog;
    }
}