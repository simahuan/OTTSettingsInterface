package com.pisen.ott.widget;

import com.pisen.ott.settings.R;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * 包裹scrollview 和 listview 滚动后可以显示渐变的header和footer
 * 
 * @author hegang
 * @version 1.0, 2015年03月27日 下午2:38:55
 */
public class FaddingFrameLayout extends FrameLayout {

	private boolean scrolling = false;
	private View scrollView = null;
	private ImageView header, footer;
	private Rect r = new Rect();
	private Context mContext;
	private Rect focusRect = new Rect();
	private Rect compareRect = new Rect();

	public FaddingFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		post(new Runnable() {

			@Override
			public void run() {
				scrolling = scrolling();
				getGlobalVisibleRect(r);
				if (r.isEmpty()) {
					postDelayed(this, 100);
				} else {
				}
				resetScrollState();
			}
		});
	}

	private boolean scrolling() {
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i) instanceof ScrollView || getChildAt(i) instanceof ListView) {
				scrollView = getChildAt(i);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		boolean ret = super.dispatchKeyEvent(event);
		if (event.getAction() == KeyEvent.ACTION_UP) {
			resetScrollState();
		}

		return ret;
	}

	private void resetScrollState() {
		if (scrollView == null)
			return;
		View focusedItem = null;
		View v = findFocus();
		if (v instanceof ListView) {
			focusedItem = ((ListView) v).getSelectedView();
		} else {
			focusedItem = v;
		}

		boolean showFooter = true;
		boolean showHeader = true;
		if (focusedItem != null) {
			focusedItem.getGlobalVisibleRect(focusRect);
			if (footer != null) {
				footer.getGlobalVisibleRect(compareRect);
				if (focusRect.intersect(compareRect)) {
					showFooter = false;
				}
			}
			if (header != null) {
				header.getGlobalVisibleRect(compareRect);
				if (focusRect.intersect(compareRect)) {
					showHeader = false;
				}
			}
		}

		if (scrollView.canScrollVertically(1) && showFooter) {
			showFooter();
		} else {
			dismissFooter();
		}
		if (scrollView.canScrollVertically(-1) && showHeader) {
			showHeader();
		} else {
			dismissHeader();
		}

	}

	private void showFooter() {
		if (footer == null) {
			footer = new ImageView(mContext);
			footer.setBackgroundResource(R.drawable.faddingfooter);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.BOTTOM;
			this.addView(footer, lp);
		}
		footer.setVisibility(View.VISIBLE);

	}

	private void dismissFooter() {
		if (footer != null) {
			footer.setVisibility(View.GONE);
		}
	}

	private void showHeader() {
		if (header == null) {
			header = new ImageView(mContext);
			header.setBackgroundResource(R.drawable.faddingheader);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.TOP;
			this.addView(header, lp);
		}
		header.setVisibility(View.VISIBLE);

	}

	private void dismissHeader() {
		if (header != null) {
			header.setVisibility(View.GONE);
		}
	}

}
