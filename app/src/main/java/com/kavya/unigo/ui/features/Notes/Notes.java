package com.kavya.unigo.ui.features.Notes;

import android.app.Dialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kavya.unigo.R;
import com.kavya.unigo.databinding.AddNotesBtmBinding;

public class Notes extends BottomSheetDialogFragment {

    private AddNotesBtmBinding binding;
    private AppCompatButton cameraBtn, galleryBtn, viewNotesBtn, save, cancel;
    private NotesViewModel viewModel;
    private ProgressBar progressBar;
    private Dialog dialog;
    private Uri imageUri;
    private EditText title, description;

    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                showPreviewDialog(uri);
            }
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result->{
            if(result){
                showPreviewDialog(imageUri);
            }
            else{
                Toast.makeText(requireContext(), "Camera Canceled", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showPreviewDialog(Uri uri) {
        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_preview);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        ImageView previewImg = dialog.findViewById(R.id.previewImage);
        save = dialog.findViewById(R.id.saveBtn);
        cancel = dialog.findViewById(R.id.cancelBtn);
        progressBar = dialog.findViewById(R.id.Progress);
        title = dialog.findViewById(R.id.titleInput);
        description = dialog.findViewById(R.id.descriptionInput);

        cancel.setOnClickListener(unused -> {
            dialog.dismiss();
        });

        previewImg.setImageURI(uri);

        save.setOnClickListener(v -> {
            String tit = title.getText().toString().trim();
            String desc = description.getText().toString().trim();
            if(tit.isEmpty()){
                title.setError("Title is required");
                title.requestFocus();
            }
            else viewModel.uploadToCloudinary(requireContext(),uri, tit, desc);
        });

        dialog.show();
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    public void observeState() {

        viewModel.getUploadState().observe(getViewLifecycleOwner(), notesState -> {

            if (notesState instanceof NotesState.Loading) {

                if (progressBar != null && save != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    save.setEnabled(false);
                }

            } else if (notesState instanceof NotesState.Success) {

                Toast.makeText(requireContext(),
                        "Upload Successfully",
                        Toast.LENGTH_SHORT).show();

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

            } else if (notesState instanceof NotesState.Error) {

                NotesState.Error error = (NotesState.Error) notesState;

                Toast.makeText(requireContext(),
                        error.message,
                        Toast.LENGTH_SHORT).show();

                if (progressBar != null && save != null) {
                    progressBar.setVisibility(View.GONE);
                    save.setEnabled(true);
                }
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AddNotesBtmBinding.inflate(inflater, container, false);
        cameraBtn = binding.cameraBtn;
        galleryBtn = binding.galleryBtn;
        viewNotesBtn = binding.viewNotesBtn;

        viewModel = new ViewModelProvider(this).get(NotesViewModel.class);

        observeState();

        cameraBtn.setOnClickListener(v -> {
            imageUri = createImageUri() ;   // uri received for that location....
            cameraLauncher.launch(imageUri);    // this is providing camera the location to store the captured image....

            /*
            Toast.makeText(getContext(), "This feature is not available at the moment.", Toast.LENGTH_SHORT).show();
            openCamera();
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   // this will show all the app which is used for image capturing...
             here we'll not use startActivity() because in this case when we start an intent and return from it will bring some result(data) with itself so inorder to handle that we'll use startActivityResult()...
            startActivityForResult(cameraIntent, camera_Req_code);
            */

        });

        galleryBtn.setOnClickListener(v -> {
            galleryLauncher.launch("image/*");  // this tells android that open a picker and show all the files that are images
            // it's an MIME type structure, syntax = type/subtype....
        });

        viewNotesBtn.setOnClickListener(v->{
            Toast.makeText(requireContext(), "Under Construction", Toast.LENGTH_SHORT).show();
        });
        
        
        return binding.getRoot();
    }

/*   when users intent returns from the camera app it will return to this method
    -->  resultCode is very imp as this is responsible for checking the validity of the result from the intent...
    let's say we open camera and instantly press back btn then in that case the result is not generated properly this will later cause the Exception...
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == camera_Req_Code){
            }
        }
    }
*/

    // working of this method --> it tells android to create an empty slot in the gallery for storing an image and give me its address..
    private Uri createImageUri() {
        ContentValues values = new ContentValues();     // small container we put all the info of the image...like metadata of the image...
        values.put(MediaStore.Images.Media.TITLE, "UniGo_Image");       // setting the meta data ...
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");

        return requireContext()
                .getContentResolver()   // this establishes the connection between the app and the android storage...
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  // this tells android i want to create something inside the public images folder...
        // basically insert() is responsible for creating a slot in the gallery for storing the image...  and returns the URI of that slot...
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(d -> {
            FrameLayout bottomsheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomsheet != null) {
                bottomsheet.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.custom_btm));
                ;
                bottomsheet.setClipToOutline(true);
            }
        });
        return dialog;
    }

}
