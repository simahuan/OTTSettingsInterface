package com.pisen.ott.settings.graphics;

import android.app.MboxOutputModeManager;
import android.app.SystemWriteManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler.Value;
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
 * 图像与声音UI
 * @author Liuhc
 * @version 1.0 2014年12月9日 下午2:44:24
 */
public class GraphicsSettingsActivity extends OTTBaseActivity implements OnItemClickFocusListener,OnItemKeyFocusListener{

	private TextView txtResolution;
	private TextView txtVoiceOutput;
	private TextView txtCompression;
	
	private MboxOutputModeManager mMboxOutputModeManager = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graphics);
		setTitle(R.string.graphics_title, R.string.graphics_title_describle);
		((FocusLinearLayout) findViewById(R.id.graphicsLayout)).setOnItemClickListener(this);
		((FocusLinearLayout) findViewById(R.id.graphicsLayout)).setOnItemKeyListener(this);
		
		txtResolution = (TextView) findViewById(R.id.txtResolution);
		txtVoiceOutput = (TextView) findViewById(R.id.txtZoom);
		txtCompression = (TextView) findViewById(R.id.txtCompression);
		
		txtVoiceOutput.setText(SettingConfig.getAudioOutput());//存在默认值
		txtCompression.setText(SettingConfig.getCompression());
		
		mMboxOutputModeManager = (MboxOutputModeManager)getSystemService(Context.MBOX_OUTPUTMODE_SERVICE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		txtResolution.setText(SettingConfig.getResolution());
	}

	@Override
	public void onItemClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.resolutionLayout:
			startActivity(new Intent(this, ResolutionActivity.class));
			break;
		case R.id.zoomDisplacementLayout:
			startActivity(new Intent(this, ZoomDisplacementActivity.class));
			break;
		case R.id.voiceOutputLayout:
			showVoiceOutputDialog();
			break;
		case R.id.compressionLayout:
			showCompDialog();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onItemKeyLeft(View v, KeyEvent event) {
		switch (v.getId()) {
		case R.id.voiceOutputLayout:
			final String audioFormat  = refreshView(true, txtVoiceOutput, R.id.voiceOutputLayout);
			SettingConfig.setAudioOutput(audioFormat);
			setAudioOutputModel(audioFormat);
			break;
		case R.id.compressionLayout:
			SettingConfig.setCompression(refreshView(true, txtCompression, R.id.compressionLayout));
			break;
		default:
			break;
		}		
	}

	
	@Override
	public void onItemKeyRight(View v, KeyEvent event) {
		switch (v.getId()) {
		case R.id.voiceOutputLayout:
			final String audioFormat  = refreshView(true, txtVoiceOutput, R.id.voiceOutputLayout);
			SettingConfig.setAudioOutput(audioFormat);
			setAudioOutputModel(audioFormat);
			break;
		case R.id.compressionLayout:
			SettingConfig.setCompression(refreshView(false, txtCompression, R.id.compressionLayout));
			break;
		default:
			break;
		}			
	}
	
	/**
	 * 按下左右键时动态刷新内容
	 * @param isLeft 是否按下左键
	 * @param tv 当前要刷新的TextView
	 * @param layoutId 当前要刷新的TextView所属LayoutId
	 * @return 返回要刷新的内容;
	 */
	private String refreshView(boolean isLeft,TextView tv,int layoutId){
		String[] arrays = null;
		if (layoutId == R.id.voiceOutputLayout) {
			arrays = SettingConfig.getAudioOutputList(this);
		}else if(layoutId == R.id.compressionLayout){
			arrays = SettingConfig.getCompressionList(this);
		}
		
		String value = "";
		if (arrays != null) {
			if (isLeft) 
				value = getPreviousItem(arrays, tv.getText().toString());
			else
				value = getNextItem(arrays, tv.getText().toString());
		}
		tv.setText(value);
		return value;
	}
	
	/**
	 * 获取上一项内容
	 * @param values
	 * @param item
	 * @return
	 */
	private String getPreviousItem(String[] values,String item){
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(item)) {
				if (i == 0)
					return values[values.length-1];
				else
					return values[i-1];
			}
		}
		return item;
	}
	
	/**
	 * 获取下一项内容
	 * @param values
	 * @param item
	 * @return
	 */
	private String getNextItem(String[] values,String item){
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(item)) {
				if (i == (values.length - 1))
					return values[0];
				else
					return values[i+1];
			}
		}
		return item;
	}
	
	/**
	 * 声音输出对话框
	 */
	private void showVoiceOutputDialog(){
		final SingleChoiceDialog<Object> voiceDialog = new SingleChoiceDialog<Object>(this);
		voiceDialog.setTitle(getString(R.string.graphics_voice_output));
		voiceDialog.setItems(SettingConfig.getAudioOutputList(GraphicsSettingsActivity.this));
		voiceDialog.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
			@Override
			public void onItemCheckedChanged(OTTDialog dialog, int itemIndex) {
				SettingConfig.setAudioOutput(voiceDialog.getCheckedItem(itemIndex).toString());
				final String audioFormat = SettingConfig.getAudioOutput();
				txtVoiceOutput.setText(audioFormat);
				setAudioOutputModel(audioFormat);
			}
		});
		voiceDialog.setCheckedItem(SettingConfig.getAudioOutput());
		voiceDialog.show();
	}
	
	/**
	 * 压缩对话框
	 */
	private void showCompDialog(){
		final SingleChoiceDialog<Object> compDialog = new SingleChoiceDialog<Object>(this);
		compDialog.setTitle(getString(R.string.graphics_compression));
		compDialog.setItems(SettingConfig.getCompressionList(GraphicsSettingsActivity.this));
		compDialog.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
			@Override
			public void onItemCheckedChanged(OTTDialog dialog, int itemIndex) {
				SettingConfig.setCompression(compDialog.getCheckedItem(itemIndex).toString());
				txtCompression.setText(SettingConfig.getCompression());
			}
		});
		compDialog.setCheckedItem(SettingConfig.getCompression());
		compDialog.show();
	}
	
	
	//设置具体音频信号接口
	private void setAudioOutputModel(String value) {
		if ("PCM".equals(value)) {
			mMboxOutputModeManager.setDigitalVoiceValue(getResources()
					.getString(R.string.graphics_voice_pcm));
		} else if ("SPDIF".equals(value)) {
			mMboxOutputModeManager.setDigitalVoiceValue(getResources()
					.getString(R.string.graphics_voice_spdif));
		} else if ("HDMI".equals(value)) {
			mMboxOutputModeManager.setDigitalVoiceValue(getResources()
					.getString(R.string.graphics_voice_hdmi));
		} else {
			mMboxOutputModeManager.setDigitalVoiceValue(getResources()
					.getString(R.string.graphics_voice_pcm));
		}
	}
	
}
