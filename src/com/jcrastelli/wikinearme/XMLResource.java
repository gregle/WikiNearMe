package com.jcrastelli.wikinearme;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class XMLResource extends ListActivity {

	private final static String TAG = XMLResource.class.getSimpleName();
	ArrayList<String> items = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    try {
	        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();       
	        factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser(); 
	        xpp.setInput(new InputStreamReader(
	            getUrlData("http://api.wikilocation.org/articles?radius=2000&format=xml&lat=47.6097&lng=-122.3331")));
	
	        while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
	            Log.i(TAG, "doc started");
	            if (xpp.getEventType() == XmlPullParser.START_TAG) {
	                if (xpp.getName().equals("entry")) {
	                    items.add(xpp.getAttributeValue(0));
	                }
	            }
	            xpp.next();
	        }
	    } catch (Throwable t) {
	        Toast.makeText(this, "Request failed: " + t.toString(),
	                Toast.LENGTH_LONG).show();
	    }
	
	    //setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, items));
	}
	
	public InputStream getUrlData(String url) throws URISyntaxException, ClientProtocolException, IOException {
	    DefaultHttpClient client = new DefaultHttpClient();
	    HttpGet method = new HttpGet(new URI(url));
	    HttpResponse res = client.execute(method);
	    return res.getEntity().getContent();
	}
}