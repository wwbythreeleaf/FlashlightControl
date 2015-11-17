package com.leaf.flashlightcontrol;
/*
 * huangjun,code. AT 2015/11/12
 * target:test mtk-platform flashlight func
 * license :GPL
 * 
 * */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.ToggleButton;

public class FlashLightControlActivity extends Activity {

	public static  native int fltNativeInit();
	public static  native void fltNativeDeInit();
	public static  native int fltNativeSelectDevice(int deviceid,int ledid);
	public static  native int fltNativeSetDuty(int duty);
	public static  native int fltNativeOnOff(int onoff);
	public static  native int fltNativeTimeout(int timeout);
	
	private CheckBox cb; 
	private Spinner spinner;
	private EditText etDuty;
	private EditText etDuty2;
	private EditText etTimeout;
	private ToggleButton tb;
	private RadioGroup rg;
	private EditText etDelay;	
	private TableRow tr;
	
	private Thread thread;
	private Integer	runstatus = 0;
	
	private final String mainCameraDev="1";
	private final String subCameraDev="2";
	private boolean isDubleFl=false;
	private int flashMode = 1;
	
	private List<String> list = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private String deviceId = "0";
	private String duty[] = {"0","0","0"};
	private String dutyT = "0";
	private String timeOut = "500";
	private String delay = "";
	private boolean onOff = false;
	
	private final String TAG = "FLLT";
	
