package com.pisen.ott.settings.common;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pisen.ott.OTTAlertDialog;
import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.OTTDialog;
import com.pisen.ott.settings.R;
import com.pisen.ott.settings.SettingConfig;
import com.pisen.ott.settings.SingleChoiceDialog;
import com.pisen.ott.settings.SingleChoiceDialog.OnItemCheckedChangeListener;
import com.pisen.ott.settings.common.childlock.ChildLockActivity;
import com.pisen.ott.settings.common.location.CityManager;
import com.pisen.ott.settings.common.location.LocationCityActivity;
import com.pisen.ott.settings.util.InputMethodHelper;
import com.pisen.ott.settings.util.KeyUtils;
import com.pisen.ott.widget.FocusLinearLayout;
import com.pisen.ott.widget.FocusLinearLayout.OnItemClickFocusListener;
import com.pisen.ott.widget.FocusLinearLayout.OnItemKeyFocusListener;

/**
 * 通用设置UI
 * 
 * @author Liuhc
 * @version 1.0 2014年12月9日 下午2:44:24
 */
public class CommonSettingsActivity extends OTTBaseActivity implements OnItemClickFocusListener, OnItemKeyFocusListener {
	private static final String[] AUTOSLEEP_TIMEITEM = new String[] { SettingConfig.AutoSleepTimeDefault, SettingConfig.ThirteenMinutes, 
															 SettingConfig.SixTeenMinutes,SettingConfig.NineTeenMinutes };
	
	private static final String[] SCREENPRO_TIMEITEM = new String[] { SettingConfig.ScreenProTimeDefault, SettingConfig.OneMinutes, SettingConfig.FourMinutes,
		SettingConfig.EightMinutes };
	
	private TextView txtSystemName;
	private TextView txtSreenproTime;
	private TextView txtAutosleepTime;
	private TextView txtChildrenLock;
	//位置信息
	private TextView txtSystemLocation;
	
