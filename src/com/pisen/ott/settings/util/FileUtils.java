package com.pisen.ott.settings.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.pisen.ott.settings.R;

import android.content.Context;
import android.izy.util.LogCat;
import android.izy.util.StringUtils;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * 文件操作工具包
 * @author Liuhc
 * @version 1.0 2014年11月21日 下午4:33:54
 */
public class FileUtils {

	// SD卡存放Lancher相关文件根目录
	public final static String APPROOT = "OTTBox";
	// SD卡存放Lancher升级更新文件目录
	public final static String UPDATE = "Update";
	// SD卡存放Lancher设置相关文件目录
	public final static String SETTING = "Setting";
	
	// SD卡存放OTTSetting相关文件根目录
	public final static String APP_SETTINGROOT = "OTTSetting";
		
	/**
	 * 获得根目录
	 * @return
	 */
	public static String getAppPath(String appName) {
		return Environment.getExternalStorageDirectory() + File.separator + appName + File.separator;
	}

	/**
	 * 获得Lancher系统更新目录
	 * @return
	 */
	public static String getUpdatePath() {
		return getAppPath(APPROOT) + UPDATE + File.separator;
	}

	/**
	 * 获得Lancher系统更新UI目录
	 * file : UIVersion/级数/图片名称
	 * @return
	 */
	public static String getUpdatePath(String file) {
		return getUpdatePath() + file + File.separator;
	}
	
	/**
	 * 获得OTTSetting设置(OTTBox设置App)目录
	 * @return
	 */
	public static String getSettingPath() {
		return getAppPath(APP_SETTINGROOT) + SETTING + File.separator;
	}
	
	/**
	 * 获得OTTSetting设置(OTTBox设置App)相关目录
	 * @return
	 */
	public static String getSettingPath(String file) {
		return getSettingPath() + file + File.separator;
	}


	/**
	 * 检查是否存在SD卡
	 * @return
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获得系统更新文件目录对象
	 * @param context
	 * @return
	 */
	public static String getUpdateFile(Context context) {
		String filePath = "";
		// 如SD卡已存在，则存储；反之存在data目录下
		if (hasSdcard()) {
			// SD卡路径
			filePath = getUpdatePath(context.getString(R.string.UiVersion));
		} else {
			filePath = context.getCacheDir().getPath() + File.separator + UPDATE 
					+ File.separator + context.getString(R.string.UiVersion);
		}
		File destDir = new File(filePath);
		if (!destDir.exists()) {
			boolean isCreate = destDir.mkdirs();
			LogCat.i("<<FileUtils>> " + filePath + " has created. " + isCreate);
		}
		return filePath;
	}

