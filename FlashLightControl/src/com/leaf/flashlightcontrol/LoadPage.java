/**
 * 
 */
package com.leaf.flashlightcontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * @author leaf
 *
 */
public class LoadPage extends Activity implements OnClickListener{
	private GestureDetector gestureDetector;
	Button got;
	public void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
        setContentView(R.layout.load_page);
        got = (Button)findViewById(R.id.got);
        got.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoadPage.this, FlashLightControlActivity.class));
				finish();
			}
		});
        gestureDetector = new GestureDetector(LoadPage.this,new GestureDetector.OnGestureListener (){

			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				startActivity(new Intent(LoadPage.this, FlashLightControlActivity.class));
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
        	
        });
        
	 }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
//		startActivity(new Intent(LoadPage.this, FlashLightControlActivity.class));
	}
//	public boolean  dispatchTouchEvent(MotionEvent ev) {
//		return gestureDetector.onTouchEvent(ev);
//	}
}
