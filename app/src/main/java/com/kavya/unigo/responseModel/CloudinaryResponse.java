package com.kavya.unigo.responseModel;

import com.google.gson.annotations.SerializedName;

public class CloudinaryResponse {
    @SerializedName("secure_url")
    public String secureUrl;

    @SerializedName("public_id")
    public String publicId;
}