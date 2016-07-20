package kr.co.wikibook.twitter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class TwitListAdapter extends ArrayAdapter<ListViewLayoutData> {
	
	private Context context = null;
	private int layout;
	
	private static Hashtable<URL, Bitmap> h = new Hashtable<URL, Bitmap>();
	
	private static final int UPDATE_FRIEND_IMAGE = 0;
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case UPDATE_FRIEND_IMAGE:
					ImageViewAndBitmap ivab = (ImageViewAndBitmap)msg.obj;
					if (ivab.friend_image != null && ivab.bitmap != null) {
						ivab.friend_image.setImageBitmap(ivab.bitmap);
					}
					break;
			}
		}
	};
	
	public TwitListAdapter(Activity context, int layout, ArrayList<ListViewLayoutData>objects) {
		super(context, layout, objects);
		
		this.context = context;
		this.layout = layout;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = null;
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layout, null);
		} else {
			row = convertView;
		}
		
		ListViewLayoutData lvld = getItem(position);
		
		String screen_name = lvld.getScreenName();
		TextView twit_screen_name = (TextView)row.findViewById(R.id.twit_screen_name);
		twit_screen_name.setText(screen_name);
		
		String twitBodyString = lvld.getTwitBody();
		TextView twit_body = (TextView)row.findViewById(R.id.twit_body);
		twit_body.setText(twitBodyString);
		
		try {
			ImageView friend_image = (ImageView)row.findViewById(R.id.friend_image);
			String imageURLString = lvld.getTwitImageUrl();
			URL imageURL = new URL(imageURLString);
			SettingImageBitmap sib = new SettingImageBitmap(friend_image, imageURL);
			sib.start();
		} catch (IOException ie) {
			Log.e("JunLog", ie.getMessage());
		}
		
		return row;
	}
	
	class ImageViewAndBitmap {
		public Bitmap bitmap;
		public ImageView friend_image;
	}
	
	class SettingImageBitmap extends Thread {
		private ImageView friend_image;
		private URL url;
		
		public SettingImageBitmap(ImageView friend_image, URL url) {
			this.friend_image = friend_image;
			this.url = url;
		}
		
		public void run() {
			try {
				Bitmap bitmap = getImageBitmapFromHttp(url);
				ImageViewAndBitmap ivab = new ImageViewAndBitmap();
				ivab.bitmap = bitmap;
				ivab.friend_image = friend_image;
				
				Message msg = handler.obtainMessage();
				msg.what = UPDATE_FRIEND_IMAGE;
				msg.obj = ivab;
				handler.sendMessage(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private Bitmap getImageBitmapFromHttp(URL imageUrl) throws IOException {
			if (h.get(imageUrl) == null) {
				TwitterFriendImage tfi = new TwitterFriendImage(imageUrl, context);
				Bitmap bm = null;
				
				try {
					bm = tfi.getBitmap();
				} catch (URISyntaxException use) {
					use.printStackTrace();
				}
				
				if (bm == null) {
					Bitmap defaultBm = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_profile_image);
					h.put(imageUrl, defaultBm);
				} else {
					h.put(imageUrl, bm);
				}
				
				return bm;
			} else {
				return (Bitmap)h.get(imageUrl);
			}
		}
	}
}
