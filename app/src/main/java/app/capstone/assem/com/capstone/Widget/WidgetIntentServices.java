package app.capstone.assem.com.capstone.Widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.util.Log;

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
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
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
}
