package com.example.administrator.gamedemo.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.view.View;
import android.view.View.OnClickListener;



import com.example.administrator.gamedemo.R;



public class LogoActivity extends Activity implements OnClickListener {
	private boolean inited = false;

	private CountDownTimer mTimer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo2);
		startTimer();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(inited){
			startTimer();
		}
	}

	/**
	 *
	 */
	private void startTimer() {
				mTimer = new CountDownTimer(3000,1000) {
					@Override
					public void onTick(long l) {

					}
					@Override
					public void onFinish() {
						Intent intent = new Intent(LogoActivity.this, MainActivity.class);
						startActivity(intent);
						stopTimer();
						finish();
					}
				}.start();
					


	}


	private void stopTimer() {

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	@Override
	public void onClick(View v) {

	}
	


	 
}
