package kr.co.wikibook.twitter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

public class TwitterFriendImage {
	
	private Context mContext;

	private final String _HEAD = "http://";
	private final int _READ_BUFFER = 1024;
	private final int _OUTPUT_BUFFER = 1024;
	private final int _TIMEOUT = 3000;
	
	private URL friendImageURL;
	
	public TwitterFriendImage(URL friendImageURL, Context context) {
		this.friendImageURL = friendImageURL;
		this.mContext = context;
	}
	
	public Bitmap getBitmap() throws URISyntaxException {
		Bitmap bm = null;
		String friendImageURL = this.friendImageURL.toURI().toString();
		String filename = friendImageURL.substring(_HEAD.length(), friendImageURL.length());
		String downloadedFriendImagePath = mContext.getFilesDir() + "/" + filename;
		
		try {
			File file = new File(downloadedFriendImagePath);
			
			if (file.isFile() == false || file.length() == 0) {
				String parentFile = file.getParentFile().getPath();
				File parent = new File(parentFile);
				
				if (parent.exists() == false) {
					parent.mkdirs();
				}
				
				if (file.exists() == false) {
					file.createNewFile();
				}
				
				HttpURLConnection conn = (HttpURLConnection)this.friendImageURL.openConnection();
				conn.setConnectTimeout(_TIMEOUT);
				conn.setReadTimeout(_TIMEOUT);
				
				BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());

				FileOutputStream fos = new FileOutputStream(downloadedFriendImagePath);
				BufferedOutputStream bos = new BufferedOutputStream(fos, _OUTPUT_BUFFER);
				
				byte data[] = new byte[_OUTPUT_BUFFER];
				int readsize = 0;
				
				while ((readsize = bis.read(data, 0, _READ_BUFFER)) != -1) {
					bos.write(data, 0, readsize);
				}
				bos.close();
				fos.close();
				bis.close();
				conn.disconnect();
			}
			
			FileInputStream fis = new FileInputStream(downloadedFriendImagePath);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bm = BitmapFactory.decodeStream(bis);
			
			bis.close();
			fis.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		
		return getRoundedCornerBitmap(bm);
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 4;
		
		paint.setAntiAlias(true);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		
		return output;
	}
}
