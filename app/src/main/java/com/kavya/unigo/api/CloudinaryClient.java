package com.kavya.unigo.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CloudinaryClient {
    private static final String BASE_URL = "https://api.cloudinary.com/";

    public static CloudinaryApi getApi() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CloudinaryApi.class);
    }
}
