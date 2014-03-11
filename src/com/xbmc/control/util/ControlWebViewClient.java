package com.xbmc.control.util;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public final class ControlWebViewClient extends WebViewClient {
	private Context context;

	public ControlWebViewClient(Context context) {
		
		this.context = context;
	}

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

	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		
		Toast.makeText(this.context, "Oh no! " + description, Toast.LENGTH_SHORT)
				.show();
	}
}