	static
	{
		System.loadLibrary("flashlight_jni");
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_light_control);
        list.add(" 主闪");
        list.add(" 前闪");
        spinner = (Spinner) findViewById(R.id.spinner_device_id);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				deviceId = subCameraDev;
				if(adapter.getItem(arg2).equals(" 主闪"))
					deviceId = mainCameraDev;
				if(fltNativeSelectDevice(Integer.valueOf("0"+deviceId),1) != 0){
					new AlertDialog.Builder(FlashLightControlActivity.this).setTitle("提示")
						.setMessage(adapter.getItem(arg2)+"不可用！")
						.create()
						.show();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
        	
        });
        tr = (TableRow)findViewById(R.id.tableRow4);
        tr.setVisibility(View.GONE);
        rg = (RadioGroup)findViewById(R.id.radioGroup1);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.radioButtonOne:
					flashMode = 1;
					break;
				case R.id.radioButtonTwo:
					flashMode = 2;
					break;
				case R.id.radioButtonDouble:
					flashMode = 3;
					break;
				}
			}
		});
        //TextDrawable d = new TextDrawable(this);
        //d.setText("闪1");
        etDuty = (EditText)findViewById(R.id.et_fllt_duty);
        //etDuty.setBackgroundDrawable(d);
        //etDuty.setText(duty[1]);
        etDuty.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				getDuty(1);
				return false;
			}
		});
        etDuty.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				duty[1] = etDuty.getText().toString();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(duty[1].equals("")){
					duty[1] = "0";
					//etDuty.setText(duty[1]);
				}
			}
		});
        etDuty2 = (EditText)findViewById(R.id.et_fllt_duty2);
        etDuty2.setEnabled(false);
        //etDuty2.setText("0");
        //TextDrawable d1 = new TextDrawable(this);
        //d1.setText("闪2");
        //etDuty2.setBackgroundDrawable(d1);
        etDuty2.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				getDuty(2);
				return false;
			}
		});
        etDuty2.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				duty[2] = etDuty2.getText().toString();
			}

			@Override
			public void afterTextChanged(Editable s) {
				if(duty[2].equals("")){
					duty[2] = "0";
					//etDuty2.setText(duty[2]);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
		});

        etTimeout = (EditText)findViewById(R.id.et_timeout);
        etTimeout.setText(timeOut);
        etTimeout.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				timeOut = etTimeout.getText().toString();
				if(timeOut.equals("")){
					timeOut = "0";
					//etTimeout.setText(timeOut);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
        cb = (CheckBox)findViewById(R.id.cb_dubule_fl);
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isDubleFl = isChecked;
				etDuty2.setEnabled(isChecked);
				if(isChecked){
					tr.setVisibility(View.VISIBLE);
				}else{
					flashMode = 1;
					RadioButton rb = (RadioButton)findViewById(R.id.radioButtonOne);
					rb.setChecked(true);
					tr.setVisibility(View.GONE);
				}
			}
		});
        tb = (ToggleButton)findViewById(R.id.toggleButton1);
        tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				onOff = isChecked;
				if(etDuty.getText().toString().equals(""))
					etDuty.setText(duty[1]);
				if(etDuty2.getText().toString().equals(""))
					etDuty2.setText(duty[2]);
				if(etTimeout.getText().toString().equals(""))
					etTimeout.setText(timeOut);
				if(etDelay.getText().toString().equals("")){
					int ret;
					ret = controlFlashLight(Integer.valueOf(deviceId), Integer.valueOf(duty[1]), Integer.valueOf(duty[2]), Integer.valueOf(timeOut), onOff);
					if(ret != 0)
						Toast.makeText(FlashLightControlActivity.this,"ret="+ ret + "\n尝试adb shell chmod 777 /dev/kd_camera_flashlight?",Toast.LENGTH_SHORT).show();
				}else{
					delay = etDelay.getText().toString();
					synchronized (runstatus) {
						runstatus.notify();
					}
				}
			}
		});
        
        etDelay = (EditText)findViewById(R.id.et_delay);
        if(fltNativeInit() != 0)
        	Log.e(TAG, "fltNativeInit return FAILn\n");
        thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean onoff = true;
				while(true){
					synchronized (runstatus) {
						if(onOff){
							controlFlashLight(Integer.valueOf(deviceId), Integer.valueOf(duty[1]), Integer.valueOf(duty[2]), Integer.valueOf(timeOut), onoff);
							onoff = !onoff;
							try {
								Thread.sleep(Long.valueOf(delay));
							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}else{
							try {
								Log.d(TAG,"before wait");
								runstatus.wait();
							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		});
        thread.start();
    }
    
    private void getDuty(int whichDuty){
    	String data[] = {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"};
    	duty[0] = ""+whichDuty;
		new AlertDialog.Builder(FlashLightControlActivity.this)
			.setTitle("选择Duty"+whichDuty)
			.setIcon(R.drawable.ic_action_search)
			.setItems(data,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
						String wch = duty[0];
						duty[0] = ""+which;
						if(wch.equals("1")){
							etDuty.setText(duty[0]);
						}else
							etDuty2.setText(duty[0]);
				}
		})
		.create().show();
    }
    private int controlFlashLight(int devId,int duty,int duty2,int timeout,boolean enable){
    	int ret = 0,ret2 = 0;
    	 Log.d(TAG,"duty1="+duty+" duty2="+duty2);
		if(enable){
			if((flashMode & 0x01) > 0){
		        ret = fltNativeSelectDevice(Integer.valueOf(devId),1) + fltNativeSetDuty(Integer.valueOf(duty))
		        		+ fltNativeTimeout(timeout);
		        ret2 = fltNativeOnOff(1);
			}
			if((flashMode & 0x02) > 0){
	        	ret = fltNativeSelectDevice(Integer.valueOf(devId),2) + fltNativeSetDuty(Integer.valueOf(duty2))
	        		+ fltNativeTimeout(timeout);
	        	ret2 = fltNativeOnOff(1);
	        }
	        Log.d(TAG, "ret=" + ret + " ret2="+ret2);
		}else{
			if((flashMode & 0x01) > 0){
				fltNativeSelectDevice(Integer.valueOf(devId),1);
				ret2 = fltNativeOnOff(0);
			}
			if((flashMode & 0x02) > 0){
				fltNativeSelectDevice(Integer.valueOf(devId),2);
				ret2 = fltNativeOnOff(0);
			}
		}
    	return ret + ret2;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_flash_light_control, menu);
        return true;
    }
    
    @Override  
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
	        case R.id.menu_settings:
	        	finish();
	        	break;
	        case R.id.menu_about:
	        	new AlertDialog.Builder(this).setTitle("关于")
	        			.setIcon(R.drawable.ic_action_search)
	        			.setMessage("闪光灯测试程序，请注意此程序只保证控制到MTK平台设备，不保证安全性。\nV0.01\nhuangjun @2015.11.10")
	        			.create()
	        			.show();
	        	break;
        }
        return super.onOptionsItemSelected(item);  
    }
    public void onStop(){
    	thread.stop();
    }
    public void onRestart(){
    	thread.start();
    }
}
