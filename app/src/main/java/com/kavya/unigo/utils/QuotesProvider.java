package com.kavya.unigo.utils;

import java.util.Random;

public class QuotesProvider {
    private static final String[] QUOTES = {"Small progress every day adds up to big results.",
            "Push yourself, because no one else will do it for you.",
            "Success is built on daily discipline.",
            "Your future is created by what you do today.",
            "Dream big. Work hard. Stay consistent."
    };
    public static String provideQuotes(){
        int index = (int) (System.currentTimeMillis() / (1000 * 60 * 60 * 24)) % QUOTES.length;
        return QUOTES[index];
    }
}
