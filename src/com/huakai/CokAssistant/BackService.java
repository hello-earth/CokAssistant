package com.huakai.CokAssistant;


import com.huakai.CokAssistant.TableView.ServiceListener;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackService extends Service implements ServiceListener{  
    private Intent mIntent;
    
    @Override  
    public IBinder onBind(Intent intent) {  
        // TODO Auto-generated method stub  
        return null;  
    }  
  
    public void onCreate() {  
        super.onCreate();  
        new TableView(this,this).fun();  
    }  
  
  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
    	mIntent = intent;
        return super.onStartCommand(intent, flags, startId);  
    }

	@Override
	public void OnCloseService(boolean isClose) {
		stopService(mIntent);
	}  
}  