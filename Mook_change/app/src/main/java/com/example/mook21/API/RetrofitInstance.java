package com.example.mook21.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static final String BASE_URL = "https://sleepsounds.nyc3.cdn.digitaloceanspaces.com/";
    private static Retrofit retrofit;

    public static SoundApi getApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(SoundApi.class);
    }
}
