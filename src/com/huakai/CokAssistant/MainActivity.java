package com.huakai.CokAssistant;


import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements IBRInteraction{
	private TaskHelperUtil tMgr;
	private boolean isRuning;
	private int period = 15;
	private SeekBar seekBar;
	private TextView seekBarProgress;
	private TextView msgShow;
	private Button bStart;
	private Button bStop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_main);

		msgShow = (TextView)findViewById(R.id.msgShow);

		bStart = (Button)findViewById(R.id.lButton);
		bStop = (Button)findViewById(R.id.rButton);
		bStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isRuning = true;
				msgShow.setText("                **********任务开始**********");
				if(exeCyjh())
					PlanManagerUtil.sendUpdateBroadcast(getBaseContext(), MainActivity.this, period*60000L);
			}
		});

		bStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isRuning){
					stopTask();
					msgShow.setText("");
				}
			}
		});

		seekBar = (SeekBar)findViewById(R.id.seekBar);
		seekBarProgress = (TextView)findViewById(R.id.seekBarProgress);
		seekBar.setMax(100);
		seekBar.setProgress(period);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar arg0,int progress,boolean fromUser) {
				seekBarProgress.setText(progress+"");
				period = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		tMgr = TaskHelperUtil.getInstance(getApplicationContext());
		tMgr.initData();
		intView();
	}

	private void intView() {  
		ArrayList<File> files = tMgr.getFolds();
		for(int i=0; i<files.size();i++){
			if(!files.get(i).exists()){
				tMgr.cFileIndex = i;
				break;
			}
		} 
		startService(new Intent(this,BackService.class));
	}

	private boolean exeCyjh(){
		try {
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("am start -n com.cyjh.elfin/com.cyjh.elfin.activity.MainActivity\n"); 
			os.flush();
			Thread.sleep(2000);
			os.writeBytes("input tap 540 1820\n"); 
			os.flush();
			os.close();
			return true;
		}catch (InterruptedException e) {
			e.printStackTrace();
			sendMsg("启动脚本精灵失败.");
		}catch (IOException e) {
			e.printStackTrace();
			sendMsg("启动脚本精灵失败.");
		} 
		return false;
	}

	private void stopTask(){
		TaskHelperUtil.killApplication("com.cyjh.elfin");
		PlanManagerUtil.cancelUpdateBroadcast(getBaseContext());
	}
	
	@Override
	public void sendMsg(String msg){
		String t = new SimpleDateFormat("MM-dd HH:mm:ss").format(new Date());
		msg = msgShow.getText()+"\n"+msg+"  "+t;
		msgShow.setText(msg);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) { 
			exitBy2Click(); 
		}
		return false;
	}

	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; 
			Toast.makeText(this, "再按一次退出", 0).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false;  
				}
			}, 1000); 
		} else {
			TableView.Close();
			MainActivity.this.stopService(new Intent(MainActivity.this,BackService.class));
			if(isRuning){
				stopTask();
			}
			finish();
			System.exit(0);
		}
	}

}
