package com.leaf.flashlightcontrol;
/*
 * huangjun,code. AT 2015/11/19
 * target:test mtk-platform flashlight func
 * license :GPL
 * 
 * */
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class FlashLightAction {
	private native int fltNativeInit();

	private native void fltNativeDeInit();

	private native int fltNativeSelectDevice(int deviceid, int ledid);

	private native int fltNativeSetDuty(int duty);

	private native int fltNativeOnOff(int onoff);

	private native int fltNativeTimeout(int timeout);

	private int deviceId, ledId, duty1, duty2, onOff = 0, timeOut, delay;
	private final String TAG = "FLLTA";
	private static FlashLightAction FLA = null;

	private int flashMode = 0;

	static {
		System.loadLibrary("flashlight_jni");
	}

	private FlashLightAction() {
		if (fltNativeInit() != 0){
			Log.e(TAG, "fltNativeInit return FAILn\n");
		}
		if (!workThread.isAlive())
			workThread.start();
	}

	protected void finalize() {
		fltNativeDeInit();
	}

	public static FlashLightAction getInstance() {
		if (FLA == null)
			FLA = new FlashLightAction();
		return FLA;
	}

	private int controlFlashLight(boolean enable) {
		int ret = 0, ret2 = 0;
		if (enable) {
			if ((flashMode & 0x01) > 0) {
				ret = fltNativeSelectDevice(deviceId, 1)
				+ fltNativeSetDuty(duty1)
				+ fltNativeTimeout(timeOut);
				ret2 += fltNativeOnOff(1);
			}
			if ((flashMode & 0x02) > 0) {
				ret = fltNativeSelectDevice(deviceId, 2)
				+ fltNativeSetDuty(duty2)
				+ fltNativeTimeout(timeOut);
				ret2 += fltNativeOnOff(1);
			}
			// Log.d(TAG, "ret=" + ret + " ret2="+ret2);
		} else {
			if ((flashMode & 0x01) > 0) {
				fltNativeSelectDevice(deviceId, 1);
				ret2 = fltNativeOnOff(0);
			}
			if ((flashMode & 0x02) > 0) {
				fltNativeSelectDevice(deviceId, 2);
				ret2 += fltNativeOnOff(0);
			}
		}
		sendReturn(ret+ret2,"豁我不懂水？\n敢关掉安全内核且修改kd_cmaera_flashlight权限后再试吗？");
		return ret + ret2;
	}

	private void sendReturn(int ret,String value){
		if(ret != 0){
			Log.d(TAG,"founsome error\n");
			Message msg = Message.obtain(UIUpdateHandler);
			Bundle data = new Bundle();
			data.putString("RET", "ret="+ret+"\n"+value);
			msg.setData(data);
			msg.sendToTarget();
		}
	}
	private Integer workLock = 0;
	private boolean wtPause = true;

	private Thread workThread = new Thread(new Runnable() {
		boolean onoff = true;

		@Override
		public void run() {
			while (true) {
				synchronized (workLock) {
					switch (flashMode & 0x04) {
					case 0x04:
						if (onOff != 0) {
							if(controlFlashLight(onoff)!=0)
								wtPause = true;
							onoff = !onoff;
							try {
								Thread.sleep(Long.valueOf(delay));
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}else{
							Log.d(TAG,"wtPause = true flashMode=0x04");
							wtPause = true;
						}
						break;
					default:
						if(flashMode != 0)
							controlFlashLight(onOff != 0);
						wtPause = true;
						Log.d(TAG,"wtPause = true flashMode="+flashMode);
						break;
					}
					if (wtPause) {
						try {
							Log.d(TAG,"wtPasue");
							workLock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	});

	// tmp
	private void setParams(int _flashMode, int _deviceId, int _duty1,
			int _duty2, int _onOff, int _timeOut, int _delay) {
		flashMode = _flashMode;
		deviceId = _deviceId;
		duty1 = _duty1;
		duty2 = _duty2;
		onOff = _onOff;
		timeOut = _timeOut;
		delay = _delay;
		Log.d(TAG, "duty1=" + duty1 + " duty2=" + duty2 + " devId=" + deviceId
				+ " timeout=" + timeOut + " flashMode="+flashMode +" delay="+delay);
	}
	@SuppressLint("HandlerLeak")
	class FLHandler extends Handler {
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			flashMode = data.getInt("FM", 1);
			deviceId = data.getInt("DEVID", 1);
			duty1 = data.getInt("DUTY1", 0);
			duty2 = data.getInt("DUTY2", 0);
			onOff = data.getInt("ONOFF", 0);
			timeOut = data.getInt("TIMEOUT", 0);
			delay = data.getInt("DELAY",256);
			Log.d(TAG,"flahMode="+flashMode+" onOff="+onOff);
			synchronized (workLock) {
				if(wtPause){
					wtPause = false;
					workLock.notifyAll();
				}
			}
			super.handleMessage(msg);
		}
	}

	private FLHandler flHandler = new FLHandler();
	private Handler UIUpdateHandler;
	public Handler getHandle() {
		return flHandler;
	}

	void setReturnFunc(Handler hdl){
		UIUpdateHandler = hdl;
	}
}
