package kr.co.tinydew4.ifconfig;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class IfconfigActivity extends Activity {
	
	static {
		try {
			System.loadLibrary("ndk-ifconfig");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    static private native String NDKIfconfig();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView result = (TextView)findViewById(R.id.result);
        if (result != null) {
        	result.setText(NDKIfconfig());
        }
    }
}