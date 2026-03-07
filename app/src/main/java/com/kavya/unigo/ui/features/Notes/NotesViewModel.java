package com.kavya.unigo.ui.features.Notes;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kavya.unigo.api.CloudinaryApi;
import com.kavya.unigo.api.CloudinaryClient;
import com.kavya.unigo.responseModel.CloudinaryResponse;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotesViewModel extends ViewModel {

    private final MutableLiveData<NotesState> uploadState = new MutableLiveData<>();

    public LiveData<NotesState> getUploadState() {
        return uploadState;
    }

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uid;

    public void uploadToCloudinary(Context context, Uri imageUri, String tit, String desc) {

        uploadState.setValue(new NotesState.Loading());

        if (auth.getCurrentUser() == null) {
            uploadState.setValue(new NotesState.Error("User not found"));
            return;
        }

        uid = auth.getCurrentUser().getUid();

        try {

            File file = FileUtils.createTempFileFromUri(context, imageUri);   // this creates the temp file of the image by converting it into bytes...

            // this wraps the file for HTTP uploads (converts into HTTP friendly format) ...
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/*"), file);

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            RequestBody preset =
                    RequestBody.create(MediaType.parse("text/plain"), "unigo_unsigned");

            // sets the path for the image....
            RequestBody folder =
                    RequestBody.create(MediaType.parse("text/plain"),
                            "users/" + uid + "/notes");

            // preparing api call...
            CloudinaryApi api = CloudinaryClient.getApi();

            Call<CloudinaryResponse> call =
                    api.uploadImage(body, preset, folder);

            call.enqueue(new Callback<CloudinaryResponse>() {
                @Override
                public void onResponse(Call<CloudinaryResponse> call,
                                       Response<CloudinaryResponse> response) {

                    if (response.isSuccessful() && response.body() != null) {

                        String imageUrl = response.body().secureUrl;
                        saveToFirestore(uid, imageUrl,tit, desc);

                    } else {
                        uploadState.setValue(
                                new NotesState.Error("Upload failed")
                        );
                    }
                }

                @Override
                public void onFailure(Call<CloudinaryResponse> call, Throwable t) {
                    uploadState.setValue(new NotesState.Error(t.getMessage()));
                }
            });

        } catch (Exception e) {
            uploadState.setValue(new NotesState.Error(e.getMessage()));
        }
    }

    private void saveToFirestore(String uid, String downloadUrl, String tit, String desc) {

        Map<String, Object> noteData = new HashMap<>();
        noteData.put("imageUrl", downloadUrl);
        noteData.put("timestamp", FieldValue.serverTimestamp());
        noteData.put("title",tit);
        noteData.put("description", desc);

        db.collection("users")
                .document(uid)
                .collection("notes")
                .add(noteData)
                .addOnSuccessListener(documentReference ->
                        uploadState.setValue(new NotesState.Success(downloadUrl))
                )
                .addOnFailureListener(e ->
                        uploadState.setValue(new NotesState.Error(e.getMessage()))
                );
    }
}