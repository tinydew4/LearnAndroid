package ryu.beginner;

import java.io.ByteArrayInputStream;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class Main extends Activity {

    private CamView cameraView = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        cameraView = new CamView(this);
        
        FrameLayout frame = (FrameLayout)findViewById(R.id.frame);
        frame.addView(cameraView);
        
        Button captureButton = (Button)findViewById(R.id.capture);

        Button wallpaperButton = (Button)findViewById(R.id.wallpaper);
        wallpaperButton.setOnClickListener(onSetWallpaperClick);
        
        Button sharedButton = (Button)findViewById(R.id.shared);
    }

    private View.OnClickListener onSetWallpaperClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			cameraView.capture(new Camera.PictureCallback() {
				
				public void onPictureTaken(byte[] data, Camera camera) {
					Log.v("Still", "Image data received from camera");
					ByteArrayInputStream bais = new ByteArrayInputStream(data);
					try {
						int height = getWallpaperDesiredMinimumHeight();
						int width = getWallpaperDesiredMinimumWidth();
						Toast.makeText(getApplicationContext(), "Wallpaper size = " + width + "x" + height, Toast.LENGTH_LONG);
						Log.v("Still", "Wallpaper size=" + width + "x" + height);
						setWallpaper(bais);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	};
}