	// 输入法名称
	private TextView txtInputMethodName;
	// 输入法数组
	String[] inputMethods;
	// 输入法索引
	int inputMethodIdex;
	// 输入法工具
	private InputMethodHelper inputMethodHelper;
	// 输入法设置layout
	RelativeLayout inputMethodLayout;
	//选择输入法的对话框
	SingleChoiceDialog<Object> inputMehtodDialog;
	// 恢复出厂设置layout
	RelativeLayout restoreLayout;
	//恢复出厂设置警告对话框
	OTTAlertDialog dialogAlert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inputMethodHelper = new InputMethodHelper(this);
		setContentView(R.layout.common);
		setTitle(R.string.common_set, R.string.common_set_describle);
		this.initViews();
	}
    @Override
    protected void onStart() {
    	//刷新输入法
    	inputMethodHelper = new InputMethodHelper(this);
    	txtInputMethodName.setText(inputMethodHelper.defaultInputMethodName);
    	inputMethods = inputMethodHelper.inputMethodNames;
    	inputMethodIdex = inputMethodHelper.getArrayIndex(inputMethodHelper.defaultInputMethodName);
    	super.onStart();
    }
	public void initViews() {
		((FocusLinearLayout) findViewById(R.id.commonsettingsLayout)).setOnItemClickListener(this);
		((FocusLinearLayout) findViewById(R.id.commonsettingsLayout)).setOnItemKeyListener(this);
		txtSystemName = (TextView) findViewById(R.id.txtSystemName);
		txtSystemName.setText(SettingConfig.getDeviceName());
		txtSreenproTime = (TextView) findViewById(R.id.txtSreenproTime);
		txtAutosleepTime = (TextView) findViewById(R.id.txtAutosleepTime);
		txtSystemLocation = (TextView) findViewById(R.id.txtSystemLocation);
		// 输入法
		inputMethodLayout = (RelativeLayout) findViewById(R.id.inputMethodLayout);
		txtInputMethodName = (TextView) findViewById(R.id.txtInputMethodName);
		txtInputMethodName.setText(inputMethodHelper.defaultInputMethodName);
		// 输入法数组
		inputMethods = inputMethodHelper.inputMethodNames;
		// 输入法数组索引
		inputMethodIdex = inputMethodHelper.getArrayIndex(inputMethodHelper.defaultInputMethodName);
		// 恢复出厂设置
		restoreLayout = (RelativeLayout) findViewById(R.id.restoreLayout);

		txtChildrenLock = (TextView) findViewById(R.id.txtChildStatu);
		if(SettingConfig.getChildLockState())
		{
			txtChildrenLock.setText("已开启");
		}else
			txtChildrenLock.setText("已关闭");

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (SettingConfig.getChildLockState()) {
			txtChildrenLock.setText("已开启");
		}else
			txtChildrenLock.setText("已关闭");
		txtSreenproTime.setText(SettingConfig.getScreenPro());
		
		txtAutosleepTime.setText(SettingConfig.getAutoSleep());
		txtSystemLocation.setText(CityManager.getInstance(this).getDefaultCityName());
	}

	@Override
	public void onItemClick(View arg0) {
		switch (arg0.getId()) {
		// 输入法设置
		case R.id.inputMethodLayout:
			showInputChoiceDialog();
			break;
		case R.id.childLayout:
			startActivity(new Intent(this, ChildLockActivity.class));
			break;
		// 设备名称设置
		case R.id.deviceNameLayout:
			startActivityForResult(new Intent(this, InputDeviceNameActivity.class), SettingConfig.REQUEST_DEVNAME);
			break;
		case R.id.locationLayout:
			startActivity(new Intent(this, LocationCityActivity.class));
			break;
		// 恢复出厂设置
		case R.id.restoreLayout:
			showRecoveryDialog();
			break;
		// 屏幕保护
		case R.id.sreenproLayout:
//			startActivityForResult(new Intent(this, ScreenProActivity.class), SettingConfig.REQUEST_SCREENPRO);
			SingleChoiceDialogScreenPro();
			break;
		// 自动休眠
		case R.id.autosleepLayout:
			SingleChoiceDialogAutoSleep();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SettingConfig.REQUEST_DEVNAME && resultCode == RESULT_OK) {
			txtSystemName.setText(data.getStringExtra(SettingConfig.getDeviceName()));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 屏保设置
	 */
	public void SingleChoiceDialogScreenPro() {
		SingleChoiceDialog<String> dialog = new SingleChoiceDialog<String>(this);
		dialog.setTitle("屏保时间");
		dialog.setItems(SCREENPRO_TIMEITEM);
		dialog.setCheckedItem(SettingConfig.getScreenProIndex());
		dialog.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
			@Override
			public void onItemCheckedChanged(OTTDialog dialog, int whichItem) {
				 SettingConfig.setScreensaverTime(getScreenProTimeOutMill(whichItem));
				 SettingConfig.setScreenProIndex(whichItem);
				 SettingConfig.setScreenPro(SCREENPRO_TIMEITEM[whichItem]);
				 txtSreenproTime.setText(SCREENPRO_TIMEITEM[whichItem]);
			}
		});
		dialog.show();
	}

	
	/**
	 * 自动休眠
	 */
	public void SingleChoiceDialogAutoSleep() {
		SingleChoiceDialog<String> dialog = new SingleChoiceDialog<String>(this);
		dialog.setTitle("自动休眠时间");
		dialog.setItems(AUTOSLEEP_TIMEITEM);
		dialog.setCheckedItem(SettingConfig.getAutoSleepIndex());
		dialog.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
			@Override
			public void onItemCheckedChanged(OTTDialog dialog, int whichItem) {
				SettingConfig.setScreenSleepTime(getAutoSleepTimeOutMill(whichItem));
				SettingConfig.setAutoSleepIndex(whichItem);
				SettingConfig.setAutoSleep(AUTOSLEEP_TIMEITEM[whichItem]);
				txtAutosleepTime.setText(AUTOSLEEP_TIMEITEM[whichItem]);
				Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, getAutoSleepTimeOutMill(whichItem));
			}
		});
		dialog.show();
	}
    /**
     *弹出选择输入法对话框 
     */
	private void showInputChoiceDialog() {
		inputMehtodDialog = new SingleChoiceDialog<Object>(this);
		inputMehtodDialog.setTitle("输入法");
		inputMehtodDialog.setItems(inputMethodHelper.inputMethodNames);
		inputMehtodDialog.setCheckedItem(inputMethodHelper.getItemId(inputMethodHelper.defaultInputMethodId));
		inputMehtodDialog.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
			@Override
			public void onItemCheckedChanged(OTTDialog dialog, int itemIndex) {
				// 设置输入法
				Settings.Secure.putString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, inputMethodHelper.list.get(itemIndex).getId().toString());
				// 重新加载helper对象
				inputMethodHelper = new InputMethodHelper(getApplication());
				// 更新UI
				txtInputMethodName.setText(inputMethodHelper.defaultInputMethodName);
				//关闭对话框
				inputMehtodDialog.dismiss();
			}
		});
		inputMehtodDialog.show();
	}
	
	/**
	 * 计算实际屏保时间(毫秒)
	 */
	public int getScreenProTimeOutMill(int whichItem) {
		int tempTime = 0;
		switch (whichItem) {
		case 0:
			tempTime = SettingConfig.ScreenProTimeDefMill;
			break;
		case 1:
			tempTime = SettingConfig.OneMinutesMill;
			break;
		case 2:
			tempTime = SettingConfig.FourMinutesMill;
			break;
		case 3:
			tempTime = SettingConfig.EightMinutesMill;
			break;
		}
		return tempTime;
	}
	
	/**
	 * 计算实际修眠时间(毫秒)
	 */
	public int getAutoSleepTimeOutMill(int whichItem) {
		int tempTime = 0;
		switch (whichItem) {
		case 0:
			tempTime = SettingConfig.FifteenMinutes;
			break;
		case 1:
			tempTime = SettingConfig.HalfHours;
			break;
		case 2:
			tempTime = SettingConfig.OneHours;
			break;
		case 3:
			tempTime = SettingConfig.HalfAnHours;
			break;
		}
		return tempTime;
	}

	@Override
	public void onItemKeyLeft(View v, KeyEvent event) {
		switch (v.getId()) {
		// 输入法
		case R.id.inputMethodLayout:
			if (inputMethodIdex <= 0) {
				inputMethodIdex = inputMethods.length - 1;
			} else {
				inputMethodIdex--;
			}
			// 设置默认输入法
			Settings.Secure.putString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, inputMethodHelper.list.get(inputMethodIdex).getId()
					.toString());
			// 重新加载helper对象
			inputMethodHelper = new InputMethodHelper(getApplication());
			// 更新UI
			txtInputMethodName.setText(inputMethodHelper.defaultInputMethodName);
			break;
		case R.id.sreenproLayout:
			txtSreenproTime.setText(getScreenProPrevItem(SCREENPRO_TIMEITEM, txtSreenproTime.getText().toString()));
			break;
		case R.id.autosleepLayout:
			txtAutosleepTime.setText(getAutoPrevItem(AUTOSLEEP_TIMEITEM, txtAutosleepTime.getText().toString()));
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemKeyRight(View v, KeyEvent event) {
		switch (v.getId()) {
		//输入法
		case R.id.inputMethodLayout:
			if (inputMethodIdex >= inputMethods.length - 1) {
				inputMethodIdex = 0;
			} else {
				inputMethodIdex++;
			}
			// 设置默认输入法
			Settings.Secure.putString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, inputMethodHelper.list.get(inputMethodIdex).getId()
					.toString());
			// 重新加载helper对象
			inputMethodHelper = new InputMethodHelper(getApplication());
			// 更新UI
			inputMethodIdex = inputMethodHelper.getArrayIndex(inputMethodHelper.defaultInputMethodName);
			txtInputMethodName.setText(inputMethodHelper.defaultInputMethodName);
			break;
		case R.id.sreenproLayout:
			txtSreenproTime.setText(getScreenProNextItem(SCREENPRO_TIMEITEM, txtSreenproTime.getText().toString()));
			break;
		case R.id.autosleepLayout:
			txtAutosleepTime.setText(getAutoNextItem(AUTOSLEEP_TIMEITEM, txtAutosleepTime.getText().toString()));
			break;
		default:
			break;
		}
	}
	
	/**
	 * 获取自动休眠上一项
	 */
	private String getAutoPrevItem(String[] values,String item){
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(item)) {
				if (i == 0){
					SettingConfig.setAutoSleepIndex(values.length-1);
					SettingConfig.setAutoSleep(AUTOSLEEP_TIMEITEM[values.length-1]);
					SettingConfig.setScreenSleepTime(getAutoSleepTimeOutMill(values.length-1));
					return values[values.length-1];
				}else{
					SettingConfig.setAutoSleepIndex(i-1);
					SettingConfig.setAutoSleep(AUTOSLEEP_TIMEITEM[i-1]);
					SettingConfig.setScreenSleepTime(getAutoSleepTimeOutMill(i-1));
					return values[i-1];
				}
			}
		}
		return item;
	}
	
	/**
	 * 获取自动休眠下一项
	 */
	private String getAutoNextItem(String[] values,String item){
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(item)) {
				if (i == (values.length - 1)){
					SettingConfig.setAutoSleepIndex(0);
					SettingConfig.setAutoSleep(AUTOSLEEP_TIMEITEM[0]);
					SettingConfig.setScreenSleepTime(getAutoSleepTimeOutMill(0));
					return values[0];
				}else{
					SettingConfig.setAutoSleepIndex(i+1);
					SettingConfig.setAutoSleep(AUTOSLEEP_TIMEITEM[i+1]);
					SettingConfig.setScreenSleepTime(getAutoSleepTimeOutMill(i+1));
					return values[i+1];
				}
			}
		}
		return item;
	}
	
	/**
	 * 获得屏保上一项内容
	 */
	private String getScreenProPrevItem(String[] values, String item) {
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(item)) {
				if (i == 0) {
					SettingConfig.setScreenProIndex(values.length - 1);
					SettingConfig.setScreenPro(SCREENPRO_TIMEITEM[values.length - 1]);
					SettingConfig.setScreensaverTime(getScreenProTimeOutMill(values.length - 1));
					return values[values.length - 1];
				} else {
					SettingConfig.setScreenProIndex(i - 1);
					SettingConfig.setScreenPro(SCREENPRO_TIMEITEM[i - 1]);
					SettingConfig.setScreensaverTime(getScreenProTimeOutMill(i - 1));
					return values[i - 1];
				}
			}
		}
		return item;
	}

	/**
	 * 获得屏保下一项内容
	 */
	private String getScreenProNextItem(String[] values, String item) {
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(item)) {
				if (i == (values.length - 1)) {
					SettingConfig.setScreenProIndex(0);
					SettingConfig.setScreenPro(SCREENPRO_TIMEITEM[0]);
					SettingConfig.setScreensaverTime(getScreenProTimeOutMill(0));
					return values[0];
				} else {
					SettingConfig.setScreenProIndex(i + 1);
					SettingConfig.setScreenPro(SCREENPRO_TIMEITEM[i+1]);
					SettingConfig.setScreensaverTime(getScreenProTimeOutMill(i + 1));
					return values[i + 1];
				}
			}
		}
		return item;
	}
	
	
	private void showRecoveryDialog(){
		dialogAlert = new OTTAlertDialog(this);
		dialogAlert.setOkListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
				intent.putExtra("wipe_media", true);
				sendBroadcast(intent);
				dialogAlert.dismiss();
	        }
	    });
		dialogAlert.setCancelListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	Log.d("testMsg", "in cancel");
	        	dialogAlert.dismiss();
	        }
	    });
		dialogAlert.show();
	}
}
