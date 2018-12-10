package app.capstone.assem.com.capstone.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import app.capstone.assem.com.capstone.Activities.MainActivity;
import app.capstone.assem.com.capstone.Models.BookmarkModel;
import app.capstone.assem.com.capstone.R;

public class BookmarksWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.bookmark_widget);
            remoteViews.setOnClickPendingIntent(R.id.ingredient_widget_layout, pendingIntent);
            // getting recipe from sharedPref
            BookmarkModel bookmarkModel = new WidgetHelper(context).getWidgetBookmark();
            // Populate widget content
            remoteViews.setTextViewText(R.id.bookmark_widget_city_name, bookmarkModel.getName());
            remoteViews.setTextViewText(R.id.bookmark_widget_city_weather, bookmarkModel.getWeather().get(0).getDescription());
            remoteViews.setTextViewText(R.id.bookmark_widget_temperature, String.valueOf(bookmarkModel.getMain().getTemp()));
            remoteViews.setTextViewText(R.id.bookmark_widget_humidity, String.valueOf(bookmarkModel.getMain().getHumidity()));
            remoteViews.setTextViewText(R.id.bookmark_widget_wind, String.valueOf(bookmarkModel.getWind().getSpeed()));
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}
