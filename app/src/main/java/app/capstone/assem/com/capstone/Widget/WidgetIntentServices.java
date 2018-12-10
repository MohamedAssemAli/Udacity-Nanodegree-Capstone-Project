package app.capstone.assem.com.capstone.Widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import app.capstone.assem.com.capstone.Models.BookmarkModel;
import app.capstone.assem.com.capstone.R;

public class WidgetIntentServices extends IntentService {

    private final String TAG = WidgetIntentServices.class.getSimpleName();

    public WidgetIntentServices() {
        super("WidgetIntentServices");
    }

    // Invoked when this intent service is started.
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d(TAG, "MyIntentService onHandleIntent() method is invoked.");
            updateView();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MyIntentService onCreate() method is invoked.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MyIntentService onDestroy() method is invoked.");
    }

    //sample function to update view
    private void updateView() {
        // TODO: Handle action Baz
        Log.d("TAG", "updating widget from intent service");
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.bookmark_widget);
        // getting recipe from sharedPref
        BookmarkModel bookmarkModel = new WidgetHelper(this).getWidgetBookmark();
        // Populate widget content
        remoteViews.setTextViewText(R.id.bookmark_widget_city_name, bookmarkModel.getName());
        remoteViews.setTextViewText(R.id.bookmark_widget_city_weather, bookmarkModel.getWeather().get(0).getDescription());
        remoteViews.setTextViewText(R.id.bookmark_widget_temperature, String.valueOf(bookmarkModel.getMain().getTemp()));
        remoteViews.setTextViewText(R.id.bookmark_widget_humidity, String.valueOf(bookmarkModel.getMain().getHumidity()));
        remoteViews.setTextViewText(R.id.bookmark_widget_wind, String.valueOf(bookmarkModel.getWind().getSpeed()));
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds =
                appWidgetManager.getAppWidgetIds(new ComponentName(this, BookmarksWidgetProvider.class));
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }
}
