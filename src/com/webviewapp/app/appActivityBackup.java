// WebViewApp (LGPL 3.0)
// Activity class
// Author: Kristo Vaher - www.waher.net - kristo@waher.net

// This is main activity of the application

package com.webviewapp.app;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.*;
import android.widget.Toast;

import java.io.*;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;

public class appActivityBackup extends Activity {
	
	/* Customizations */
		//String baseUrl = "http://webviewapp.waher.net/"; //Web service URL
		//String baseUrl = "http://10.0.2.2:4567/app";
		String baseUrl = "http://192.168.0.198:4567/app";
		String appSalt = "thisIsRandomSaltForHashFunctions"; //App salt
		String currentUrl = baseUrl;
	
	/* Package information (loaded in onCreate) */
		int versionCode = 0;
		String versionName = "";
		String appPackage = "";
	
	/* Additional variables */
		WebView appBrowser;
		Boolean internetConnection = true;
	
	/* For tracking scrolling positions */
	    int browserScrollY = 0; //For tracking X scrolling position
	    int browserScrollX = 0; //For tracking Y scrolling position
	    int browserContentHeight;  
	    int browserHeight;
    
    /* Used for application preferences */
	    public SharedPreferences appPreferences;
	    
	/* Used for database connection */
	    private appDatabase appDatabaseConnect;

