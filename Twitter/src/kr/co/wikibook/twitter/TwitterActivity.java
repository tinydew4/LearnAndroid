package kr.co.wikibook.twitter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.RequestToken;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

public class TwitterActivity extends ListActivity {
	
	public static String consumerKey = "YgOds5PBma3XsNAzz19gQ";
	public static String consumerSecret = "QBjZvwddmA88l6kTrGhmhXkT02B4mJUI1V1VyeiIoo";
	private RequestToken rqToken;
	public static Uri CALLBACK_URL;
	
	private static final int _DID_LOADING_FRIENDS_TIMELINE = 0;
	
	private TwitListAdapter mTa = null;
	
	public static Twitter mTwitter;
	
	private static String CREATE_TABLE_QUERY = "CREATE TABLE TWITTER ("
		+ "id INTEGER PRIMARY KEY,"
		+ "friend_name TEXT,"
		+ "screen_name TEXT,"
		+ "twit_body TEXT,"
		+ "twit_image_url,"
		+ "geo_location_latitude INTEGER,"
		+ "geo_location_longitude INTEGER,"
		+ "follower_count INTEGER,"
		+ "url TEXT,"
		+ "location,"
		+ "retweet_id INTEGER"
		+ ");";
	
	private static String DROP_TABLE_QUERY = "DROP TABLE TWITTER";
	private View moreItem = null;
	
	private int getting_action = 0;
	private final int GAC_GET_LATEST = 0;
	private final int GAC_GET_PREVIOUS = 1;
	
	ArrayList<ListViewLayoutData> mArrlstLvld = null;
	
	private int POST_PER_PAGE = 20;
	
	private String menu0 = "입소문내기";
	private String menu1 = "답글";
	private String menu2 = "삭제";
	
	private static final int WHAT_GET_TIMELINE = 0;
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case WHAT_GET_TIMELINE:
					if (mTa == null && mArrlstLvld != null) {
						if (mArrlstLvld.size() > 0) {
							mTa = new TwitListAdapter(TwitterActivity.this, R.layout.timeline_item, mArrlstLvld);
							setListAdapter(mTa);
							mArrlstLvld = null;
						}
					} else if (mTa != null && mArrlstLvld != null) {
						for (int i = 0; i < mArrlstLvld.size(); i++) {
							if (getting_action == GAC_GET_LATEST) {
								mTa.insert(mArrlstLvld.get(i), i);
							} else {
								mTa.add(mArrlstLvld.get(i));
							}
						}
						mArrlstLvld = null;
					}
					
