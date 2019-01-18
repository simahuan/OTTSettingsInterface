package com.pisen.ott.service.screensaver;

import android.content.Context;
import android.izy.service.window.WindowViewBase;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.pisen.ott.service.LockScreenService;
import com.pisen.ott.settings.R;

public class ScreenproView extends WindowViewBase {

	public ScreenproView(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		View.inflate(context, R.layout.screen_pro_unlock, this);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		wakeUp();
		return true;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		wakeUp();
		return true;
	}

	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent event) {
		wakeUp();
		return true;
	}

	/**
	 * 唤醒屏幕
	 */
	private void wakeUp() {
		LockScreenService.sendCmd(getContext(), LockScreenService.CMD_SCREEN_SAVER_UNLOCK);
	}

}
