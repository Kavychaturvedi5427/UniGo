package com.kavya.unigo.ui.features.Notes;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {

    public static File createTempFileFromUri(Context context, Uri uri) throws Exception {

        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        File tempFile = new File(context.getCacheDir(),
                "upload_" + System.currentTimeMillis() + ".jpg");

        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[4096];
        int read;

        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();

        return tempFile;
    }
}