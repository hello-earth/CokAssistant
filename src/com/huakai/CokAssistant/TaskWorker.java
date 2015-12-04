package com.huakai.CokAssistant;

import java.io.DataOutputStream;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class TaskWorker extends BroadcastReceiver{

	int runningTimes;
	static IBRInteraction brInteraction;
	Context mContext;
	boolean firstRun;
	SharedPreferences sharedPreferences;
	
	public static void setIBRInteraction(IBRInteraction brInteraction){
		TaskWorker.brInteraction = brInteraction;
	}

	public void onReceive(Context context, Intent intent) {
		mContext = context;
		sharedPreferences = mContext.getSharedPreferences("CokAssistant", Context.MODE_PRIVATE); //私有数据
		firstRun = sharedPreferences.getBoolean("firstRun", true);
		execute();
	}

	private void execute(){
		runningTimes++;
		try {
			
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			if(!firstRun){
				Editor editor = sharedPreferences.edit();//获取编辑器
				editor.putBoolean("firstRun", false);
				editor.commit();
				os.writeBytes("input keyevent 26\n"); 
				os.flush();
				Thread.sleep(2000);
			}else{
				Toast.makeText(mContext, "任务开始执行", 0).show();
			}

			os.writeBytes("input tap 1075 610\n"); 
			os.flush();
			Thread.sleep(1000);
			os.writeBytes("input tap 415 610\n"); 
			os.flush();
			os.close();
			if(TaskWorker.brInteraction!=null)
				TaskWorker.brInteraction.sendMsg(runningTimes+".   任务已完成");
		} catch (InterruptedException e) {
			TaskWorker.brInteraction.sendMsg(e.toString());
			if(TaskWorker.brInteraction!=null)
				TaskWorker.brInteraction.sendMsg(runningTimes+".   任务失败");
		} catch (IOException e) {
			TaskWorker.brInteraction.sendMsg(e.toString());
		}
		
	}

}