    @Override
    public void onCreate(Bundle savedInstanceState) {

    	/* Instance */
        	super.onCreate(savedInstanceState);

        /* Set the Content View */
        	setContentView(R.layout.main);
        
    	/* Building unique identification hash */
    		String uniqueId = getUniqueId();
    	
    	/* Load package information */
	        try {
	            PackageInfo appInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
	            versionCode = appInfo.versionCode;
	            versionName = appInfo.versionName;
	            appPackage = appInfo.packageName;
	        } catch (NameNotFoundException e1) {
	            //nothing
	        }
        
        /* Cookie handling */
        	CookieSyncManager.createInstance(this);
        	CookieSyncManager.getInstance().sync();
        
        /* Get the WebView */
        	appBrowser = (WebView) findViewById(R.id.web_engine);
        
        /* Browser variables */
	        browserContentHeight = appBrowser.getContentHeight();  
	        browserHeight = appBrowser.getHeight();

        
        /* WebView settings and configuration */
	        WebSettings appBrowserSettings = appBrowser.getSettings();
	        appBrowserSettings.setSavePassword(false);
	        appBrowserSettings.setSaveFormData(false);
	        appBrowserSettings.setJavaScriptEnabled(true);
	        appBrowserSettings.setLoadsImagesAutomatically(true);
	        appBrowserSettings.setDomStorageEnabled(true);
	        appBrowserSettings.setDatabaseEnabled(true);
	        appBrowserSettings.setDatabasePath("data/data/"+appPackage+"/databases");
	        appBrowserSettings.setUserAgentString(appBrowserSettings.getUserAgentString()+" [["+uniqueId+"@"+appPackage+"@"+versionName+"@"+versionCode+"]]");
	        appBrowserSettings.setSupportZoom(false);
	        appBrowserSettings.setAllowFileAccess(true);
	        appBrowserSettings.setSupportMultipleWindows(false);
	        appBrowserSettings.setAppCacheEnabled(true);
	        appBrowserSettings.setAppCachePath("/data/data/"+appPackage+"/cache");
	        appBrowserSettings.setAppCacheMaxSize(5*1024*1024); // 5MB
	        //appBrowserSettings.setEnableSmoothTransition(true); //Android 3.0+
	        appBrowserSettings.setPluginState(WebSettings.PluginState.OFF);
	        appBrowserSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
	        appBrowserSettings.setCacheMode(WebSettings.LOAD_NORMAL);
        
        /* Prevent WebView from Opening the Browser */
	        appBrowser.setWebChromeClient(new WebChromeClient()); //Not needed if you don't need alerts, etc
	        appBrowser.setWebViewClient(new appWebViewClient());
	        appBrowser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        
        /* Adding JavaScript interface */
        	appBrowser.addJavascriptInterface(new JavaScriptInterface(this),"android");
        
        /* Get last used time (only available if application has already run before */
	        SharedPreferences settings = getPreferences(MODE_PRIVATE);
	        String lastUsed = settings.getString("lastUsed", "never");
        	Toast.makeText(this, "appActivity: App last used: "+lastUsed, Toast.LENGTH_LONG).show();
        	
        /* Using HTTP */
            String webContent = "";
	        try {
	        	webContent = getWebContents(baseUrl);
			} catch (Exception e) {
				Toast.makeText(appActivityBackup.this, "Cannot connect to API", Toast.LENGTH_LONG).show();
			}

	        
	    /* Using file system */
        	String fileAddress=getFilesDir()+"my-file.txt"; //you can also write to SD like /sdcard/my-file.txt
	        try {
	        	 writeFile(webContent,fileAddress);
			} catch (Exception e) {
				Toast.makeText(appActivityBackup.this, "Cannot write file", Toast.LENGTH_LONG).show();
			}
	        String myOutputFileContents = "";
	        try {
	        	myOutputFileContents = getFileContents(fileAddress);
			} catch (Exception e) {
				myOutputFileContents = "";
				Toast.makeText(appActivityBackup.this, "Cannot read file", Toast.LENGTH_LONG).show();
			}
	        Toast.makeText(this, "appActivity: File content was: "+myOutputFileContents, Toast.LENGTH_LONG).show();
	        
        /* Database example */
            appDatabaseConnect = new appDatabase(this);
            appDatabaseConnect.deleteAll();
            appDatabaseConnect.insert("line 1");
            appDatabaseConnect.insert("line 2");
            appDatabaseConnect.insert("line 3");
            List<String> names = appDatabaseConnect.selectAll();
            StringBuilder sb = new StringBuilder();
            sb.append("Names in database:\n");
            for (String name : names) {
               sb.append(name + "\n");
            }
            Toast.makeText(this, "appActivity: Names from database:\n"+sb.toString(), Toast.LENGTH_LONG).show();
        
        /* Starting background service */
	        Intent backgroundIntent = new Intent();
	        backgroundIntent.setAction("com.webviewapp.app.appService");
	        startService(backgroundIntent);
        
        /* Starting alarm service that will be started later on in background */
	        // get a Calendar object with current time
	        Calendar cal = Calendar.getInstance();
	        // add 20 seconds
	        cal.add(Calendar.SECOND, 20);
			Intent alarmIntent = new Intent();
			alarmIntent.setAction("com.webviewapp.app.appBroadcastReceiver");
	        alarmIntent.putExtra("background_message", "Alarm message!!");
	        // In reality, you would want to have a static variable for the request code instead of 192837
	        PendingIntent sender = PendingIntent.getBroadcast(this, 192837, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	        // Get the AlarmManager service
	        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
	        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
    	
        /* Checks for Internet connection and displays local HTML if not available */
	        if(onlineCheck()){
	        	appBrowser.loadUrl(currentUrl);
	        } else {
	        	internetConnection=false;
	        	loadNoInternet();
	        }
       
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	//Toast.makeText(appActivity.this, "onStart", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onResume() {
    	super.onResume(); 	
    	if (!onlineCheck()) {
        	internetConnection=false;
        	Toast.makeText(appActivityBackup.this, "appActivity: No internet!", Toast.LENGTH_LONG).show();
        	loadNoInternet();
    	} else if(internetConnection==false){
        	internetConnection=true;
        	Toast.makeText(appActivityBackup.this, "appActivity: Internet found!", Toast.LENGTH_LONG).show();
        	appBrowser.loadUrl(currentUrl);
    	} else {
        	//Toast.makeText(appActivity.this, "onResume", Toast.LENGTH_SHORT).show();
    	}
    }

    @Override
    protected void onPause() {
    	super.onPause();   	
    	//Toast.makeText(appActivity.this, "onPause", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onRestart() {
    	super.onRestart();
    	/* Starting up cookie synchronization again */
    	CookieSyncManager.getInstance().startSync(); 
    	//Toast.makeText(appActivity.this, "onRestart", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();	
    	
    	/* Since we are in the background, we do not need to synchronize cookies, so we stop the sync */
	    	CookieSyncManager.getInstance().stopSync();
	    	Toast.makeText(appActivityBackup.this, "appActivity: Application stopped", Toast.LENGTH_SHORT).show();
    	
    	/* This is an example of using notifications */
	    	// Get a reference to the NotificationManager:
	    	String ns = Context.NOTIFICATION_SERVICE;
	    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
	    	// Instantiate the Notification:
	    	int icon = R.drawable.notification;
	    	CharSequence tickerText = "Notification";
	    	long when = System.currentTimeMillis();
	    	Notification notification = new Notification(icon, tickerText, when);
	    	// Define the Notification's expanded message and Intent:
	    	Context context = getApplicationContext();
	    	CharSequence contentTitle = "Notification title";
	    	CharSequence contentText = "You can start the app again here!";
	    	Intent notificationIntent = new Intent(this, appActivityBackup.class);
	    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
	    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
	    	// Pass the Notification to the NotificationManager:
	    	//private static final int HELLO_ID = 1;
	    	mNotificationManager.notify(1, notification);
	    	//With sound
	    	//notification.defaults |= Notification.DEFAULT_SOUND;
	    	//notification.sound = Uri.parse("file:///sdcard/notification/ringer.mp3");
	    	//notification.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");
	    	//Vibration
	    	//notification.defaults |= Notification.DEFAULT_VIBRATE;
	    	//Lights
	    	//notification.defaults |= Notification.DEFAULT_LIGHTS;
	    	
	    /* This is an example of using time and saved preferences */
	    	Time now = new Time();
	    	now.setToNow();			
			SharedPreferences settings = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor sharedEditor = settings.edit();
			sharedEditor.putString("lastUsed", now.format("%d/%m/%Y %H:%M"));
			sharedEditor.commit();
			
		/* This closes the background service should it be needed */
	        //Intent backgroundIntent = new Intent(this, appService.class);
	        //stopService(backgroundIntent);
    	
    	/* This will close the application entirely should it lose focus, comment this out if you want to let it continue (usually you should) */
			this.finish();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();	
    	//Toast.makeText(appActivity.this, "onDestroy", Toast.LENGTH_SHORT).show();
    }
    
    /* Class that prevents opening the Browser, opening URL's in WebView */
    private class appWebViewClient extends WebViewClient {
        /* Here we make sure that URL's are either opened within app or in external browser */
        @Override
        public boolean shouldOverrideUrlLoading(WebView appBrowser, String url) {
        	/* There should be a check here to make sure the new URL is still in this domain to block external websites */
    		if(url.startsWith(baseUrl) || url.startsWith("javascript:") || url.startsWith("localhost://")){
    			if(url.startsWith(baseUrl)){
    				/* Here we maintain page reload scroll */
    				if(url.endsWith("#maintainscroll")){
    					browserScrollY = appBrowser.getScrollY(); 
    					browserScrollX = appBrowser.getScrollX(); 
    				} else {
    					browserScrollY = 0; 
    					browserScrollX = 0; 
    				}
    				currentUrl=url;
    			}
    			/* Local HTML files from assets folder should be referred to in HTML like localhost://android_asset/local_page.html */
    			if(url.startsWith("localhost://")){
    				Toast.makeText(appActivityBackup.this, "appActivity: Loading local page", Toast.LENGTH_SHORT).show();
    				url=url.replace("localhost://","file:///android_asset/");
    				appBrowser.loadUrl(url);
    			} else {
    				appBrowser.loadUrl(url);
    			}
    		} else {
    			Toast.makeText(appActivityBackup.this, "appActivity: Launching browser", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
    		}
            return true;
        }
        /* Makes sure that if page had POST submitted and page is returned from history, this will re-send the POST */
        @Override
        public void onFormResubmission(WebView appBrowser, Message dontResend, Message resend) {
        	resend.sendToTarget();
        }
        /* Page has finished loading, attempt to scroll to previous location */
        @Override
        public void onPageFinished(WebView appBrowser, String url) {
        	if(browserScrollX!=0 || browserScrollY!=0){
        		appBrowser.scrollTo(browserScrollX,browserScrollY); 
        	}
        	super.onPageFinished(appBrowser, url);
        }
        /* Action when resource is being load */
        //Resource requests can be overwritten by API 11 version with:
        //WebViewClient#shouldInterceptRequest(WebView view, String url)
        @Override
        public void onLoadResource(WebView appBrowser, String url){
            super.onLoadResource(appBrowser, url); //you cannot overwrite this
        }
    }
    
    /* Controlling Android hardware buttons */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (internetConnection==true && keyCode == KeyEvent.KEYCODE_BACK && appBrowser.canGoBack()) {
        	appBrowser.goBackOrForward(-1);
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_BACK){
        	/* This will never actually 'close' the app when back button is pressed
        	 * as the last backwards activity. Please note that app will still closed 
        	 * if finish() is called in onStop() */
        	this.moveTaskToBack(true); //if this is set to false then pressing 'back' will always put the app in the background instead of moving back the activity tree
        	return true; 
        }
        if (internetConnection==true && keyCode == KeyEvent.KEYCODE_SEARCH) {
        	appBrowser.loadUrl("javascript:searchButton();");
            return true;
        }
        if (internetConnection==true && keyCode == KeyEvent.KEYCODE_MENU) {
        	appBrowser.loadUrl("javascript:menuButton();");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /* Checking if networking is active */
    public boolean onlineCheck(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //if (netInfo != null && netInfo.isConnectedOrConnecting()) { // this checked also 'if connecting'
        if (netInfo != null && netInfo.isConnected()) {
        	return true;
        } else {
        	return false;
        }
    }
    
    /* This is used to show HTML view if there is no Internet connection */
    public void loadNoInternet(){
		String summary = "<html><body style=\"text-align:center;font:12px Ubuntu;color:#2E2222;\"><p>This application requires internet access!</p><p><a style=\"color:#9C1111; text-decoration:none; font-size:16px; font-weight:bold;\" href=\"javascript:android.attemptReload();\"/>ATTEMPT RELOAD</a></p><p><img src=\"file:///android_asset/no-internet.jpg\"/></p></body></html>";
		appBrowser.loadDataWithBaseURL("file:///not/needed/",summary, "text/html", "utf-8", "");
    	/* This commented out section uses cache instead of displaying an error */
    	//myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    	//myWebView.loadUrl(baseUrl));
    }
    
    /* Calculates hash string from another string */
    public String hashString(String input) throws NoSuchAlgorithmException{
    	MessageDigest m = MessageDigest.getInstance("MD5");
    	m.reset();
    	m.update(input.getBytes());
    	byte[] digest = m.digest();
    	BigInteger bigInt = new BigInteger(1,digest);
    	String hashtext = bigInt.toString(16);
    	// Now we need to zero pad it if you actually want the full 32 chars.
    	while(hashtext.length() < 32 ){
    	  hashtext = "0"+hashtext;
    	}
    	return hashtext;
    }
    
    /* Attempts to return hashed unique ID for the device */
    public String getUniqueId(){
    	String AndroidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID); //Note that this may not be unique!
    	String AndroidSerial = null;      
    	try {                                                                               
    		Class<?> c = Class.forName("android.os.SystemProperties");        	 
    		Method get = c.getMethod("get", String.class, String.class );                     
    		AndroidSerial = (String)(   get.invoke(c, "ro.serialno", "unknown" )  );              
    	} catch (Exception ignored){}
    	TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
    	String AndroidDevice = tManager.getDeviceId();
    	String uniqueSerial = appPackage+AndroidId+AndroidSerial+AndroidDevice+appSalt;
    	try {                                                                               
    		uniqueSerial = hashString(uniqueSerial);            
    	} catch (Exception ignored){}
    	return uniqueSerial;
    }
    
    /* JavaScript interface */
    public class JavaScriptInterface {
        Context appContext;
        /* Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
        	appContext = c;
        }
        /* Attempts reloading the active URL if internet is available */
        public void attemptReload() {
        	if(!onlineCheck()){
        		Toast.makeText(appActivityBackup.this, "appActivity: Still no connection!", Toast.LENGTH_LONG).show();
        	} else {
            	internetConnection=true;
            	Toast.makeText(appActivityBackup.this, "appActivity: Internet found, reloading!", Toast.LENGTH_LONG).show();
            	appBrowser.loadUrl(currentUrl);
        	}
            
        } 
        /* Shows short alert to the user */
        public void showToast(String toast) {
            Toast.makeText(appActivityBackup.this, toast, Toast.LENGTH_LONG).show();
        }
    }
    
    /* Write contents into a file */
    public void writeFile(String contents,String filename) throws Exception {
        BufferedWriter out = new BufferedWriter(new FileWriter(filename));
        out.write(contents);
        out.close();

    }
    
    /* Read file contents into a String */
    public String getFileContents(String filename) throws Exception {
    	BufferedReader buf = new BufferedReader(new FileReader(filename));
    	StringBuffer inLine = new StringBuffer();
    	String text;
    	while((text=buf.readLine())!=null) {
    		inLine.append(text);
    		inLine.append("\n");
    	}
    	buf.close();
    	return inLine.toString();
    }
    
    /* Ask for contents from web or execute a web script */
    public String getWebContents(String url) throws Exception {
        URL urlConnect = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlConnect.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.connect();
        BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnect.openStream()), 4096);
        String line;
        StringBuilder sb =  new StringBuilder();
        while ((line = rd.readLine()) != null) {
        		sb.append(line);
        }
        rd.close();
        String contentOfMyInputStream = sb.toString();
        return contentOfMyInputStream;
    }
        
}
