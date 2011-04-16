package com.repsis.android.ecobici;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class About extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        TextView repsisUrl = (TextView) findViewById(R.id.aboutUrl);
        repsisUrl.setMovementMethod(LinkMovementMethod.getInstance());
        
        TextView facebookUrl = (TextView) findViewById(R.id.facebookUrl);
        facebookUrl.setMovementMethod(LinkMovementMethod.getInstance());
        
        TextView iconPhotoCredit = (TextView) findViewById(R.id.iconPhotoCredit);
        iconPhotoCredit.setMovementMethod(LinkMovementMethod.getInstance());
	}
}