package com.kavya.unigo.api;

import com.kavya.unigo.responseModel.CloudinaryResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

// Retrofit Instance...
// we're defining HTTP method, endpoint, request structure...
public interface CloudinaryApi {
    @Multipart
    @POST("v1_1/dsn07s2we/image/upload")
    Call<CloudinaryResponse> uploadImage(
            @Part MultipartBody.Part file,
            @Part("upload_preset") RequestBody preset,
            @Part("folder") RequestBody folder
    );
}
