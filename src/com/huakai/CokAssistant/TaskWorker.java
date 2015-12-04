package com.huakai.CokAssistant;

import java.io.DataOutputStream;
import java.io.IOException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TaskWorker extends BroadcastReceiver{

	boolean firstRun = true;
	int runningTimes;
	static IBRInteraction brInteraction;
	Context mContext;
	
	
	public static void setIBRInteraction(IBRInteraction brInteraction){
		TaskWorker.brInteraction = brInteraction;
	}

	public void onReceive(Context context, Intent intent) {
		mContext = context;
		execute();
	}

	private void execute(){
		runningTimes++;
		try {
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			if(!firstRun){
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
			e.printStackTrace();
			if(TaskWorker.brInteraction!=null)
				TaskWorker.brInteraction.sendMsg(runningTimes+".   任务失败");
		} catch (IOException e) {
			e.printStackTrace();
		}
		firstRun = false;
	}

}