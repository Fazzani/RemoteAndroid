package com.xbmc.control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;


public class SplashScreenActivity extends Activity {
	/** Durée d'affichage du SplashScreen */
	   protected int _splashTime = 2000; 

	   private Thread splashTread;

	   /** Chargement de l'Activity */
	   @Override
	   public void onCreate(Bundle savedInstanceState) 
	   {
	      super.onCreate(savedInstanceState);
	      
	      setContentView(R.layout.splash);

	      final SplashScreenActivity sPlashScreen = this; 

	      /** Thread pour l'affichage du SplashScreen */
	      splashTread = new Thread() 
	      {
	         @Override
	         public void run() 
	         {
	            try 
	            {
	                 synchronized(this)
	                 {
	                    wait(_splashTime);
	                 }
	             } catch(InterruptedException e) {} 
	             finally 
	             {
	                finish();
	                Intent i = new Intent();
	                i.setClass(sPlashScreen, MainActivity.class);
	                startActivity(i);
	             }
	          }
	       };

	       splashTread.start();
	    }
	    @Override
	    public boolean onTouchEvent(MotionEvent event) 
	    {
	       /** Si l'utilisateur fait un mouvement de haut en bas on passe à l'Activity principale */
	       if (event.getAction() == MotionEvent.ACTION_DOWN) 
	       {
		   synchronized(splashTread)
	           {
	                splashTread.notifyAll();
	           }
	       }
	       return true;
	    }
	    @SuppressLint("NewApi")
		@Override
	    public void onWindowFocusChanged(boolean hasFocus) {
	            super.onWindowFocusChanged(hasFocus);
	        if (hasFocus) {
	        	
	        	this.getWindow().getDecorView().setSystemUiVisibility(
	                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	                    | View.SYSTEM_UI_FLAG_FULLSCREEN
	                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
	    }
	    
}
