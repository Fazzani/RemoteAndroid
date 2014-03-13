package com.xbmc.control.util;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * @author 922261
 *
 */
public final class ControlWebViewClient extends WebViewClient {
	
	private Context context;
	ProgressDialog progressDialog;
	/**
	 * @param context
	 */
	public ControlWebViewClient(Context context) {
		
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
	 */
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (url.endsWith(".mp4") || url.endsWith(".avi")) {
			MediaPlayer mp = new MediaPlayer();
			try {
				
				mp.setDataSource(url);
				mp.setScreenOnWhilePlaying(true);
				mp.prepare();
				mp.start();
			} catch (IOException e) {
				Log.e("Player", e.getMessage());
			}
		} else
			view.loadUrl(url);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.webkit.WebViewClient#onReceivedError(android.webkit.WebView, int, java.lang.String, java.lang.String)
	 */
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		
		Toast.makeText(this.context, "Oh no! " + description, Toast.LENGTH_SHORT)
				.show();
		((Activity) context).finish();
	}
	
	/* (non-Javadoc)
	 * @see android.webkit.WebViewClient#onLoadResource(android.webkit.WebView, java.lang.String)
	 */
	public void onLoadResource(WebView view, String url) {
		
//		if (progressDialog == null && url.endsWith(".php")||url.endsWith(".html")) {
//			// in standard case YourActivity.this
//			progressDialog = new ProgressDialog(context);
//			progressDialog.setMessage("Loading...");
//			progressDialog.show();
//		}
	}

	/* (non-Javadoc)
	 * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
	 */
	public void onPageFinished(WebView view, String url) {
//		try {
//			if (progressDialog.isShowing()) {
//				progressDialog.dismiss();
//				progressDialog = null;
//			}
//		} catch (Exception exception) {
//			progressDialog.dismiss();
//			progressDialog = null;
//			exception.printStackTrace();
//		}
	}

}
