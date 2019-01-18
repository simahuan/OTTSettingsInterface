package com.pisen.ott.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.izy.service.window.WindowService;
import android.izy.service.window.WindowViewBase;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.pisen.ott.service.childlock.ChildLockView;
import com.pisen.ott.service.screensaver.ScreenproView;
import com.pisen.ott.settings.SettingConfig;

/**
 * 锁屏服务
 * <p>
 * 用于启动屏保与儿童锁
 * </p>
 * 
 * @author yangyp
 * @version 1.0, 2014年12月26日 上午11:08:50
 */
public class LockScreenService extends WindowService {

	static final String TAG = WindowService.class.getSimpleName();

	// 发送命令指令
	public static final String SERVICECMD = "com.pisen.ott.service.lockscreenservice";
	public static final String CMDNAME = "command";
	public static final int CMD_SCREEN_NONE = -1;
	public static final int CMD_CHILD_LOCK = 1;
	public static final int CMD_CHILD_UNLOCK = 2;
	public static final int CMD_SCREEN_SAVER_LOCK = 3;
	public static final int CMD_SCREEN_SAVER_UNLOCK = 4;

	public PowerManager mPowerManager;
	private PowerManager.WakeLock mWakeLock;
	// private KeyguardManager mKeyguardManager;

	private ContentView mContentView;
	private boolean mStartScreensaver; // 启动屏保标记

	private BroadcastReceiver mScreenReceiver;

	class ScreenReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				actionScreenOff();
			}
		}
	}

	/**
	 * 发送命令
	 * 
	 * @param context
	 * @param cmd
	 */
	public static void sendCmd(Context context, int cmd) {
		Intent i = new Intent(LockScreenService.SERVICECMD);
		i.putExtra(LockScreenService.CMDNAME, cmd);
		context.startService(i);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
				"LockScreen");
		// mKeyguardManager = (KeyguardManager)
		// getSystemService(Context.KEYGUARD_SERVICE);

		mScreenReceiver = new ScreenReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mScreenReceiver, filter);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		unregisterReceiver(mScreenReceiver);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String action = intent.getAction();
			int cmd = intent.getIntExtra(CMDNAME, -1);
			Log.i(TAG, "onStartCommand " + action + " / " + cmd);
			switch (cmd) {
			case CMD_CHILD_LOCK:
				mStartScreensaver = true;
				showChildLockView();
				break;
			case CMD_CHILD_UNLOCK:
				hideChildLock();
				break;
			case CMD_SCREEN_SAVER_LOCK:
				showScreensaver();
				break;
			case CMD_SCREEN_SAVER_UNLOCK:
				unlockScreensaver();
				break;
			default:
				break;
			}
		}

		return START_STICKY;
	}

	/**
	 * 获取当前的View
	 * 
	 * @return
	 */
	private ContentView getContentView() {
		if (mContentView == null) {
			mContentView = new ContentView(this);
			addToBackStack(mContentView, false);
		}
		return mContentView;
	}

	/**
	 * 关闭屏幕处理
	 * 
	 * @param context
	 */
	private void actionScreenOff() {
		// 判断屏保是否设置了时间
		if (SettingConfig.getScreensaverTime() > 0) {
			showScreensaver();
		} else {
			// 判断儿童锁是否开启
			if (SettingConfig.getChildLockState()) {
				showChildLockView();
			}
		}
	}

	/**
	 * 显示屏保
	 */
	private void showScreensaver() {
		if (!mStartScreensaver) {
			mStartScreensaver = true;
			getContentView().showScreenproView();
			mWakeLock.acquire(10 * 1000);
			// 设置下次屏幕休眠时间
			int nextSleepTime = SettingConfig.getScreenSleepTime() - SettingConfig.getScreensaverTime();
			SettingConfig.setSystemSettings(Settings.System.SCREEN_OFF_TIMEOUT, nextSleepTime);
		}
	}

	/**
	 * 解锁屏保
	 */
	private void unlockScreensaver() {
		// 判断儿童锁是否开启
		if (SettingConfig.getChildLockState()) {
			showChildLockView();
		} else {
			hideChildLock();
		}
	}

	/**
	 * 显示儿童锁
	 */
	private void showChildLockView() {
		getContentView().showChildLockView();
	}

	/**
	 * 解锁
	 */
	private void hideChildLock() {
		getWindowViewManager().hide();
		mStartScreensaver = false;
		mContentView = null;
		// KeyguardLock mKeyguardLock =
		// mKeyguardManager.newKeyguardLock("kLock");
		// mKeyguardLock.disableKeyguard();
	}

	static private class ContentView extends WindowViewBase {

		// private LockScreenService mLockScreenService;
		private ChildLockView mChildLockView;
		private ScreenproView mScreenproView;

		private ContentView(LockScreenService context) {
			super(context);
			// this.mLockScreenService = context;
			mChildLockView = new ChildLockView(context);
			mScreenproView = new ScreenproView(context);
			addView(mChildLockView);
			addView(mScreenproView);
		}

		public void showChildLockView() {
			// mLockScreenService.show();
			mChildLockView.setVisibility(View.VISIBLE);
			// mChildLockView.requestFocus();
			mScreenproView.setVisibility(View.GONE);
		}

		public void showScreenproView() {
			// mLockScreenService.show();
			mScreenproView.setVisibility(View.VISIBLE);
			// mScreenproView.requestFocus();
			mChildLockView.setVisibility(View.GONE);
		}
	}
}
