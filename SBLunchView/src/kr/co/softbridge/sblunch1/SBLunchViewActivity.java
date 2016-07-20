package kr.co.softbridge.sblunch1;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class SBLunchViewActivity extends Activity {

	private static final String DEFAULT_URL = "http://trac.softbridge.co.kr/lunch/";
	private static final int MSG_TIMER_EXPIRED = 1;

	private static final int BACKKEY_TIMEOUT = 3000;

	private WebView mWebView = null;
	private Toast toastCloseQuery = null;
	private boolean isBackKeyPressed = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		toastCloseQuery = Toast.makeText(getApplicationContext(), "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);

		mWebView = (WebView)findViewById(R.id.webview);
		mWebView.setWebViewClient(new WebViewCallBack());

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setBuiltInZoomControls(true);

		mWebView.loadUrl(DEFAULT_URL);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		toastCloseQuery.cancel();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView.canGoBack()) {
				mWebView.goBack();
				return true;
			} else {
				if (isBackKeyPressed == false) {
					isBackKeyPressed = true;
					toastCloseQuery.show();
					startTerminateTimer();
					return true;
				}
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	private void startTerminateTimer() {
		mTerminateTimerHandler.sendEmptyMessageDelayed(MSG_TIMER_EXPIRED, BACKKEY_TIMEOUT);
	}

	private Handler mTerminateTimerHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_TIMER_EXPIRED:
				isBackKeyPressed = false;
			}
		}
	};

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		toastCloseQuery.cancel();
		isBackKeyPressed = false;

		return super.dispatchTouchEvent(ev);
	}

	private class WebViewCallBack extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
}