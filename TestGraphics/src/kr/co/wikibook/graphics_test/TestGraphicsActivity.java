package kr.co.wikibook.graphics_test;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class TestGraphicsActivity extends Activity {
	
	LinearLayout mLinearLayout;
	CustomDrawableView mCustomDrawableView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*
        setContentView(new MyView(this));
//*/
        
/*
        mLinearLayout = new LinearLayout(this);
        
        ImageView i = new ImageView(this);
        i.setImageResource(R.drawable.dp);
        i.setAdjustViewBounds(true);
        i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mLinearLayout.addView(i);
        setContentView(mLinearLayout);
//*/
        mCustomDrawableView = new CustomDrawableView(this);
        setContentView(mCustomDrawableView);
    }
    
    public class CustomDrawableView extends View {

    	private ShapeDrawable mDrawable;
    	
    	public CustomDrawableView(Context context) {
    		super(context);
    		
            int x = 10;
            int y = 10;
            int width = 300;
            int height = 50;
            mDrawable = new ShapeDrawable(new OvalShape());
            mDrawable.getPaint().setColor(0xFF74AC23);
            mDrawable.setBounds(x, y, x + width, y + height);
    	}

    	protected void onDraw(Canvas canvas) {
        	mDrawable.draw(canvas);
        }
    }
    
    private static class MyView extends View {
    	
    	private Context mContext;
    	
    	public MyView(Context context) {
    		super(context);
    		mContext = context;
    	}
    	
    	@Override
    	protected void onDraw(Canvas canvas) {
    		Paint paint = new Paint();

/*
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.dp);
            BitmapShader bs = new BitmapShader(bitmap, TileMode.REPEAT, TileMode.REPEAT);
            paint.setShader(bs);
            canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
//*/
//*    		
    		int[] colors = new int[3];
    		colors[0] = Color.RED;
    		colors[1] = Color.GREEN;
    		colors[2] = Color.BLUE;
    		
    		float[] pointPositions = new float[3];
    		pointPositions[0] = 0.0f;
    		pointPositions[1] = 0.3f;
    		pointPositions[2] = 1.0f;
    		
    		LinearGradient lg = new LinearGradient(10, 10, 400, 10, colors[1], colors[0], TileMode.CLAMP);
    		RadialGradient rg = new RadialGradient(200, 200, 350, colors, pointPositions, TileMode.CLAMP);
    		SweepGradient sg = new SweepGradient(240, 200, colors[2], Color.GRAY);
    		
    		ComposeShader cs = new ComposeShader(lg, sg, new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
    		
    		paint.setShader(cs);
//    		canvas.drawPaint(paint);
    		canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
//*/
    		
/*
     		paint.setColor(0xFF504F95);
    		paint.setStrokeWidth(30);
    		canvas.drawColor(Color.GRAY);
    		
    		Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.dp);
    		
    		Bitmap scaledBitmapImage = Bitmap.createScaledBitmap(bitmapImage, 130, 130, false);
    		int sc = canvas.saveLayer(20, 20,
    			150,
    			150,
    			null, 
    			Canvas.MATRIX_SAVE_FLAG |
    			Canvas.CLIP_SAVE_FLAG |
    			Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
    		Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
    		Canvas.CLIP_TO_LAYER_SAVE_FLAG);
    		
    		RectF rectf = new RectF(20, 20, 150, 150);
    		canvas.drawRoundRect(rectf, 15, 15, paint);
    		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    		canvas.drawBitmap(scaledBitmapImage, 20, 20, paint);
//*/
    	}
    }
}