package com.example.halalfoodscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    TextView eCodesTextView;
    TextView ingredientsTextView;
    EditText userInput;
    String baseUrl = "https://world.openfoodfacts.org/api/v0/product/";
    ArrayList<String> eCodes = new ArrayList<>();
    String[] ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eCodesTextView = findViewById(R.id.eCodes);
        ingredientsTextView = findViewById(R.id.ingredients);
        userInput = findViewById(R.id.userInput);
    }

    public void callApi(View view) {
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
                    ingredientsTextView.setText(Arrays.toString(ingredients));
                    eCodesTextView.setText(eCodes.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> eCodesTextView.setText("error"));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}