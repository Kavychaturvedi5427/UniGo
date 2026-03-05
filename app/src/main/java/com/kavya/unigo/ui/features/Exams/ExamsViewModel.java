package com.kavya.unigo.ui.features.Exams;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExamsViewModel extends ViewModel {

    private MutableLiveData<List<String>> parsedSchedule = new MutableLiveData<>();

    public LiveData<List<String>> getParsedState() {
        return parsedSchedule;
    }
    public void parseExamSchedule(InputImage image) {
        // working will be implemented later....
    }
}