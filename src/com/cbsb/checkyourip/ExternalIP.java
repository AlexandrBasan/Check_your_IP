package com.cbsb.checkyourip;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.cbsb.checkyourip.R.string;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import android.util.Log;
import com.cbsb.checkyourip.R;

public class ExternalIP extends Activity implements OnClickListener {
	EditText ip;
	EditText aip;
	private static final int IDM_About = 101;	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ip = (EditText) findViewById(R.id.extip);
        aip = (EditText) findViewById(R.id.androidip);

        Button button = (Button)findViewById(R.id.btnRefresh);
        button.setOnClickListener(this);
        
        updateIP();
    }
    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(Menu.NONE, IDM_About, Menu.NONE, string.about).setIcon(R.drawable.ic_menu_info_details);
		return super.onCreateOptionsMenu(menu);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://proalab.com/?page_id=517"));
		startActivity(browserIntent);
		return super.onOptionsItemSelected(item);
	}



	public void updateIP() {
    	getCurrentIP();
    	dispAndroidIP();
    }
    
    public void onClick(View v) {
    	updateIP();
    }
    
    public String getAndroidIP () {
    	    try {
    	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
    	            NetworkInterface intf = en.nextElement();
    	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
    	                InetAddress inetAddress = enumIpAddr.nextElement();
    	                if (!inetAddress.isLoopbackAddress()) {
    	                    return inetAddress.getHostAddress().toString();
    	                }
    	            }
    	        }
    	    } catch (SocketException ex) {
    	        Log.i("externalip", ex.toString());
    	    }
    	    return null;
    }
    
    public void dispAndroidIP () {
    	String andIP;
    	aip.setText("Please wait...");
    	andIP = getAndroidIP();
    	if (andIP==null) {
    		aip.setText("Error");
    	} else {
    		aip.setText(andIP);
    	}
        
    	
    	
    }
    public void getCurrentIP () {
        ip.setText(string.please_wait);  
        try {
        	HttpClient httpclient = new DefaultHttpClient();
        	HttpGet httpget = new HttpGet("http://wiki.iti-lab.org/ip.php");
        	// HttpGet httpget = new HttpGet("http://whatismyip.everdot.org/ip");
        	// HttpGet httpget = new HttpGet("http://whatismyip.com.au/");
        	// HttpGet httpget = new HttpGet("http://www.whatismyip.org/");
        	HttpResponse response;
        	
        	response = httpclient.execute(httpget);
        	
        	//Log.i("externalip",response.getStatusLine().toString());
        	
        	HttpEntity entity = response.getEntity();
        	if (entity != null) {
        		long len = entity.getContentLength();
        		if (len != -1 && len < 1024) {
        			String str=EntityUtils.toString(entity);
        			//Log.i("externalip",str);
                    ip.setText(str);
        		} else {
        			ip.setText("Response too long or error.");
        			//debug
        			//ip.setText("Response too long or error: "+EntityUtils.toString(entity));
        			//Log.i("externalip",EntityUtils.toString(entity));
        		}            
        	} else {
        		ip.setText("Null:"+response.getStatusLine().toString());
        	}
            
        }
        catch (Exception e)
        {
            ip.setText("Error");
        }

    }
}