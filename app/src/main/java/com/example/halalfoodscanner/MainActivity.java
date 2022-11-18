package com.example.halalfoodscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
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
    TextView statusECode;
    TextView statusIngredients;
    EditText userInput;
    String baseUrl = "https://world.openfoodfacts.org/api/v0/product/";
    ArrayList<String> eCodes = new ArrayList<>();
    String[] ingredients;
    InputStream inputStream;
    String[] ids;
    ArrayList<String> halal = new ArrayList<>();
    ArrayList<String> musbooh = new ArrayList<>();
    ArrayList<String> haram = new ArrayList<>();
    String[] musboohIngredients = ("Acid Casein, Artificial Colors, FD&C Yellow No. 5, " +
            "Artificial Flavors, Aspartame, Balsamic Vinegar, Behenyl Alcohol, Docosanol, " +
            "Beta-Carotene, Beta Carotene, BHA, BHT, Butter fat Lipolyzed, Buttermilk Solids, " +
            "Calcium Stearate, Calcium Stearoyl Lactylate, Carrageenan, Caseinates, Cetyl Alcohol, " +
            "Cheese Powder, Cultured Cream Lipolyzed, Cultured Milk, DATEM, " +
            "Di- Acetyl Tartrate Ester of Monoglycerides, Diglyceride, Disodium Inosinate, " +
            "Dried Milk, Enzyme Modified Lecithin, Enzyme Modified Soya Lecithin, Enzymes, " +
            "Ethoxylated Mono- and Diglycerides, Folic Acid, Glycerin, Glycerol Ester, " +
            "Glycerol Monostearate, Grape Seed Extract, Grape Skin Powder, Grape Seed Oil, " +
            "Hydroxylated Lecithin, Lactose, Magnesium Stearate, Margarine, Marshmallow, " +
            "Monoglycerides, Diglycerides, Niacin, Vitamin B3, Nonfat Dry Milk, Pectin, " +
            "Polyglycerol Esters of Fatty Acids, Polyoxythylene Sorbitan Monostearate, " +
            "Polysorbate 60, Polysorbate 65, Polysorbate 80, Propylene Glycol Monostearate, " +
            "Rennet, Rennet Casein, Riboflavin, Shellac, Sodium Lauryl Sulfate, " +
            "Sodium Stearoyl Lactylate, Softener, Sorbitan Monostearate, Soy Protein Concentrate, " +
            "Stevia, Sushi, Taurine, TBHQ, Thiamine Mononitrate, Tocopherol, Vitamin E, Turmeric, " +
            "Turmeric Extract, Turola Yeast, Vanilla Bean Powder, Whey, Whey Protein Concentrate, " +
            "Worcestershire Sauce").toLowerCase().split(",");
    String[] haramIngredients = ("Adenosine 5′ Monophosphate, Alcohol, Bacon, Beer, Beer Batters, " +
            "Beer Flavor, Brewer’s Yeast Extract, Brewers Yeast Extract, Carmine color, " +
            "Cochineal Color, Confectionary Glaze, Cytidene 5′ – Monophosphate, " +
            "Disodium Guanosine 5′ – Monophosphate, Disodium Uridine 5′ – Monophosphate, " +
            "Erythritol, Ethyl Alcohol, Ethanol, Fermented Cider, Gelatin, Ham, Hard Cider, " +
            "Inosito 5′ – Monophosphate, L-Cysteine, Lard, Nucleotides, Pork, Rainbow Sprinkles, " +
            "Rosemary Extract, Rum, Sherry Wine, Sovent Extracted Modified Lecithin, Soya Sauce, " +
            "Surimi, Teriyaki, Teriyaki Suace, Vanilla Bean Specks, Vanilla Beans, " +
            "Vanilla Beans Speck, Vanilla Extract, Wine, Wine Vinagar, Yeast Extract, " +
            "Brewer Yeast").toLowerCase().split(",");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eCodesTextView = findViewById(R.id.eCodes);
        ingredientsTextView = findViewById(R.id.ingredients);
        statusECode = findViewById(R.id.status);
        statusIngredients = findViewById(R.id.statusIngredients);
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

//    public void startProcess(View view) {
//        callApi(view);
//        String eCodesStatus = eCodesHalalOrHaram();
//        status.setText(eCodesStatus);
//    }

    public void callApi(View view) {
        // clear eCodes
        eCodes = new ArrayList<>();

        String url = baseUrl + userInput.getText() + ".json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String ingredientsString = response.getJSONObject("product").getString("ingredients_text").replace(" [", ", ").replace(" (", ", ")
                            .replace("]", "").replace(")", "");
                    ingredients = ingredientsString.split(",");

                    for (String ingredient: ingredients) {
                        boolean containsHaram = Arrays.asList(haramIngredients).contains(ingredient.toLowerCase());
                        boolean containsMusbooh = Arrays.asList(haramIngredients).contains(ingredient.toLowerCase());
                        if (containsHaram) {
                            statusIngredients.setText("Haram");
                            statusIngredients.setTextColor(Color.RED);
                            break;
                        } else if (containsMusbooh && statusIngredients.getText() != "Haram") {
                            statusIngredients.setText("Musbooh");
                            statusIngredients.setTextColor(Color.YELLOW);
                        }
                    }
                    if (statusIngredients.getText() == null) {
                        statusIngredients.setText("Halal");
                        statusIngredients.setTextColor(Color.GREEN);
                    }
                    // ingredients = response.getJSONObject("product").getString("ingredients_text").split("[,(]");
                    JSONArray eCodesJSON = response.getJSONObject("product").getJSONArray("additives_tags");
                    for (int i = 0; i < eCodesJSON.length(); i++){
                        eCodes.add(eCodesJSON.getString(i).substring(3));
                    }
                    String test = "Halal";
                    statusECode.setTextColor(Color.GREEN);
                    for (String eCode: eCodes) {
                        Log.d("gustymouse", eCode);
                        if (haram.contains(eCode.toUpperCase())) {
                            test = "Haram";
                            statusECode.setTextColor(Color.RED);
                            break;
                        } else if (musbooh.contains(eCode.toUpperCase())) {
                            test = "Musbooh";
                            statusECode.setTextColor(Color.YELLOW);
                        }
                    }
                    statusECode.setText(test);
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