	/**
	 * 获取文件大小，单位为byte（若为目录，则包括所有子目录和文件）
	 * 
	 * @param file
	 * @return
	 */
	public static long getFileSize(File file) {
		long size = 0;
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] subFiles = file.listFiles();
				if (subFiles != null) {
					int num = subFiles.length;
					for (int i = 0; i < num; i++) {
						size += getFileSize(subFiles[i]);
					}
				}
			} else {
				size += file.length();
			}
		}
		return size;
	}


	public static boolean isExists(File dir, String fileName) {
		return new File(dir, fileName).exists();
	}
	
	public static boolean isExists(String dir, String fileName) {
		return new File(dir, fileName).exists();
	}
	
	public static boolean isExists(String filePath) {
		return new File(filePath).exists();
	}


	/**
	 * 读取文本文件
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String read(Context context, String fileName) {
		try {
			FileInputStream in = context.openFileInput(fileName);
			return readInStream(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private static String readInStream(FileInputStream inStream) {
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}

			outStream.close();
			inStream.close();
			return outStream.toString();
		} catch (IOException e) {
			Log.i("FileTest", e.getMessage());
		}
		return null;
	}

	/**
	 * 向App写图片
	 * @param buffer
	 * @param folder
	 * @param fileName
	 * @return 成功返回True
	 */
	public static boolean writeFile(byte[] buffer, String folder, String fileName) {
		boolean writeSucc = false;

		if (!StringUtils.isEmpty(folder)) {
			File fileDir = new File(folder);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}

			File file = new File(folder + fileName);
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(file);
				out.write(buffer);
				writeSucc = true;
			} catch (Exception e) {
				e.printStackTrace();
				writeSucc = false;
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (IOException e) {
					e.printStackTrace();
					writeSucc = false;
				}
			}
		}
		if (writeSucc) {
			LogCat.i(folder+fileName+"saved success.");
		}else{
			LogCat.i(folder+fileName+"saved failed.");
		}
		return writeSucc;
	}

	/**
	 * 根据文件绝对路径获取文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		if (StringUtils.isEmpty(filePath))
			return "";
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}

	/**
	 * 根据文件的绝对路径获取文件名但不包含扩展名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileNameNoFormat(String filePath) {
		if (StringUtils.isEmpty(filePath)) {
			return "";
		}
		int point = filePath.lastIndexOf('.');
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1, point);
	}

	/**
	 * 获取文件扩展名
	 * @param fileName
	 * @return
	 */
	public static String getFileFormat(String fileName) {
		if (StringUtils.isEmpty(fileName))
			return "";
		int point = fileName.lastIndexOf('.');
		return fileName.substring(point + 1);
	}

	/**
	 * 获取文件大小
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		long size = 0;
		File file = new File(filePath);
		if (file != null && file.exists()) {
			size = file.length();
		}
		return size;
	}

	/**
	 * 获取文件大小
	 * 
	 * @param size
	 *            字节
	 * @return
	 */
	public static String getFileSize(long size) {
		if (size <= 0)
			return "0";
		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		float temp = (float) size / 1024;
		if (temp >= 1024) {
			return df.format(temp / 1024) + "M";
		} else {
			return df.format(temp) + "K";
		}
	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize(long fileS) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 获取目录文件大小
	 * 
	 * @param dir
	 * @return
	 */
	public static long getDirSize(File dir) {
		if (dir == null) {
			return 0;
		}
		if (!dir.isDirectory()) {
			return 0;
		}
		long dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				dirSize += file.length();
			} else if (file.isDirectory()) {
				dirSize += file.length();
				dirSize += getDirSize(file); // 递归调用继续统计
			}
		}
		return dirSize;
	}

	/**
	 * 获取目录文件个数
	 * 
	 * @param f
	 * @return
	 */
	public long getFileList(File dir) {
		long count = 0;
		File[] files = dir.listFiles();
		count = files.length;
		for (File file : files) {
			if (file.isDirectory()) {
				count = count + getFileList(file);// 递归
				count--;
			}
		}
		return count;
	}

	public static byte[] toBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;
		while ((ch = in.read()) != -1) {
			out.write(ch);
		}
		byte buffer[] = out.toByteArray();
		out.close();
		return buffer;
	}

	/**
	 * 计算SD卡的剩余空间
	 * @return 返回-1，说明没有安装sd卡
	 */
	public static long getFreeDiskSpace() {
		String status = Environment.getExternalStorageState();
		long freeSpace = 0;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				freeSpace = availableBlocks * blockSize / 1024;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return -1;
		}
		return (freeSpace);
	}

	/**
	 * 新建目录
	 * @param directoryName
	 * @return
	 */
	public static boolean createDirectory(String directoryName) {
		boolean status;
		if (!directoryName.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + directoryName);
			status = newPath.mkdir();
			status = true;
		} else
			status = false;
		return status;
	}

	public static File createFile(String folderPath, String fileName) {
		File destDir = new File(folderPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		return new File(folderPath, fileName + fileName);
	}

	/**
	 * 删除目录(包括：目录里的所有文件)
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteDirectory(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isDirectory()) {
				String[] listfile = newPath.list();
				// delete all files within the specified directory and then
				// delete the directory
				try {
					for (int i = 0; i < listfile.length; i++) {
						File deletedFile = new File(newPath.toString() + "/" + listfile[i].toString());
						deletedFile.delete();
					}
					newPath.delete();
					Log.i("deleteDirectory", fileName);
					status = true;
				} catch (Exception e) {
					e.printStackTrace();
					status = false;
				}

			} else
				status = false;
		} else
			status = false;
		return status;
	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteFile(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isFile()) {
				try {
					Log.i("deleteFile", fileName);
					newPath.delete();
					status = true;
				} catch (SecurityException se) {
					se.printStackTrace();
					status = false;
				}
			} else
				status = false;
		} else
			status = false;
		return status;
	}
}
