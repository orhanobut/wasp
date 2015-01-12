package com.orhanobut.wasp;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Emmar Kardeslik
 */
public class IOUtils {

    private IOUtils() {
        //no instance
    }

    public static String readFileFromAssets(final Context context, final String filePath) throws IOException {

        StringBuilder builder = new StringBuilder();

        InputStream inputStream = context.getAssets().open(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String str;

        while ((str = reader.readLine()) != null) {
            builder.append(str);
        }

        reader.close();

        return builder.toString();

    }

}
