package kr.co.wikibook.wis;

import android.app.Activity;
import android.os.Bundle;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class WeatherAppWidgetConfigure extends Activity {
	
	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.weather_appwidget_configure);
	
	    setResult(RESULT_CANCELED);
	    
	    Button configure_to_end_button = (Button)findViewById(R.id.configure_to_end_button);
	    configure_to_end_button.setOnClickListener(
	    	new View.OnClickListener() {
	    		public void onClick(View v) {
	    			Intent resultValue = new Intent();
	    			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
	    			
	    			setResult(RESULT_OK, resultValue);
	    			finish();
	    		}
	    	}
	    );
	    
	    Intent intent = getIntent();
	    Bundle extras = intent.getExtras();
	    if (extras != null) {
	    	mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	    }
	    
	    if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
	    	finish();
	    }
	}

}
