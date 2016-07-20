package kr.co.wikibook.twitter;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class TwitViewActivity extends MapActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.twit_view_activity);
	
	    Intent it = getIntent();
	    Bundle et = it.getExtras();
	    long id = et.getLong("id");
	    
	    SQLiteDatabase db = null;
	    if (db == null) {
	    	db = openOrCreateDatabase("twitter.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
	    }
	    
	    Cursor c = db.query("TWITTER", new String[]{"*"}, "id = ?", new String[] { String.valueOf(id) }, null, null, null);
	    
	    c.moveToFirst();
	    
	    ImageView profile_image_view = (ImageView)findViewById(R.id.profile_image_view);
	    
	    if (c.isAfterLast() == false) {
	    	String friend_name = c.getString(c.getColumnIndex("friend_name"));
	    	String twit_body = c.getString(c.getColumnIndex("twit_body"));
	    	String twit_image_url = c.getString(c.getColumnIndex("twit_image_url"));
	    	int follower_count = c.getInt(c.getColumnIndex("follower_count"));
	    	String url = c.getString(c.getColumnIndex("url"));
	    	String location = c.getString(c.getColumnIndex("location"));
	    	double geo_location_latitude = c.getDouble(c.getColumnIndex("geo_location_latitude"));
	    	double geo_location_longitude = c.getDouble(c.getColumnIndex("geo_location_longitude"));
	    	
	    	MapView mapview = (MapView)findViewById(R.id.mapview);
	    	if (geo_location_latitude > 0 && geo_location_longitude > 0) {
	    		mapview.setSatellite(false);
	    		
	    		GeoPoint gp = new GeoPoint((int)(geo_location_latitude * 1000000), (int)(geo_location_longitude * 1000000));
	    		
	    		MapController mc = mapview.getController();
	    		mc.animateTo(gp);
	    		mc.setZoom(14);
	    		mapview.setBuiltInZoomControls(true);
	    	} else {
	    		mapview.setVisibility(View.INVISIBLE);
	    	}
	    	
	    	TextView twit_view_activity_friend_name = (TextView)findViewById(R.id.twit_view_activity_friend_name);
	    	twit_view_activity_friend_name.setText(friend_name);
	    	
	    	TextView twit_view_activity_body = (TextView)findViewById(R.id.twit_view_activity_body);
	    	twit_view_activity_body.setText(twit_body);
	    	
	    	TextView twit_view_activity_follower_count = (TextView)findViewById(R.id.twit_view_activity_follower_count);
	    	twit_view_activity_follower_count.setText(String.valueOf(follower_count));
	    	
	    	TextView twit_view_activity_friend_region = (TextView)findViewById(R.id.twit_view_activity_friend_region);
	    	twit_view_activity_friend_region.setText(location);
	    	
	    	TextView twit_view_activity_friend_homepage = (TextView)findViewById(R.id.twit_view_activity_friend_homepage);
	    	twit_view_activity_friend_homepage.setText(url);
	    	
	    	try {
	    		TwitterFriendImage tfi = new TwitterFriendImage(new URL(twit_image_url), this);
	    		Bitmap bm = null;
	    		bm = tfi.getBitmap();
	    		bm = TwitterFriendImage.getRoundedCornerBitmap(bm);
	    		
	    		profile_image_view.setImageBitmap(bm);
	    	} catch (MalformedURLException mue) {
	    		mue.printStackTrace();
	    	} catch (URISyntaxException use) {
	    		use.printStackTrace();
	    	}
	    }
	    
	    c.close();
	    db.close();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
