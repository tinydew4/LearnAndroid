package org.example.ndk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class NDKExam extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
        
        TextView tv = new TextView(this);
        int x = 1000;
        int y = 42;
        
        System.loadLibrary("ndk-exam");
        
        Log.i("msg", "1");
        int z = add(x, y);
        Log.i("msg", "2");
        tv.setText("The sum of " + x + " and " + y + " is " + z);
        setContentView(tv);
    }
    
    public native int add(int x, int y);
}