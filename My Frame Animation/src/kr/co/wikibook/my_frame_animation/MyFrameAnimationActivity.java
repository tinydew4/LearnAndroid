package kr.co.wikibook.my_frame_animation;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

public class MyFrameAnimationActivity extends Activity {
	
	AnimationDrawable animation;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ImageView iv = (ImageView)findViewById(R.id.imageview);
        iv.setBackgroundResource(R.drawable.my_frame_animation);
        animation = (AnimationDrawable)iv.getBackground();
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	animation.start();
    }
}