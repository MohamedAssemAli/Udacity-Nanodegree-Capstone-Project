package app.capstone.assem.com.capstone.Networking;


import android.content.Context;

import app.capstone.assem.com.capstone.App.AppConfig;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient = null;
    private static Cache cache = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null && okHttpClient == null && cache == null) {
            cache = new Cache(context.getCacheDir(), AppConfig.cacheSize);
            okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(AppConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}