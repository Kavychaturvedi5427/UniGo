package com.kavya.unigo.ui.about;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import com.kavya.unigo.databinding.AboutSectionBinding;

public class AboutApp extends AppCompatActivity {

    private AboutSectionBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AboutSectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Instagram
        binding.install.setOnClickListener(v ->
                openLink("https://www.instagram.com/kavy___17/")
        );

        // LinkedIn
        binding.linkedll.setOnClickListener(v ->
                openLink("https://www.linkedin.com/in/kavya-chaturvedi-1a181932a/")
        );

        // GitHub
        binding.gitll.setOnClickListener(v ->
                openLink("https://github.com/Kavychaturvedi5427")
        );

        // Learn More
        binding.learnMore.setOnClickListener(v ->
                openLink("https://kavychaturvedi5427.github.io/UniGo-Legal/about.htm")
        );

        // Terms of Use
        binding.TermsOfUse.setOnClickListener(v ->
                openLink("https://kavychaturvedi5427.github.io/UniGo-Legal/terms.htm")
        );

        // Privacy Policy
        binding.privacyPolicy.setOnClickListener(v ->
                openLink("https://kavychaturvedi5427.github.io/UniGo-Legal/privacy.htm")
        );

        // Back button
        binding.back.setOnClickListener(v -> finish());
    }

    /**
     * Opens a link using Chrome Custom Tabs
     */
    private void openLink(String url) {

        binding.Progress.setVisibility(View.VISIBLE);

        binding.Progress.postDelayed(() -> {
            // custom chrome tab intent...
            CustomTabsIntent customTabsIntent =
                    new CustomTabsIntent.Builder().build();

            customTabsIntent.launchUrl(this, Uri.parse(url));

            binding.Progress.setVisibility(View.GONE);

        }, 300);
    }
}