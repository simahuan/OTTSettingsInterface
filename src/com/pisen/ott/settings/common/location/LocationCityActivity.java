package com.pisen.ott.settings.common.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.R;
import com.pisen.ott.settings.common.location.widget.ArrayWheelAdapter;
import com.pisen.ott.settings.common.location.widget.OnWheelChangedListener;
import com.pisen.ott.settings.common.location.widget.WheelView;
import com.pisen.ott.widget.FocusLinearLayout;

import android.izy.util.StringUtils;
import android.os.Bundle;
import android.view.KeyEvent;

/**
 * 城市设置
 * @author Liuhc
 * @version 1.0 2014年12月5日 上午11:26:00
 */
public class LocationCityActivity extends OTTBaseActivity implements OnWheelChangedListener {

	/** 省的WheelView控件 */
	private WheelView mProvince;
	/*** 市的WheelView控件 */
	private WheelView mCity;
	/*** 区的WheelView控件 */
	private WheelView mArea;
	
	private FocusLinearLayout fll_locations;

	private Map<String, CityInfo> mProviceList = new HashMap<String, CityInfo>();
	private Map<String, CityInfo> mCityList = new HashMap<String, CityInfo>();
	private Map<String, CityInfo> mAreaList = new HashMap<String, CityInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_location_city);
		setTitle(R.string.common_system_location, R.string.common_system_location_describle);
		this.initViews();
		fll_locations = (FocusLinearLayout) findViewById(R.id.fll_locations);
		if (mProvince.getVisibleItems() != mCity.getVisibleItems() || mCity.getVisibleItems() != mArea.getVisibleItems()
				|| mProvince.getVisibleItems() != mArea.getVisibleItems()) {
			throw new IllegalStateException(" visibleItems doesn't matched");//
		}
		int scaleY = mProvince.getVisibleItems();
		fll_locations.setScaleXY(1, scaleY);
	}

	private void initViews() {
		mProvince = (WheelView) findViewById(R.id.id_province);
		mCity = (WheelView) findViewById(R.id.id_city);
		mArea = (WheelView) findViewById(R.id.id_area);
		mProvince.requestFocus();
		mProviceList = CityManager.getInstance(this).getProvince();
		mProvince.setViewAdapter(new ArrayWheelAdapter(this, new ArrayList(mProviceList.keySet())));

		// 添加change事件
		mProvince.addChangingListener(this);
		mCity.addChangingListener(this);
		mArea.addChangingListener(this);
		updateCities();
	}
	

	@Override
	protected void onPause() {
		saveChoosedCity();
		super.onPause();
	}


	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		String currentProviceName = mProvince.getCurrentText().toString();
		if (!StringUtils.isEmpty(currentProviceName)) {
			CityInfo info = mProviceList.get(currentProviceName);
			mCityList.clear();
			mCityList = CityManager.getInstance(this).getCity(info.city_id);
			mCity.setViewAdapter(new ArrayWheelAdapter(this, new ArrayList(mCityList.keySet())));
			mCity.setCurrentItem(0);
			updateAreas();
		}
	}

	
	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		String currentCityName = mCity.getCurrentText().toString();
		if (!StringUtils.isEmpty(currentCityName)) {
			CityInfo info = mCityList.get(currentCityName);
			mAreaList.clear();
			mAreaList = CityManager.getInstance(this).getArea(info.city_id);
			mArea.setViewAdapter(new ArrayWheelAdapter(this, new ArrayList(mAreaList.keySet())));
			mArea.setCurrentItem(0);
		}
	}
	

	/**
	 * change事件的处理
	 */
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (wheel == mProvince) {
			updateCities();
		} else if (wheel == mCity) {
			updateAreas();
		} 
	}

	/**
	 * 保存当前选择的城市信息
	 */
	private void saveChoosedCity(){
		String currentAreaName = mArea.getCurrentText().toString();
		if (!StringUtils.isEmpty(currentAreaName)) {
			CityInfo info = mAreaList.get(currentAreaName);
			if (info != null) {
				CityManager.getInstance(this).setDefaultCityName(currentAreaName);
				CityManager.getInstance(this).setDefaultCityAreaid(info.weather_id);
			}
		}
	}
}
