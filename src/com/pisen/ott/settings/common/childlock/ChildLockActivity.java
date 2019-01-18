package com.pisen.ott.settings.common.childlock;

import android.content.Intent;
import android.izy.preference.PreferencesUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.OTTDialog;
import com.pisen.ott.service.LockScreenService;
import com.pisen.ott.service.childlock.ChildLockView;
import com.pisen.ott.settings.R;
import com.pisen.ott.settings.SettingConfig;
import com.pisen.ott.settings.SingleChoiceDialog;
import com.pisen.ott.settings.SingleChoiceDialog.OnItemCheckedChangeListener;
import com.pisen.ott.widget.FocusLinearLayout;

/**
 * 儿童锁主界面 主要负责从这里跳转到修改密码
 * 
 * @author mugabutie
 *
 */

public class ChildLockActivity extends OTTBaseActivity implements
		FocusLinearLayout.OnItemClickFocusListener,
		FocusLinearLayout.OnItemKeyFocusListener, OnClickListener {

	private Button btnImmediateLock = null;
	private static boolean isLocked = false;
	private TextView txtIsOpenChildrenLock = null;
	private FocusLinearLayout settingsLayout;
	private RelativeLayout relModifyPassWord;
	private RelativeLayout relOpenLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_childlock);
		setInitiConfig();
		initViewControl();
	}

	/**
	 * 设置第一次进入儿童锁配置的属性
	 */
	public void setInitiConfig() {
		if (PreferencesUtils.getString(SettingConfig.ChildrenPassWordContent,
				"").equalsIgnoreCase("")) {
			PreferencesUtils.setString(SettingConfig.ChildrenPassWordContent,
					"LLLL");
		}

		if (!PreferencesUtils
				.getBoolean(SettingConfig.ChildrenLockState, false)) {
			PreferencesUtils.setBoolean(SettingConfig.ChildrenLockState, false);
		}
	}

	// 初始化界面控件
	public void initViewControl() {
		setTitle("儿童锁", "限制儿童收看电视，可通过家长输入密码解锁");
		btnImmediateLock = (Button) findViewById(R.id.btn_ImmediateLock);
		txtIsOpenChildrenLock = (TextView) findViewById(R.id.txt_isopen_childrenlock);
		relModifyPassWord = (RelativeLayout) findViewById(R.id.lrel_modify_password);
		relModifyPassWord.setOnClickListener(this);
		relOpenLock = (RelativeLayout) findViewById(R.id.lrel_open_lock);
		openOrCloseString(false);
		btnImmediateLock.setOnClickListener(this);
		settingsLayout = (FocusLinearLayout) findViewById(R.id.childlockHomepage);
		settingsLayout.setOnItemClickListener(this);
		settingsLayout.setOnItemKeyListener(this);
	}

	/**
	 * 设置是否开启或者关闭儿童锁
	 * 
	 * @param isOnClick
	 */
	public void openOrCloseString(boolean isOnClick) {

		if (isOnClick) {
			if (isOpenLock()) {
				SettingConfig.setChildLockEnabled(false);
				LockScreenService.sendCmd(this,
						LockScreenService.CMD_CHILD_LOCK);
			} else {
				SettingConfig.setChildLockEnabled(true);
			}
		}
		setControlVisible();

	}

	/**
	 * 设置控件是否可见
	 */
	public void setControlVisible() {
		if (isOpenLock()) {
			relOpenLock.setBackgroundResource(R.drawable.table_round_top);
			//relOpenLock.setPadding(23, 0, 23, 0);
			relModifyPassWord.setVisibility(View.VISIBLE);
			btnImmediateLock.setVisibility(View.VISIBLE);
			txtIsOpenChildrenLock.setText("开启");
		} else {
			relOpenLock.setBackgroundResource(R.drawable.table_round_single);
			//relOpenLock.setPadding(23, 0, 23, 0);
			relModifyPassWord.setVisibility(View.GONE);
			btnImmediateLock.setVisibility(View.GONE);
			txtIsOpenChildrenLock.setText("关闭");

		}

	}

	/**
	 * 判断是否开启儿童锁
	 * 
	 * @return
	 */
	public boolean isOpenLock() {
		if (SettingConfig.getChildLockState())
			return true;
		else
			return false;

	}

	/**
	 * 获取设置的密码
	 * 
	 * @return
	 */
	public String getLockPassWord() {
		return PreferencesUtils.getString(
				SettingConfig.ChildrenPassWordContent, "");
	}

	/**
	 * 设定儿童锁的开关
	 * 
	 * @param lockState
	 * @param event
	 */
	public void setLocker(boolean lockState, KeyEvent event) {
		isLocked = lockState;
	}

	/**
	 * 处理各种点击事件
	 * 
	 * @param v
	 * @param event
	 */
	public void ClickView(View v, KeyEvent event) {
		Log.i("onclick", "onclick...");
		switch (v.getId()) {
		case R.id.lrel_open_lock:
			// 开启关闭儿童锁
			if (event.getAction() != 0) {
				isLocked = !isLocked;
				openOrCloseString(true);
			}
			break;
		case R.id.actionbarLayout:
			// 返回actionbar
			finish();
		default:
			break;
		}
	}

	/**
	 * 儿童锁开关
	 */
	public void SingleChoiceDialogAutoSleep() {
		SingleChoiceDialog<String> dialog = new SingleChoiceDialog<String>(this);
		dialog.setTitle("儿童锁开关");
		String[] OpenClose = new String[] { "关闭", "打开" };
		dialog.setItems(OpenClose);

		if (SettingConfig.getChildLockState()) {
			dialog.setCheckedItem(OpenClose[1]);
		} else
			dialog.setCheckedItem(OpenClose[0]);

		dialog.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
			@Override
			public void onItemCheckedChanged(OTTDialog dialog, int whichItem) {

				if (0 == whichItem) {// 关闭
					unLock();
					SettingConfig.setChildLockEnabled(false);
				} else
					SettingConfig.setChildLockEnabled(true);
				setControlVisible();
			}
		});
		dialog.show();
	}

	@Override
	public void onItemClick(View v) {
	    if (v.getId() == R.id.lrel_open_lock) {
			SingleChoiceDialogAutoSleep();
		}
	}

	@Override
	public void onItemKeyLeft(View v, KeyEvent event) {

		ClickView(v, event);
	}

	@Override
	public void onItemKeyRight(View v, KeyEvent event) {

		ClickView(v, event);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() != R.id.lrel_modify_password)// 启动锁屏的服务
		{
			ChildLockView.IsRetreivePassWord = false;
			LockScreenService.sendCmd(this, LockScreenService.CMD_CHILD_LOCK);
		} else{
			//修改密码
			Intent ModifyPassWord = new Intent(this,ChildLockPasswordChangeActivity.class);
			startActivity(ModifyPassWord);
		}
	}
	
	/**
	 * 打开锁屏
	 */
	public void unLock()
	{
		LockScreenService.sendCmd(this, LockScreenService.CMD_CHILD_LOCK);
	}

}
