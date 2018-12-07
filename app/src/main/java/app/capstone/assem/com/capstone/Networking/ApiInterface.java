package app.capstone.assem.com.capstone.Networking;


import app.capstone.assem.com.capstone.App.AppConfig;
import app.capstone.assem.com.capstone.Models.BookmarkModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET(".")
    Call<BookmarkModel> getBookmarkData(@Query(AppConfig.LAT) String lat,
                                        @Query(AppConfig.LON) String lon,
                                        @Query(AppConfig.APP_ID) String appId,
                                        @Query(AppConfig.UNITS) String units);
}
