package com.huakai.CokAssistant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;


public class PlanManagerUtil {
	
	private static AlarmManager getAlarmManager(Context ctx){
		return (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
	}

	public static void sendUpdateBroadcast(Context ctx,IBRInteraction brInteraction, long period){
		TaskWorker.setIBRInteraction(brInteraction);
		AlarmManager am = getAlarmManager(ctx);
	    Intent i = new Intent(ctx, TaskWorker.class); 
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
	    long firstime=SystemClock.elapsedRealtime()+3000;
	    am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime, period, pendingIntent);
	}

	public static void cancelUpdateBroadcast(Context ctx){
	    AlarmManager am = getAlarmManager(ctx);
	    Intent i = new Intent(ctx, TaskWorker.class);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
	    am.cancel(pendingIntent);
	}
}