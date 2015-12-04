package com.huakai.CokAssistant;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class TaskHelperUtil {

	private static final String GAMEMAINACTIVITYNAME = "com.clash.of.kings.EmpireActivity";
	private static final String GAMEPACKAGENAME = "com.hcg.cok.gp";
	private static final String MYPACKAGENAME = "com.huakai.cok_assistant";
	private static final String MYMAINACTIVITYNAME = "com.huakai.cok_assistant.MainActivity";
	private static final String FILEROOT = "/data/data/com.hcg.cok.gp/shared_prefs";
	private static Context context;
	private static ActivityManager am;
	private static TaskHelperUtil tMgr;
	public int cFileIndex = -1;
	private static int nextIndex = -1;
	private final static ArrayList<File> folds  = new ArrayList<File>();
	private final static List<String> acountNames = new ArrayList<String>();
	private TaskHelperUtil(){}

	public static TaskHelperUtil getInstance(Context mContext){
		if(tMgr==null) {
			tMgr = new TaskHelperUtil();
			context = mContext;
			am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		}
		return tMgr;
	}

	public ArrayList<File> getFolds(){
		return folds;
	}

	public void initData(){
		folds.clear();
		acountNames.clear();
		FileInputStream fis = null;
		BufferedInputStream in = null;
		BufferedReader reader = null;
		try {
			fis = new FileInputStream("/sdcard/COK_Assistant/run.conf");
			in = new BufferedInputStream(fis);
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8")); 
			String temp;
			while((temp = reader.readLine())!=null){
				if(temp.startsWith("//"))
					continue;
				String[] temps=temp.split("[|]+");
				folds.add(new File(FILEROOT+temps[1]));
				acountNames.add(temps[0]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(fis!=null) fis.close();
				if(in!=null) in.close();
				if(reader!=null)reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}

	}

	private void renameFold(int arg1, int arg2){
		try {
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(process.getOutputStream());  
			os.writeBytes("mv "+FILEROOT+" "+folds.get(arg1).getAbsolutePath()+"\n");  
			os.writeBytes("mv "+folds.get(arg2).getAbsolutePath()+" "+FILEROOT+"\n");  
			os.writeBytes("exit\n");  
			os.flush();  
			os.close();
			cFileIndex = arg2;
		}catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private static void killProcess(int pid){
		try {
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(process.getOutputStream());  
			os.writeBytes("kill "+pid+"\n");  
			os.writeBytes("exit\n");  
			os.flush();  
			os.close();
		}catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public static void killApplication(String pName){
		List<RunningAppProcessInfo> rInfos = am.getRunningAppProcesses();
		for(RunningAppProcessInfo info:rInfos){
			if(pName.equals(info.processName)){
				killProcess(info.pid);
			}
		}
	}

	public void startActivity(String pName, String aName){
		Intent i = new Intent();  
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setClassName(pName, aName);  
		context.startActivity(i);
	}
	
	public void switchGameAccount(int method){
		nextIndex = cFileIndex==getFolds().size()-1?0:cFileIndex+1;
		if(method==1)
			nextIndex = tMgr.cFileIndex==0?getFolds().size()-1:tMgr.cFileIndex-1;
		else if(method==2){
			startActivity(MYPACKAGENAME, MYMAINACTIVITYNAME);
			return;
		}
		Toast.makeText(context, "切换到账号["+acountNames.get(nextIndex)+"]\n请稍后..", 0).show();
		new Thread() {
			public void run(){
				try {
					killApplication(GAMEPACKAGENAME);
					renameFold(cFileIndex,nextIndex);
					Thread.sleep(5000);
					startActivity(GAMEPACKAGENAME,GAMEMAINACTIVITYNAME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

}
