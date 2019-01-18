package com.pisen.ott.settings.upgrade;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pisen.ott.OTTAlertDialog;
import com.pisen.ott.settings.R;

public class UpDateTipDialog extends OTTAlertDialog{
    private LinearLayout lryProcess;
    private TextView txtContent;
	protected Button btnOk;
	protected Button btnCancel;
	public UpDateTipDialog(Context context) {
		super(context);
		initView(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void initView(Context context) {
		setContentView(R.layout.ui_dialog_setting_update);
		lryProcess = (LinearLayout) findViewById(R.id.downProcess);
		btnOk =  (Button) findViewById(R.id.btnokUpdate); 
		btnOk.requestFocus();
		btnCancel = (Button) findViewById(R.id.btncancleUpdate);
		txtContent = (TextView) findViewById(R.id.downContent);
	}
	
	/**
	 * 设置版本更新说明
	 * @param str
	 */
	public void setUpdateContent(String str)
	{
		txtContent.setText(str);
	}
	/**
	 * 设置进度条是否可见
	 * @param isVisible
	 */
	public void setVisible(boolean isVisible)
	{	
	  if(isVisible)
	  {
		txtContent.setVisibility(View.GONE);
		lryProcess.setVisibility(View.VISIBLE);
	  }
	  else
	  {
		btnOk.requestFocus();
		txtContent.setVisibility(View.VISIBLE);
		lryProcess.setVisibility(View.GONE);  
	  }
	}
}
