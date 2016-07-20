package ryu.beginner;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CamView extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder holder;
	private Camera camera = null;

	public CamView(Context context) {
		super(context);

		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public CamView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CamView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public boolean capture(Camera.PictureCallback jpegHandler) {
		if (camera != null) {
			camera.takePicture(null, null, jpegHandler);
			return true;
		} else {
			return false;
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
		try {
			camera.setDisplayOrientation(90);
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (camera == null)
			return;
		
		Camera.Parameters params = camera.getParameters();
		params.setPreviewSize(width, height);
//		camera.setParameters(params);
		camera.startPreview();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera = null;
	}
}
