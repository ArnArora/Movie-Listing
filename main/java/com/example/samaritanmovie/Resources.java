package com.example.samaritanmovie;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.samaritanmovie.movie_list.Movie;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

//a file with two helpful methods for creating movies from the JSON files
//static methods so that both the listing and search page can access it
public class Resources {
    //create hashmap with poster string name as key and drawable ID as value
    //easy to convert JSON attributes into Movie class
    public static HashMap<String, Drawable> getPosters(Context context){
        HashMap<String, Drawable> posters = new HashMap<>();
        for (int i = 1; i<=9; i++){
            String posterName = "@drawable/poster"+Integer.toString(i);
            //get id of poster
            int id = context.getResources().getIdentifier(posterName, null, context.getPackageName());
            posters.put("poster"+Integer.toString(i)+".jpg", context.getResources().getDrawable(id));
        }
        //edge case poster
        int id = context.getResources().getIdentifier("@drawable/posterthatismissing", null, context.getPackageName());
        posters.put("posterthatismissing.jpg", context.getResources().getDrawable(id));
        return posters;
    }

    //read from JSON file and get JSON string back
    public static String loadJSONFromAsset(int pageNum, Context context) {
        String json = null;
        try {
            InputStream in = context.getAssets().open("ListingPage"+Integer.toString(pageNum)+".json");
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            //convert byte array into string
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }
}
