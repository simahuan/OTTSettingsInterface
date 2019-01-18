/**
 * 2014年12月30日mahuan下午3:20:10
 */
package com.pisen.ott.settings.about;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author  mahuan
 * @date    2014年12月30日 下午3:20:10
 */
public class DashedLine extends View {

	public DashedLine(Context context,AttributeSet attrs) {
		super(context,attrs);
	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.DKGRAY);
		Path path = new Path();
		path.moveTo(0, 10);
		path.lineTo(480, 10);
		PathEffect effects = new DashPathEffect(new float[]{5,5,5,5}, 1);
		paint.setPathEffect(effects);
		canvas.drawPath(path, paint);
	}
}
