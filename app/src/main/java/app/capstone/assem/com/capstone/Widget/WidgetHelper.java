package app.capstone.assem.com.capstone.Widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import app.capstone.assem.com.capstone.Models.BookmarkModel;

public class WidgetHelper {

    // LogCat tag
    private static String TAG = WidgetHelper.class.getSimpleName();
    // Shared Preferences
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    // Shared preferences file name
    private static final String PREF_NAME = "CapstoneAppWidget";
    private static final String KEY_WIDGET = "widget";
    // Widget Token
    private Gson gson;

    @SuppressLint("CommitPrefEdits")
    public WidgetHelper(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, 0);
        editor = pref.edit();
        gson = new Gson();
    }

    public void setWidgetBookmark(BookmarkModel bookmarkModel) {
        String jsonObj = gson.toJson(bookmarkModel);
        editor.putString(KEY_WIDGET, jsonObj);
        editor.commit();
        // commit changes
        editor.commit();
        Log.d(TAG, "recipeModel added successfully!");
    }

    public BookmarkModel getWidgetBookmark() {
        String json = pref.getString(KEY_WIDGET, "No items added");
        return gson.fromJson(json, BookmarkModel.class);
    }
}
