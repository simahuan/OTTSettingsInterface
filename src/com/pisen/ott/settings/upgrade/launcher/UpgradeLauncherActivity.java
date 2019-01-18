package com.pisen.ott.settings.upgrade.launcher;

import android.content.Intent;
import android.izy.util.LogCat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.R;
import com.pisen.ott.settings.upgrade.launcher.UpdateManager.OnDownLoadListener;
import com.pisen.ott.settings.util.HttpUtils;

/**
 * 系统升级 
 * 盒子的版本升级和介绍
 * @author Liuhc
 * @version 1.0 2014年12月16日 下午5:05:43
 */
public class UpgradeLauncherActivity extends OTTBaseActivity implements OnClickListener{

	private TextView txtMessage;
	private LinearLayout progressLayout;
	private ProgressBar pbarUpgrade;
	private TextView txtProgress;
	private Button btnVersion;
	private Button btnUpdate;
	private Button btnCancel;
	
    private Handler handler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    		case UpdateManager.UPDATE_NEW:
    			setViewStatu(msg.what);
    			break;
    		case UpdateManager.UPDATE_LATEST:
    			setViewStatu(msg.what);
    			break;
    		case UpdateManager.UPDATE_FAIL:
    			setViewStatu(msg.what);
    			break;
    		case UpdateManager.DOWN_OVER:
    			setViewStatu(msg.what);
    			break;
    		case UpdateManager.DOWN_ERROR:
    			setViewStatu(msg.what);
    			break;
			}
    	};
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upgrade_launcher);
		setTitle(R.string.setting_upgrade, R.string.setting_upgrade_describle);
		
		this.initViews();
	}
	
	private void initViews(){
		txtMessage = (TextView) findViewById(R.id.txtMessage);
		progressLayout = (LinearLayout) findViewById(R.id.progressLayout);
		pbarUpgrade = (ProgressBar) findViewById(R.id.pbarUpgrade);
		txtProgress = (TextView) findViewById(R.id.txtProgress);
		btnVersion = (Button) findViewById(R.id.btnVersion);
		btnUpdate = (Button) findViewById(R.id.btnUpdate);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		
		txtMessage.setText(getString(R.string.system_upgrade_checkversion));
		if (HttpUtils.isNetworkConnected(this)) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					//检测更新
					UpdateManager.getUpdateManager().checkAppUpdate(UpgradeLauncherActivity.this, handler);
				}
			}, 1500);
		}else{
			setViewStatu(UpdateManager.UPDATE_FAIL);
			txtMessage.setText(getString(R.string.system_upgrade_neterro));
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnVersion:
			startActivity(new Intent(this,UpgradeLauncherDetailActivity.class));
			break;
		case R.id.btnUpdate:
			setViewStatu(UpdateManager.DOWN_UPDATE);
			UpdateManager.getUpdateManager().startDownload(new OnDownLoadListener() {
				@Override
				public void OnDownling(int dwonType, final int progress, final String tmpFileSize, final String apkFileSize) {
					if (dwonType == UpdateManager.DOWN_UPDATE) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								pbarUpgrade.setProgress(progress);
								txtProgress.setText(tmpFileSize + "/" + apkFileSize);
							}
						});
					}
				}
			});
			break;
		case R.id.btnCancel:
			UpdateManager.getUpdateManager().cancelDownload();
			setViewStatu(UpdateManager.UPDATE_NEW);
			pbarUpgrade.setProgress(0);
			txtProgress.setText("");
			break;
		default:
			break;
		}
	}
	
	/**
	 * 设置更新状态
	 * @param type
	 */
	private void setViewStatu(int type){
		switch (type) {
		case UpdateManager.UPDATE_NEW:
			txtMessage.setText(getString(R.string.system_upgrade_new));
			progressLayout.setVisibility(View.GONE);
			btnVersion.setVisibility(View.GONE);
			btnUpdate.setVisibility(View.VISIBLE);
			btnCancel.setVisibility(View.GONE);
			LogCat.i("<Update> new version");
			break;
		case UpdateManager.UPDATE_LATEST:
			txtMessage.setText(getString(R.string.system_upgrade_latest));
			progressLayout.setVisibility(View.GONE);
			btnVersion.setVisibility(View.VISIBLE);
			btnUpdate.setVisibility(View.GONE);
			btnCancel.setVisibility(View.GONE);
			LogCat.i("<Update> don't have new version");
			break;
		case UpdateManager.UPDATE_FAIL:
			txtMessage.setText(getString(R.string.system_upgrade_checkfailed));
			progressLayout.setVisibility(View.GONE);
			btnVersion.setVisibility(View.GONE);
			btnUpdate.setVisibility(View.GONE);
			btnCancel.setVisibility(View.GONE);
			LogCat.i("<Update> check up version failed.");
			break;
		case UpdateManager.DOWN_UPDATE:
			txtMessage.setText(getString(R.string.system_upgrade_down));
			progressLayout.setVisibility(View.VISIBLE);
			btnVersion.setVisibility(View.GONE);
			btnUpdate.setVisibility(View.GONE);
			btnCancel.setVisibility(View.VISIBLE);
			break;
		case UpdateManager.DOWN_OVER:
			UpdateManager.getUpdateManager().installApk();
			txtMessage.setText(getString(R.string.system_upgrade_install));
			progressLayout.setVisibility(View.GONE);
			btnVersion.setVisibility(View.GONE);
			btnUpdate.setVisibility(View.GONE);
			btnCancel.setVisibility(View.GONE);
			break;
		case UpdateManager.DOWN_ERROR:
			setViewStatu(UpdateManager.UPDATE_NEW);
			txtMessage.setText(getString(R.string.system_upgrade_downerro));
			break;
		case UpdateManager.DOWN_NOSDCARD:
//			downloadDialog.dismiss();
//			Toast.makeText(mContext, "无法下载安装文件，请检查SD卡是否挂载", 3000).show();
			break;
		}
	}
}
