package kr.co.wikinook.my_animation;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MyAnimationActivity extends Activity {
	
	ImageView iv1;
	ImageView iv2;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        iv1 = (ImageView) findViewById(R.id.imageview1);
        iv2 = (ImageView) findViewById(R.id.imageview2);
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.my_animation1);
    	Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.my_animation2);
    	iv1.startAnimation(anim1);
    	iv2.startAnimation(anim2);
    }
}