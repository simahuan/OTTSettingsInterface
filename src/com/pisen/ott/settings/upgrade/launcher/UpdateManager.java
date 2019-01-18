package com.pisen.ott.settings.upgrade.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.pisen.ott.settings.util.FileUtils;
import com.pisen.ott.settings.util.HttpUtils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.izy.util.LogCat;
import android.izy.util.StringUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.volley.RequestManager;

/**
 * 应用程序更新工具包
 * @author Liuhc
 * @version 1.0 2014年12月17日 下午4:39:17
 */
public class UpdateManager {

	public static final int DOWN_NOSDCARD = 0x01;
	public static final int DOWN_UPDATE = 0x02;
	public static final int DOWN_OVER = 0x03;
	public static final int DOWN_ERROR = 0x04;
	
	public static final int UPDATE_NEW = 0x05;
	public static final int UPDATE_LATEST = 0x06;
	public static final int UPDATE_FAIL   = 0x07;
    
	//超时设置 10m
	public static final int TIME_OUT_CONNECT = 10000;
	public static final int TIME_OUT_READ = 10000;
	
	private static UpdateManager updateManager;
	
	private Context mContext;
    //进度值
    private int progress;
    //下载线程
    private Thread downLoadThread;
    //终止标记
    private boolean interceptFlag;
	//更新描述
	private String updateMsg = "";
	//返回的安装包url
	private String apkUrl = "";
	//下载包保存路径
    private String savePath = "";
	//apk保存完整路径
	private String apkFilePath = "";
	//临时下载文件路径
	private String tmpFilePath = "";
	//下载文件大小
	private String apkFileSize;
	//已下载文件大小
	private String tmpFileSize;
	
	private String curVersionName = "";
	private int curVersionCode;
	private UpdateInfo mUpdate;
	
	private OnDownLoadListener mDownListener;
    private Handler mHandler = null;
    
	public static UpdateManager getUpdateManager() {
		if(updateManager == null){
			updateManager = new UpdateManager();
		}
		updateManager.interceptFlag = false;
		return updateManager;
	}
	
	/**
	 * 获取Lanucher版本信息
	 */
	private void getCurrentVersion(){
        try { 
        	PackageInfo info = mContext.getPackageManager().getPackageInfo("com.pisen.ott", 0);
        	curVersionName = info.versionName;
        	curVersionCode = info.versionCode;
        } catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
	}
	
	/**
	 * 检查App更新
	 * @param context
	 * @param handler UI更新句柄
	 */
	public void checkAppUpdate(Context context,Handler handler){
		this.mContext = context;
		this.mHandler = handler;
		getCurrentVersion();
		RequestManager.addRequest(new StringRequest(HttpUtils.URL_UPDATE, new Listener<String>() {
			@Override
			public void onResponse(String arg0) {
				LogCat.i("<Update> check up inner version:"+arg0);
				if (!StringUtils.isEmpty(arg0)) {
					//系统版本信息
					mUpdate = UpdateInfo.json2bean(arg0);
					if(mUpdate != null){
						Message msg = new Message();
						if(curVersionCode < mUpdate.System.InternalVersion){
							apkUrl = mUpdate.System.Apk;
							updateMsg = mUpdate.System.Description;
							msg.what = UPDATE_NEW;
						}else{
							msg.what = UPDATE_LATEST;
						}
						mHandler.sendMessage(msg);
					}
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				mHandler.sendEmptyMessage(UPDATE_FAIL);
				LogCat.i("<Update> check up new version failed:"+arg0.getMessage());
			}
		}), mContext);
	}	
	

	private Runnable mdownApkRunnable = new Runnable() {	
		@Override
		public void run() {
			FileOutputStream fos = null;
			InputStream is = null;
			try {
				String apkName = "OTTBoxApp_"+mUpdate.System.SystemVersion+".apk";
				String tmpApk = "OTTBoxApp_"+mUpdate.System.SystemVersion+".tmp";
				//判断是否挂载了SD卡
				if(FileUtils.hasSdcard()){
					savePath = FileUtils.getUpdatePath();
					File file = new File(savePath);
					if(!file.exists()){
						file.mkdirs();
					}
					apkFilePath = savePath + apkName;
					tmpFilePath = savePath + tmpApk;
				}
				
				//没有挂载SD卡，无法下载文件
				if(apkFilePath == null || apkFilePath.equals("")){
					mHandler.sendEmptyMessage(DOWN_NOSDCARD);
					return;
				}
				
				File ApkFile = new File(apkFilePath);
				//是否已下载更新文件
				if(ApkFile.exists()){
					installApk();
					return;
				}
				
				//输出临时下载文件
				File tmpFile = new File(tmpFilePath);
				fos = new FileOutputStream(tmpFile);
				
				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setConnectTimeout(TIME_OUT_CONNECT);
				conn.setReadTimeout(TIME_OUT_READ);
				conn.connect();
				int length = conn.getContentLength();
				is = conn.getInputStream();
				
				//显示文件大小格式：2个小数点显示
		    	DecimalFormat df = new DecimalFormat("0.00");
		    	//进度条下面显示的总文件大小
		    	apkFileSize = df.format((float) length / 1024 / 1024) + "MB";
				
				int count = 0;
				byte buf[] = new byte[1024];
				
				do{   		   		
		    		int numread = is.read(buf);
		    		count += numread;
		    		//进度条下面显示的当前下载文件大小
		    		tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
		    		//当前进度值
		    	    progress =(int)(((float)count / length) * 100);
		    	    if (mDownListener != null) {
		    	    	 //更新进度
		    	    	mDownListener.OnDownling(DOWN_UPDATE, progress, tmpFileSize, apkFileSize);
					}
		    		if(numread <= 0){	
		    			//下载完成 - 将临时下载文件转成APK文件
						if(tmpFile.renameTo(ApkFile)){
							//通知安装
							mHandler.sendEmptyMessage(DOWN_OVER);
						}
		    			break;
		    		}
		    		fos.write(buf,0,numread);
		    	}while(!interceptFlag);//点击取消就停止下载
				
			} catch(Exception e){
				e.printStackTrace();
				mHandler.sendEmptyMessage(DOWN_ERROR);
				LogCat.e("<<UpdateManager>> down apk error... :"+e.toString());
			}finally{
				try {
					fos.close();
					is.close();
					FileUtils.deleteFile(tmpFilePath);
				} catch (IOException e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(DOWN_ERROR);
					LogCat.e("<<UpdateManager>> close io error...:"+e.toString());
				}
			}
			
		}
	};
	
	/**
	* 下载apk
	* @param url
	*/	
	public void startDownload(OnDownLoadListener listerner){
		this.mDownListener = listerner;
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}
	
	public void cancelDownload(){
		interceptFlag = true;
	}
	
	/**
    * 安装apk
    * @param url
    */
	public void installApk(){
		File apkfile = new File(apkFilePath);
        if (!apkfile.exists()) {
            return;
        }    
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive"); 
        mContext.startActivity(i);
	}
	
	public interface OnDownLoadListener{
		public void OnDownling(int dwonType,int progress,String tmpFileSize,String apkFileSize);
	}
}
