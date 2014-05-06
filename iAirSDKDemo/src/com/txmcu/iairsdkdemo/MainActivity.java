package com.txmcu.iairsdkdemo;


import android.app.Activity;
import android.os.Bundle;
import com.txmcu.iairsdkdemo.R;
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
		//	getSupportFragmentManager().beginTransaction()
		//			.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	

	

}
