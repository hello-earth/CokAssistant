package com.huakai.CokAssistant;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

public class TableView extends View {
	private Context	mContext;
	private static WindowManager mWManager;
	private WindowManager.LayoutParams mWMParams;
	private static View mTableTip;
	private PopupWindow mPopuWin;
	private ServiceListener mSerLisrener;
	private View mShowView;
	private int mTag = 0;
	private int midX;
	private int midY;
	private int mOldOffsetX;
	private int mOldOffsetY;
	private TaskHelperUtil tMgr;

	public TableView(Context context,ServiceListener listener) {
		super(context);
		tMgr = TaskHelperUtil.getInstance(context);
		mContext = context;
		mSerLisrener = listener;
	}

	public void fun() {
		mWManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		midX = mWManager.getDefaultDisplay().getWidth()/2-25;
		midY = mWManager.getDefaultDisplay().getHeight()/2-44;
		mTableTip = LayoutInflater.from(mContext).inflate(R.layout.ctrl_window, null);
		mTableTip.setBackgroundColor(Color.TRANSPARENT);

		mTableTip.setOnTouchListener(mTouchListener);
		WindowManager wm = mWManager;
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
		mWMParams = wmParams;
		wmParams.type = 2003;
		wmParams.flags = 40;
		wmParams.width = 120;
		wmParams.height = 120;
		wmParams.format = -3;
		wm.addView(mTableTip, wmParams);
		mWMParams.x = mWManager.getDefaultDisplay().getWidth()-10;
		mWMParams.y = mWManager.getDefaultDisplay().getHeight()-100;
		mWManager.updateViewLayout(mTableTip, mWMParams);

		mShowView = LayoutInflater.from(mContext).inflate(R.layout.main, null);
		ImageView button1 = (ImageView) mShowView.findViewById(R.id.next);
		//ImageView button2 = (ImageView) mShowView.findViewById(R.id.home);
		ImageView button3 = (ImageView) mShowView.findViewById(R.id.pre);
		button1.setOnClickListener(mClickListener);
		//button2.setOnClickListener(mClickListener);
		button3.setOnClickListener(mClickListener);
	}

	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position=-1;
			switch(v.getId()){
			case R.id.next:
				disPopu();
				position=0;
				break;
			case R.id.pre:
				disPopu();
				position=1;
				break;
			}
			tMgr.switchGameAccount(position);
		}
	};
	
	private OnTouchListener mTouchListener = new OnTouchListener() {
		float	lastX, lastY;

		public boolean onTouch(View v, MotionEvent event) {
			final int action = event.getAction();

			float x = event.getX();
			float y = event.getY();

			if(mTag == 0){
				mOldOffsetX= mWMParams.x;
				mOldOffsetY = mWMParams.y;
			}


			if (action == MotionEvent.ACTION_DOWN) {
				lastX = x;
				lastY = y;

			}
			else if (action == MotionEvent.ACTION_MOVE) {
				int dx = (int) (x - lastX);
				int dy = (int) (y - lastY);
				if(Math.abs(dx)>10 || Math.abs(dy)>10){
					mWMParams.x += dx;
					mWMParams.y += dy;
					mTag = 1;
					mWManager.updateViewLayout(mTableTip, mWMParams);
				}
			}

			else if (action ==  MotionEvent.ACTION_UP){
				int newOffsetX = mWMParams.x;
				int newOffsetY = mWMParams.y;					
				if(mOldOffsetX == newOffsetX && mOldOffsetY == newOffsetY){
					mPopuWin = new PopupWindow(mShowView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
					mPopuWin.setTouchInterceptor(new OnTouchListener() {

						public boolean onTouch(View v, MotionEvent event) {
							if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
								disPopu();
								return true;
							}
							mTableTip.findViewById(R.id.iAction).setBackgroundResource(R.drawable.set);
							return false;
						}
					});
					mPopuWin.setBackgroundDrawable(new BitmapDrawable());
					mPopuWin.setTouchable(true);
					mPopuWin.setFocusable(true);
					mPopuWin.setOutsideTouchable(true);
					mPopuWin.setContentView(mShowView);
					if(Math.abs(mOldOffsetX)>midX){
						if(mOldOffsetX>0){
							mOldOffsetX = midX;
						}else{
							mOldOffsetX = -midX;
						}
					}
					if(Math.abs(mOldOffsetY)>midY){
						if(mOldOffsetY>0){
							mOldOffsetY = midY;
						}else{
							mOldOffsetY = -midY;
						}
					}
					mPopuWin.setAnimationStyle(R.style.AnimationPreview);  
					mPopuWin.setFocusable(true);  
					mPopuWin.update();
					mTableTip.findViewById(R.id.iAction).setBackground(new BitmapDrawable());
					mPopuWin.showAtLocation(mTableTip, Gravity.CENTER, 0, 0);
				}else {
					mTag = 0;
				}
			}
			return true;
		}
	};

	private void disPopu(){
		if(null!=mPopuWin){
			mPopuWin.dismiss();
		}
	}

	public static void Close(){
		mWManager.removeView(mTableTip);
	}
	
	public interface ServiceListener{
		public void OnCloseService(boolean isClose);
	}
}
