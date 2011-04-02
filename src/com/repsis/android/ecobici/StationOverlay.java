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
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.google.gson.Gson;

public class StationOverlay extends ItemizedOverlay<OverlayItem> implements Runnable {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private String stationId;
	private String stationName;
	private String stationInfo;
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
		
		pd = ProgressDialog.show(mContext, mContext.getString(R.string.loadingTitle), mContext.getString(R.string.loadingText), true, false);

		Thread thread = new Thread(this);
		thread.start();
		return true;
	}

	public void run() {
		stationInfo = Ecobici.getStationInfo(stationId);
		
		handler.sendEmptyMessage(0);
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			
			if (stationInfo == "error" || stationInfo == "" || stationInfo == "[]") {
				Toast.makeText(mContext, mContext.getString(R.string.loadingStationInfoError), Toast.LENGTH_LONG).show();
			}else{
				Gson json = new Gson();
				Station station = json.fromJson(stationInfo, Station.class);
				
				String stationParking = station.parking;
				String stationBikes = station.bikes;
				
				AlertDialog.Builder b = new AlertDialog.Builder(mContext);
				b.setMessage(mContext.getString(R.string.infoBikes) + ": " + stationBikes + "\n" + mContext.getString(R.string.infoParking) + ": " + stationParking);
				b.setTitle(stationName).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.cancel();
					}
				});
				AlertDialog info = b.create();
				info.show();
			}
		}
	};
}
