package com.repsis.android.ecobici;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

public class Ecobici {
	private static final String STATIONS_URL = "http://ecobici.osiux.ws/Ecobici.php?mode=getStations";
	private static final String INFO_URL = "http://ecobici.osiux.ws/Ecobici.php?mode=getStationInfo&id=";
	
	public static String getStations() {
		// The data that is retrieved 
		String result = null;
		
		try {
		     // This assumes that you have a URL from which the response will come
		     URL url = new URL(STATIONS_URL);
		     
		     // Open a connection to the URL and obtain a buffered input stream
		     URLConnection connection = url.openConnection();
		     InputStream inputStream = connection.getInputStream();
		     BufferedInputStream bufferedInput = new BufferedInputStream(inputStream);
		     
		     // Read the response into a byte array
		     ByteArrayBuffer byteArray = new ByteArrayBuffer(50);
		     int current = 0;
		     while((current = bufferedInput.read()) != -1){
		          byteArray.append((byte)current);
		     }
		
		     // Construct a String object from the byte array containing the response
		     result = new String(byteArray.toByteArray());
		} catch (Exception e) {
			return "error";
		}
		
		// Handle the result
		return result;
	}
	
	public static String getStationInfo(int stationId) {
		// The data that is retrieved 
		String result = null;
		
		try {
		     // This assumes that you have a URL from which the response will come
		     URL url = new URL(INFO_URL + stationId);
		     
		     // Open a connection to the URL and obtain a buffered input stream
		     URLConnection connection = url.openConnection();
		     InputStream inputStream = connection.getInputStream();
		     BufferedInputStream bufferedInput = new BufferedInputStream(inputStream);
		     
		     // Read the response into a byte array
		     ByteArrayBuffer byteArray = new ByteArrayBuffer(50);
		     int current = 0;
		     while((current = bufferedInput.read()) != -1){
		          byteArray.append((byte)current);
		     }
		
		     // Construct a String object from the byte array containing the response
		     result = new String(byteArray.toByteArray());
		} catch (Exception e) {
			
		}
		
		// Handle the result
		return result;
	}
}
