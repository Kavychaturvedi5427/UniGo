package com.kavya.unigo.ui.features.Notes;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.kavya.unigo.R;

public class FullImageActivity extends AppCompatActivity {

    private PhotoView fullimage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_lay);

        fullimage = findViewById(R.id.fullImage);

        String imgUrl = getIntent().getStringExtra("imageUrl");

        Glide.with(this).load(imgUrl).into(fullimage);

    }
}