					removeDialog(_DID_LOADING_FRIENDS_TIMELINE);
					break;
			}
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        createTable(true);
        
        registerForContextMenu(getListView());
        
        CALLBACK_URL = Uri.parse("tinydew4://android.artofcode.org");
        
        try {
        	System.setProperty("twitter4j.auth.useSSL", "false");
        	System.setProperty("twitter4j.oauth.consumerKey", consumerKey);
        	System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret);
        	System.setProperty("twitter4j.oauth.requestTokenURL", "http://api.twitter.com/oauth/request_token");
        	System.setProperty("twitter4j.oauth.accessTokenURL", "http://api.twitter.com/oauth/access_token");
        	System.setProperty("twitter4j.oauth.authorizationURL", "http://api.twitter.com/oauth/authorize");
        	
        	mTwitter = new TwitterFactory().getInstance();
        	rqToken = mTwitter.getOAuthRequestToken(CALLBACK_URL.toString());
        	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rqToken.getAuthorizationURL())));
        } catch (TwitterException e) {
        	e.printStackTrace();
        }
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	Uri uri = intent.getData();
    	if (uri != null && CALLBACK_URL.getScheme().equals(uri.getScheme())) {
    		String oauth_verifier = uri.getQueryParameter("oauth_verifier");
    		try {
    			mTwitter.getOAuthAccessToken(rqToken, oauth_verifier);
    			loadSavedTwitListFromDb();
    			syncLatestTwitWithServer();
    		} catch (TwitterException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(Menu.NONE, Menu.FIRST + 1, Menu.NONE, "새 트윗 작성하기");
    	menu.add(Menu.NONE, Menu.FIRST + 2, Menu.NONE, "새로 고침");
    	menu.add(Menu.NONE, Menu.FIRST + 3, Menu.NONE, "DB 초기화");
    	menu.add(Menu.NONE, Menu.FIRST + 4, Menu.NONE, "종료");
    	
    	return (super.onCreateOptionsMenu(menu));
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	return (itemCallback(item) || super.onOptionsItemSelected(item));
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch(id) {
    		case _DID_LOADING_FRIENDS_TIMELINE: {
    			ProgressDialog dialog = new ProgressDialog(this);
    			dialog.setMessage("트윗 목록을 로딩하는 중입니다...");
    			dialog.setIndeterminate(true);
    			dialog.setCancelable(true);
    			return dialog;
    		}
    	}
    	return null;
    }
    
    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
    	super.onListItemClick(parent, v, position, id);
    	
    	ListViewLayoutData lvld = (ListViewLayoutData)parent.getItemAtPosition(position);
    	
    	Intent intent = new Intent(TwitterActivity.this, TwitViewActivity.class);
    	intent.putExtra("id", lvld.getId());
    	startActivity(intent);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
    	
    	long listviewItemId = getListAdapter().getItemId(info.position);
    	ListViewLayoutData lvld = mTa.getItem((int)listviewItemId);
    	
    	menu.setHeaderTitle("이 트윗에 대하여");
    	try {
    		if (!mTwitter.getScreenName().equals(lvld.getScreenName())) {
    			menu.add(0, v.getId(), 0, menu0);
    			menu.add(0, v.getId(), 0, menu1);
    		}
    	} catch (IllegalStateException e) {
    		e.printStackTrace();
    	} catch (TwitterException e) {
    		e.printStackTrace();
    	}
    	try {
    		if (mTwitter.getScreenName().equals(lvld.getScreenName())) {
    			menu.add(0, v.getId(), 0, menu2);
    		}
    	} catch (IllegalStateException e) {
    		e.printStackTrace();
    	} catch (TwitterException e) {
    		e.printStackTrace();
    	}
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterView.AdapterContextMenuInfo info;
    	try {
    		info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    	} catch (ClassCastException e) {
    		e.printStackTrace();
    		return false;
    	}
    	long listviewItemId = getListAdapter().getItemId(info.position);
    	
    	ListViewLayoutData lvld = mTa.getItem((int)listviewItemId);
    	long id = lvld.getId();
    	String screen_name = lvld.getScreenName();
    	
    	if (item.getTitle().equals(menu0)) {
    		try {
    			Status status = mTwitter.retweetStatus(id);
    			addStatusToDb(status, null);
    			
    			ListViewLayoutData newLvld = new ListViewLayoutData(
    				status.getId(),
    				status.getUser().getName(),
    				status.getUser().getScreenName(),
    				status.getText(),
    				status.getUser().getProfileImageURL().toString());
    			mTa.insert(newLvld, 0);
    			
    			syncLatestTwitWithServer();
    		} catch (TwitterException te) {
    			te.printStackTrace();
    		}
    	} else if (item.getTitle().equals(menu1)) {
    		Intent it = new Intent(TwitterActivity.this, AddNewTwitActivity.class);
    		it.putExtra("id", id);
    		it.putExtra("screen_name", screen_name);
    		startActivity(it);
    	} else if (item.getTitle().equals(menu2)) {
    		try {
    			Status status = mTwitter.destroyStatus(id);
    			removeStatusFromDb(status, null);
    			mTa.remove(lvld);
    		} catch (TwitterException te) {
    			te.printStackTrace();
    		}
    	} else {
    		return false;
    	}
    	return true;
    }
    
    class SyncWithServerThread implements Runnable {
    	Paging mPaging = null;
    	
    	public void run() {
    		try {
    			getHomeTimeline();
    			Message message = handler.obtainMessage();
    			message.what = WHAT_GET_TIMELINE;
    			handler.sendMessage(message);
    		} catch (TwitterException te) {
    		}
    	}
    	
    	public void setPaging(Paging paging) {
    		mPaging = paging;
    	}
    	
    	private void getHomeTimeline() throws TwitterException {
    		SQLiteDatabase db = null;
    		if (db == null) {
    			db = openOrCreateDatabase("twitter.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
    		}
    		try {
    			List<Status> statuses = null;
    			if (mPaging != null) {
    				statuses = mTwitter.getHomeTimeline(mPaging);
    			} else {
    				statuses = mTwitter.getHomeTimeline();
    			}
    			
    			if (mArrlstLvld == null) {
    				mArrlstLvld = new ArrayList<ListViewLayoutData>();
    			}
    			
    			for (Status status : statuses) {
    				long latestId = getLatestItemId();
    				if (status.getId() == latestId && !status.isRetweet()) {
    					Log.i("JunLog", latestId + "is already existed!! ");
    				}
    				
    				User user = status.getUser();
    				URL url = user.getProfileImageURL();
    				
    				if (addStatusToDb(status, db) != -1) {
    					ListViewLayoutData lvld = new ListViewLayoutData(
    						status.getId(),
    						user.getName(),
    						user.getScreenName(),
    						status.getText(),
    						url.toString());
    					mArrlstLvld.add(lvld);
    				}
    			}
    		} catch (TwitterException te) {
    			throw te;
    		}
    		db.close();
    	}
    }
    
    private long addStatusToDb(Status status, SQLiteDatabase pDb) {
    	long ret = -1;
    	
    	SQLiteDatabase db = null;
    	if (pDb == null) {
    		if (db == null) {
    			db = openOrCreateDatabase("twitter.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
    		}
    	} else {
    		db = pDb;
    	}
    	
    	User user = status.getUser();
    	URL url = user.getProfileImageURL();
    	GeoLocation gl = status.getGeoLocation();
    	double latitude = 0;
    	double longitude = 0;
    	if (gl != null) {
    		latitude = gl.getLatitude();
    		longitude = gl.getLongitude();
    	}
    	
    	ContentValues cv = new ContentValues();
    	cv.put("id", status.getId());
    	cv.put("friend_name", user.getName());
    	cv.put("screen_name", user.getScreenName());
    	cv.put("twit_body", status.getText());
    	cv.put("twit_image_url", url.toString());
    	if (gl != null) {
    		cv.put("geo_location_latitude", latitude);
    		cv.put("geo_location_longitude", longitude);
    	}
    	cv.put("follower_count", user.getFollowersCount());
    	if (user.getURL() != null)
    		cv.put("url", user.getURL().toString());
    	if (user.getLocation() != null)
    		cv.put("location", user.getLocation());
    	if (status.getRetweetedStatus() != null) {
    		cv.put("retweet_id", status.getRetweetedStatus().getId());
    	}
    	ret = db.insert("TWITTER", null, cv);
    	
    	if (pDb == null) {
    		if (db != null) {
    			db.close();
    		}
    	}
    	return ret;
    }
    
    private void removeStatusFromDb(Status status, SQLiteDatabase pDb) {
    	SQLiteDatabase db = null;
    	if (pDb == null) {
    		if (db == null) {
    			db = openOrCreateDatabase("twitter.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
    		}
    	} else {
    		db = pDb;
    	}
    	
    	db.delete("TWITTER", "id=?", new String[] {String.valueOf(status.getId())});
    	
    	if (pDb == null) {
    		if (db != null) {
    			db.close();
    		}
    	}
    }
    
    private boolean itemCallback(MenuItem item) {
    	switch (item.getItemId()) {
    		case Menu.FIRST + 1:
    			Intent it = new Intent(TwitterActivity.this, AddNewTwitActivity.class);
    			startActivity(it);
    			return true;
    		case Menu.FIRST + 2:
    			syncLatestTwitWithServer();
    			return true;
    		case Menu.FIRST + 3:
    			createTable(false);
    			finish();
    			return true;
    		case Menu.FIRST + 4:
    			finish();
    			return true;
    	}
    	
    	return false;
    }
    
    private void loadSavedTwitListFromDb() {
    	addMoreItem();
    	
    	SQLiteDatabase db = null;
    	if (db == null) {
    		db = openOrCreateDatabase("twitter.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
    	}
    	
    	Cursor c = db.query("twitter", new String[] { "id", "friend_name", "screen_name", "twit_body", "twit_image_url" }, null, null, null, null, "id DESC");

    	mArrlstLvld = new ArrayList<ListViewLayoutData>();
    	
    	c.moveToFirst();
    	
    	for (int i = 0; i < c.getCount(); i++) {
    		ListViewLayoutData lvld = new ListViewLayoutData(
    			c.getLong(c.getColumnIndex("id")),
    			c.getString(c.getColumnIndex("friend_name")),
    			c.getString(c.getColumnIndex("screen_name")),
    			c.getString(c.getColumnIndex("twit_body")),
    			c.getString(c.getColumnIndex("twit_image_url")));
    		
    		if (mTa == null) {
    			mArrlstLvld.add(lvld);
    		} else {
    			mTa.add(lvld);
    		}
    		c.moveToNext();
    	}
    	
    	if (mTa == null && mArrlstLvld.size() > 0) {
    		mTa = new TwitListAdapter(TwitterActivity.this, R.layout.timeline_item, mArrlstLvld);
    		setListAdapter(mTa);
    		mArrlstLvld = null;
    	}
    	
    	c.close();
    	db.close();
    }
    
    private void addMoreItem() {
    	if (moreItem != null) {
    	} else {
    		ListView lv = getListView();
    		
    		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		View addMoreItemLayout = inflater.inflate(R.layout.add_more_item, null);
    		
    		addMoreItemLayout.setOnClickListener(new OnClickListener() {
    			public void onClick(View v) {
    				syncPreviousTwitWithServer();
    			}
    		});
    		
    		lv.addFooterView(addMoreItemLayout);
    		moreItem = addMoreItemLayout;
    	}
    }

/*
    private void removeMoreItem() {
    	if (moreItem != null) {
    		ListView lv = getListView();
    		lv.removeFooterView(moreItem);
    		moreItem = null;
    	} else {
    	}
    }
//*/  
    
    private void syncLatestTwitWithServer() {
  		showDialog(_DID_LOADING_FRIENDS_TIMELINE);
    	
    	getting_action = GAC_GET_LATEST;
    	
    	SyncWithServerThread sst = new SyncWithServerThread();
    	
    	long sinceId = getLatestItemId();
    	if (sinceId > 0) {
    		Paging paging = new Paging(sinceId);
    		sst.setPaging(paging);
    	}

    	Thread thread = new Thread(sst);
     	thread.start();
    }
    
    private void syncPreviousTwitWithServer() {
    	showDialog(_DID_LOADING_FRIENDS_TIMELINE);
    	
    	getting_action = GAC_GET_PREVIOUS;
    	
    	long prevoiusId = getPreviousItemId();
    	Paging paging = new Paging(1, POST_PER_PAGE + 1, 1, prevoiusId);
    	
    	SyncWithServerThread sst = new SyncWithServerThread();
    	sst.setPaging(paging);
    	Thread thread = new Thread(sst);
    	thread.start();
    }
    
    private void createTable(boolean create) {
    	SQLiteDatabase db = null;
    	if (db == null) {
    		db = openOrCreateDatabase("twitter.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
    	}
    	
    	if (create == true) {
    		try {
    			Cursor c = db.query("sqlite_master", new String[]{"count(*)"}, "name=?", new String[]{"TWITTER"}, null, null, null);
    			
    			int cnt = 0;
    			
    			c.moveToFirst();
    			while (c.isAfterLast() == false) {
    				cnt = c.getInt(0);
    				c.moveToNext();
    			}
    			c.close();
    			if (cnt == 0)
    				db.execSQL(CREATE_TABLE_QUERY);
    		} catch (SQLiteException se) {
    		}
    	} else {
    		db.execSQL(DROP_TABLE_QUERY);
    	}
    	
    	db.close();
    }
    
    private long getLatestItemId() {
    	long ret = 0;
    	
    	SQLiteDatabase db = null;
    	if (db == null) {
    		db = openOrCreateDatabase("twitter.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
    	}
    	
    	Cursor c = db.query("TWITTER", new String[]{"id"}, "retweet_id is null", null, null, null, "id DESC");
    	
    	c.moveToFirst();
    	if (c.isAfterLast() == false) {
    		ret = c.getLong(0);
    		c.moveToNext();
    	}
    	
    	c.close();
    	db.close();
    	
    	return ret;
    }
    
    private long getPreviousItemId() {
    	long ret = 0;
    	
    	SQLiteDatabase db = null;
    	if (db == null) {
    		db = openOrCreateDatabase("twitter.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
    	}
    	
    	Cursor c = db.query("TWITTER", new String[]{"id"}, "retweet_id is null", null, null, null, "id");
    	
    	c.moveToFirst();
    	if (c.isAfterLast() == false) {
    		ret = c.getLong(0);
    		c.moveToNext();
    	}
    	
    	c.close();
    	db.close();
    	
    	return ret;
    }
}