package com.xbmc.control;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.xbmc.control.util.ControlWebViewClient;
import com.xbmc.control.util.JsInterface;
import com.xbmc.control.util.SystemUiHider;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
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

/**
 * @author 922261
 * 
 */
public class MainActivity extends Activity implements SensorEventListener {

	private static final String URL_WEBAPP = "http://henifezzeni.hd.free.fr:8080/Remote/index.php";
	private WebView mWebView;
	/** * Le sensor manager */
	SensorManager sensorManager;
	PowerManager _powerManager;
	/** * l'accéléromètre */
	Sensor orientaton;
	private PowerManager.WakeLock wakeLock;
	private int field = 0x00000020;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	protected void onCreate(Bundle savedInstanceState) {

		final Activity activity = this;
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// Instancier l'accéléromètre
		orientaton = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		_powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = _powerManager.newWakeLock(field, getLocalClassName());
		super.onCreate(savedInstanceState);
		CookieSyncManager.createInstance(activity);
		CookieSyncManager.getInstance().startSync();
		CookieManager.getInstance().setAcceptCookie(true);
		CookieManager.getInstance().removeExpiredCookie();
		Window w = activity.getWindow();
		w.requestFeature(Window.FEATURE_NO_TITLE);
		w.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		// in Activity's onCreate() for instance
		w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		// w.requestFeature(Window.FEATURE_PROGRESS);
		// setContentView(R.layout.activity_main);
		startWebView(URL_WEBAPP);
		setContentView(mWebView);
		if (isOnline()) {
			mWebView.loadUrl(URL_WEBAPP);
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			// 2. Chain together various setter methods to set the dialog
			// characteristics
			builder.setMessage(R.string.dialog_body_no_connection).setTitle(
					R.string.dialog_title_no_connection);

			// 3. Get the AlertDialog from create()
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}

	/**
	 * @param url
	 */
	private void startWebView(String url) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
				WebView.setWebContentsDebuggingEnabled(true);
			}
		}

		mWebView = new WebView(this);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setAllowContentAccess(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
		webSettings.setAllowFileAccess(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		// webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		// webSettings.setSupportMultipleWindows(true);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

		mWebView.addJavascriptInterface(new JsInterface(), "android");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			webSettings.setAllowUniversalAccessFromFileURLs(true);
			webSettings.setAllowFileAccessFromFileURLs(true);
		}

		mWebView.setWebViewClient(new ControlWebViewClient(this));
	}

	@Override
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(this, orientaton,
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this, orientaton);
	}

	@Override
	// Detect when the back button is pressed
	public void onBackPressed() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
		} else {
			// Let the system handle the back button
			super.onBackPressed();
		}
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		} else
			Log.v("Control", "Internet Connection Not Present");

		return false;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			//float azimuth_angle = event.values[0];
			float pitch_angle = event.values[1];
			//float roll_angle = event.values[2];
			_powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
			if ((pitch_angle >= 150 || pitch_angle <= -150)
					&& _powerManager.isScreenOn()) {
				screenOnOff(true);
			} else {
				screenOnOff(false);
			}

		}
	}

	public void screenOnOff(Boolean screenOff) {
		try {
			if (!wakeLock.isHeld() && screenOff) {
				wakeLock.acquire();
			}

			else if (!screenOff && wakeLock.isHeld())
				wakeLock.release();
		} catch (Exception e) {
			// do something useful
		}
	}

}
