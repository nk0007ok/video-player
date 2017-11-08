package com.nk.permissionrequire;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;

public class DetermineTextSize {

	public static int determineTextSize(Typeface font, float allowableHeight) {

		Paint p = new Paint();
		p.setTypeface(font);

		int size = (int) allowableHeight;
		p.setTextSize(size);

		float currentHeight = calculateHeight(p.getFontMetrics());

		while (size != 0 && (currentHeight) > allowableHeight) {
			p.setTextSize(size--);
			currentHeight = calculateHeight(p.getFontMetrics());
		}

		if (size == 0) {
			return (int) allowableHeight;
		}
		return size;
	}

	public static float calculateHeight(FontMetrics fm) {
		return fm.bottom - fm.top;
	}

}
