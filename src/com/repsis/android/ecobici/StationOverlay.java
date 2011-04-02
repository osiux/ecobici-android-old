package com.repsis.android.ecobici;

import java.util.ArrayList;
import org.apache.commons.lang.StringEscapeUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.google.gson.Gson;

public class StationOverlay extends ItemizedOverlay<OverlayItem> implements Runnable {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private String stationId;
	private String stationName;
	private String stationBikes;
	private String stationParking;
	private ProgressDialog pd;

	public StationOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}
	
	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		stationId = item.getTitle();
		stationName = StringEscapeUtils.unescapeHtml(item.getSnippet());
		
		pd = ProgressDialog.show(mContext, "Espera..", "Obteniendo informacion", true, false);

		Thread thread = new Thread(this);
		thread.start();
		return true;
	}

	public void run() {
		String stationInfo = Ecobici.getStationInfo(stationId);
		
		Gson json = new Gson();
		Station station = json.fromJson(stationInfo, Station.class);
		
		stationParking = station.parking;
		stationBikes = station.bikes;
		
		handler.sendEmptyMessage(0);
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			
			AlertDialog.Builder b = new AlertDialog.Builder(mContext);
			b.setMessage("Bicicletas: " + stationBikes + "\nEspacios: " + stationParking).setTitle(stationName).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.cancel();
				}
			});
			AlertDialog info = b.create();
			info.show();
		}
	};
}
