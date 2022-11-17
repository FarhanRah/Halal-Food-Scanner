package com.example.halalfoodscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    TextView eCodesTextView;
    TextView ingredientsTextView;
    TextView status;
    EditText userInput;
    String baseUrl = "https://world.openfoodfacts.org/api/v0/product/";
    ArrayList<String> eCodes = new ArrayList<>();
    String[] ingredients;
    InputStream inputStream;
    String[] ids;
    ArrayList<String> halal = new ArrayList<>();
    ArrayList<String> musbooh = new ArrayList<>();
    ArrayList<String> haram = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eCodesTextView = findViewById(R.id.eCodes);
        ingredientsTextView = findViewById(R.id.ingredients);
        status = findViewById(R.id.status);
        userInput = findViewById(R.id.userInput);

        inputStream = getResources().openRawResource(R.raw.ecodes);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String csvLine;
            while((csvLine = reader.readLine()) != null) {
                ids = csvLine.split(",");
                try {
                    if (Objects.equals(ids[1], "Halal")) {
                        halal.add(ids[0]);
                    } else if (Objects.equals(ids[1], "MUSBOOH")) {
                        musbooh.add(ids[0]);
                    } else {
                        haram.add(ids[0]);
                    }
                } catch (Exception e) {
                    Log.e("ERROR-ECODES", e.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startProcess(View view) {
        callApi(view);
        String eCodesStatus = eCodesHalalOrHaram();
        status.setText(eCodesStatus);
    }

    public void callApi(View view) {
        // clear eCodes
        eCodes = new ArrayList<>();

        String url = baseUrl + userInput.getText() + ".json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ingredients = response.getJSONObject("product").getString("ingredients_text").split("\\W+");;
                    JSONArray eCodesJSON = response.getJSONObject("product").getJSONArray("additives_tags");
                    for (int i = 0; i < eCodesJSON.length(); i++){
                        eCodes.add(eCodesJSON.getString(i).substring(3));
                    }
                    String test = "halal";
                    for (String eCode: eCodes) {
                        Log.d("gustymouse", eCode);
                        if (haram.contains(eCode.toUpperCase())) {
                            test = "Haram";
                        } else if (musbooh.contains(eCode.toUpperCase())) {
                            test = "Musbooh";
                        }
                    }
                    status.setText(test);
                    ingredientsTextView.setText(Arrays.toString(ingredients));
                    eCodesTextView.setText(eCodes.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> eCodesTextView.setText("error"));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

//        for (String eCode: eCodes) {
//            if (haram.contains(eCode)) {
//                status.setText("Haram");
//                break;
//            } else if (musbooh.contains(eCode)) {
//                status.setText("Musbooh");
//            } else if (halal.contains(eCode)) {
//                status.setText("Halal");
//            }
//        }
    }

    public String eCodesHalalOrHaram() {
        Log.d("gustymouse", String.valueOf(eCodes));
        for (String eCode: eCodes) {
            if (haram.contains(eCode.toUpperCase())) {
                return "Haram";
            } else if (musbooh.contains(eCode.toUpperCase())) {
                return "Musbooh";
            }
        }

        return "Halal";
    }
}