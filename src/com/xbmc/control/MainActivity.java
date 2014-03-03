package com.xbmc.control;

import com.xbmc.control.util.SystemUiHider;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
	private WebView mWebView;

	@Override
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		
		final Activity activity = this;
		super.onCreate(savedInstanceState);
		
		CookieSyncManager.createInstance(activity);
		CookieSyncManager.getInstance().startSync();
		CookieManager.getInstance().setAcceptCookie(true);
		CookieManager.getInstance().removeExpiredCookie();
		
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // check if API of device is that of KitKat (19)
		if ( 0 != ( getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) ) { // check if app is set to debugging
		WebView.setWebContentsDebuggingEnabled(true); // set debugging with Chrome to true
		}
		}
		
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_main);
		
		WebView webview = new WebView(this);      
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setAllowContentAccess(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

		webSettings.setDomStorageEnabled(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setSupportMultipleWindows(true);
		webview.setHorizontalScrollBarEnabled(false);
		webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		
		webview.addJavascriptInterface(this, "android");
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			webSettings.setAllowUniversalAccessFromFileURLs(true);
			webSettings.setAllowFileAccessFromFileURLs(true);
	    }
		
		webview.setWebChromeClient(new WebChromeClient() {
			  public void onConsoleMessage(String message, int lineNumber, String sourceID) {
			    Log.d("MyApplication", message + " -- From line "
			                         + lineNumber + " of "
			                         + sourceID);
			  }

			   public void onProgressChanged(WebView view, int progress) {
			     // Activities and WebViews measure progress with different scales.
			     // The progress meter will automatically disappear when we reach 100%
			     activity.setProgress(progress * 1000);
			   }
			 });
		
		webview.setWebViewClient(new WebViewClient() {  
		      @Override  
		      public boolean shouldOverrideUrlLoading(WebView view, String url)  
		      {  
		        view.loadUrl(url);  
		        return true;  
		      }  
		   
			   public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			     Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
			   }
			 });
		
		  setContentView(webview); 
		  
		  webview.loadUrl("http://henifezzeni.hd.free.fr:8080/Remote/index.php");
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
		//	mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
}
