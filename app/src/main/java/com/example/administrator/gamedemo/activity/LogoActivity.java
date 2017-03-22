package com.example.administrator.gamedemo.activity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.administrator.gamedemo.R;

import java.util.Timer;
import java.util.TimerTask;

public class LogoActivity extends Activity implements OnClickListener {
	private ImageView logo;
	private TextView name;
	private RelativeLayout first;
	private ImageView iv;
	private boolean inited = false;
	private Button skip;
	private CountDownTimer mTimer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo2);
		
	    logo = (ImageView)findViewById(R.id.logo);
	    name = (TextView)findViewById(R.id.title);
	    first = (RelativeLayout) findViewById(R.id.first);
	    iv = (ImageView) findViewById(R.id.iv);
	    iv.setOnClickListener(this);
	    skip = (Button) findViewById(R.id.skip);
	    skip.setOnClickListener(this);
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
	 * ��timer
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

	/**
	 * �ر�timer
	 */
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
