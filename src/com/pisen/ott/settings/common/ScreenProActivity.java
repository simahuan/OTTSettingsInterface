package com.pisen.ott.settings.common;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.OTTDialog;
import com.pisen.ott.settings.R;
import com.pisen.ott.settings.SettingConfig;
import com.pisen.ott.settings.SingleChoiceDialog;
import com.pisen.ott.settings.SingleChoiceDialog.OnItemCheckedChangeListener;
import com.pisen.ott.widget.FocusLinearLayout;
import com.pisen.ott.widget.FocusLinearLayout.OnItemClickFocusListener;
import com.pisen.ott.widget.FocusLinearLayout.OnItemKeyFocusListener;

/**
 * TODO
 * 
 * @author mahuan
 * @date 2014年12月10日 下午4:02:24
 */
public class ScreenProActivity extends OTTBaseActivity implements OnItemClickFocusListener, OnItemKeyFocusListener {
	private final String TAG = "ScreenProActivity";
	private static final String[] SCREENPRO_TIMEITEM = new String[] { SettingConfig.ScreenProTimeDefault, SettingConfig.OneMinutes, SettingConfig.FourMinutes,
			SettingConfig.EightMinutes };
	private TextView txtScreenproValue;
	private FocusLinearLayout screenProtocolLayout;
	SingleChoiceDialog<String> dialog;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.common_screen_protocol);
		initView();
	}

	public void initView() {
		setTitle(R.string.common_screen_protocol_title, R.string.common_screen_protocol_subtitle);
		screenProtocolLayout = (FocusLinearLayout) findViewById(R.id.screenProtocolLayout);
		screenProtocolLayout.setOnItemClickListener(this);
		screenProtocolLayout.setOnItemKeyListener(this);
		txtScreenproValue = (TextView) findViewById(R.id.txtScreenproValue);
		txtScreenproValue.setText(SettingConfig.getScreenPro());
	}

	/**
	 * 设置屏保时间
	 */
	@Override
	public void onItemClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.screenproTimeLayout:
			SingleChoiceDialogScreenPro();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 屏保设置
	 */
	public void SingleChoiceDialogScreenPro() {
		dialog = new SingleChoiceDialog<String>(this);
		dialog.setTitle("屏保时间");
		dialog.setItems(SCREENPRO_TIMEITEM);
		dialog.setCheckedItem(SettingConfig.getScreenProIndex());
		dialog.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
			@Override
			public void onItemCheckedChanged(OTTDialog dialog, int whichItem) {
				 SettingConfig.setScreensaverTime(getScreenProTimeOutMill(whichItem));
				 SettingConfig.setScreenProIndex(whichItem);
				 SettingConfig.setScreenPro(SCREENPRO_TIMEITEM[whichItem]);
				 txtScreenproValue.setText(SCREENPRO_TIMEITEM[whichItem]);
			}
		});
		dialog.show();
	}

	
	/**
	 * 计算实际修眠时间(毫秒)
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

	@Override
	public void onItemKeyLeft(View v, KeyEvent event) {
		switch (v.getId()) {
		case R.id.screenproTimeLayout:
			txtScreenproValue.setText(getScreenProPrevItem(SCREENPRO_TIMEITEM, txtScreenproValue.getText().toString()));
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemKeyRight(View v, KeyEvent event) {
		switch (v.getId()) {
		case R.id.screenproTimeLayout:
			txtScreenproValue.setText(getScreenProNextItem(SCREENPRO_TIMEITEM, txtScreenproValue.getText().toString()));
			break;
		default:
			break;
		}
	}

	/**
	 * 上一项内容
	 */
	private String getScreenProPrevItem(String[] values, String item) {
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(item)) {
				if (i == 0) {
					SettingConfig.setScreenProIndex(values.length - 1);
					SettingConfig.setScreenPro(SCREENPRO_TIMEITEM[values.length - 1]);
					return values[values.length - 1];
				} else {
					SettingConfig.setScreenProIndex(i - 1);
					SettingConfig.setScreenPro(SCREENPRO_TIMEITEM[i - 1]);
					return values[i - 1];
				}
			}
		}
		return item;
	}

	/**
	 * 下一项内容
	 */
	private String getScreenProNextItem(String[] values, String item) {
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(item)) {
				if (i == (values.length - 1)) {
					SettingConfig.setScreenProIndex(0);
					SettingConfig.setScreenPro(SCREENPRO_TIMEITEM[0]);
					return values[0];
				} else {
					SettingConfig.setScreenProIndex(i + 1);
					SettingConfig.setScreenPro(SCREENPRO_TIMEITEM[i+1]);
					return values[i + 1];
				}
			}
		}
		return item;
	}
}
