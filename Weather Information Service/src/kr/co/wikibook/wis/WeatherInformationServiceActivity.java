package kr.co.wikibook.wis;

import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class WeatherInformationServiceActivity extends Activity {
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String area = bundle.getString(WeatherService.AREA);
			int[] rIds = bundle.getIntArray(WeatherService.RESOURCE_IDS);
			String[] dates = bundle.getStringArray(WeatherService.DATES);
			updateTableLayout(area, rIds, dates);
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Intent intent = new Intent(this, WeatherService.class);
        startService(intent);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	registerReceiver(receiver, new IntentFilter(WeatherService.WEATHER_INFORMATION_RECEIVER));
    }
    
    public void updateTableLayout(String area, int[] resourceIds, String[] dates) {

		TableLayout mTableLayout = (TableLayout)findViewById(R.id.table_layout);
   
		if (area.equals("clear"))
		{
			mTableLayout.removeAllViews();
			return;
		}

    	TableRow tr = new TableRow(this);
    	
    	TextView tv = new TextView(this);
    	tv.setText(area);
    	TableRow.LayoutParams tlp = new TableRow.LayoutParams();
    	tlp.gravity = Gravity.CENTER_VERTICAL;
    	tv.setPadding(5, 5, 10, 5);
    	tv.setLayoutParams(tlp);
    	tr.addView(tv);
    	
    	for (int i = 0; i < resourceIds.length; i++) {
    		LinearLayout ll = new LinearLayout(this);
    		ll.setOrientation(LinearLayout.VERTICAL);
    		
    		ImageView iv = new ImageView(this);
    		iv.setImageResource(resourceIds[i]);
    		LinearLayout.LayoutParams llparam = new LinearLayout.LayoutParams(dipsToPixels(45), dipsToPixels(45));
    		llparam.gravity = Gravity.CENTER_VERTICAL;
    		iv.setPadding(5, 5, 5, 5);
    		iv.setLayoutParams(llparam);
    		ll.addView(iv);
    		
    		TextView itv = new TextView(this);
    		LinearLayout.LayoutParams tvllparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    		tvllparam.gravity = Gravity.CENTER_HORIZONTAL;

    		itv.setText(dates[i].substring(5, dates[i].length()));
    		Log.i("JunLog", dates[i]);
    		Log.i("JunLog", dates[i].substring(5, dates[i].length()));
    		ll.addView(itv);
    		
    		tr.addView(ll);
    	}
    	mTableLayout.addView(tr);
    	
    	TextView last_update_message = (TextView)findViewById(R.id.last_update_message);
    	last_update_message.setText("마지막으로 업데이트 한 시간: " + new Date().toString());
    }
    
    private int dipsToPixels(float dip) {
    	int ret;
    	final float scale = getResources().getDisplayMetrics().density;
    	ret = (int)(dip * scale + 0.5f);
    	return ret;
    }
}