package kr.co.wikibook.wis;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WeatherAppWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		
	}
	
	@Override
	public void onEnabled(Context context) {
		
	}
	
	@Override
	public void onDisabled(Context context) {
		
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(WeatherService.WEATHER_INFORMATION_WIDGET_UPDATE_EVENT)) {
			String area = intent.getStringExtra(WeatherService.AREA);
			String date = intent.getStringExtra(WeatherService.DATES);
			int rId = intent.getIntExtra(WeatherService.RESOURCE_IDS, 0);
			
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_appwidget);
			
			views.setTextViewText(R.id.area, area);
			views.setTextViewText(R.id.split, "/");
			views.setTextViewText(R.id.date, date);
			views.setImageViewResource(R.id.weather_icon, rId);
			
			ComponentName cn = new ComponentName(context, WeatherAppWidgetProvider.class);
			AppWidgetManager.getInstance(context).updateAppWidget(cn, views);
		} else {
			super.onReceive(context, intent);
		}
	}
}
