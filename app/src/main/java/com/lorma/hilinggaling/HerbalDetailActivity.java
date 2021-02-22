package com.lorma.hilinggaling;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lorma.hilinggaling.utils.Adapter;
import com.lorma.hilinggaling.utils.Herbal;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HerbalDetailActivity extends AppCompatActivity {

    //    private ImplementationItem item;
    private NestedScrollView rootView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView imageView;
    private TextView plant, desc, treatment, usage, location, best_for, classification;
    private Toolbar toolbar;
    private MaterialButton button;

    private String universalDesc;
    private String uniHerbalName;

    private int image;

    private RecyclerView recyclerView;
    private Adapter adapter;
    public static String[] herbalList = HomeActivity.herbalList;
    public static Integer[] drawables = HomeActivity.drawables;
    public static String[] scientificNames = HomeActivity.scientificNames;
    public static String[] diseases = HomeActivity.diseases;
    public static String[] descriptions = HomeActivity.descriptions;

    public static String[] locations = HomeActivity.locations;
    public static String[] commons = HomeActivity.commons;
    public static String[] goods = HomeActivity.goods;
    public static String[] topicals = HomeActivity.topicals;
    public static ArrayList<Herbal> herbalNamesArrayList = new ArrayList<>();
    public static ArrayList<Herbal> array_sort;

    private ImageClassifier classifier;
    private static final int REQUEST_IMAGE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_herbal_detail);
        setUpToolbar();
        init();

        try {
            classifier = new ImageClassifier(HerbalDetailActivity.this);
        } catch (IOException e) {
            Log.e(ImageClassifier.TAG, "Failed to initialize an image classifier.");
        }

        Herbal herbal = (Herbal) getIntent().getSerializableExtra("herbal");
        String name = herbal.getName();
        boolean isShow = getIntent().getBooleanExtra("isShow", true);
        if (name != null) {
            int usage = 0;
            if (name.equalsIgnoreCase("yerba buena")) {
                usage = R.array.yerba_usage;
            }else if (name.equalsIgnoreCase("tsaang gubat")) {
                usage = R.array.tsaang_usage;
            }else if (name.equalsIgnoreCase("lagundi")) {
                usage = R.array.lagundi_usage;
            }else if (name.equalsIgnoreCase("neem tree")) {
                usage = R.array.gotu_usage;
            }else if (name.equalsIgnoreCase("akapulco")) {
                usage = R.array.akapulko_usage;
            }else if (name.equalsIgnoreCase("bayabas")) {
                usage = R.array.garlic_usage;
            }else if (name.equalsIgnoreCase("sambong")) {
                usage = R.array.sambong_usage;
            }else if (name.equalsIgnoreCase("Niyog-niyogan")) {
                usage = R.array.niyog_usage;
            }else if (name.equalsIgnoreCase("malunggay")) {
                usage = R.array.malunggay_usage;
            }else if (name.equalsIgnoreCase("oregano")) {
                usage = R.array.oregano_usage;
            }else if (name.equalsIgnoreCase("saluyot")) {
                usage = R.array.saluyot_usage;
            }else if (name.equalsIgnoreCase("tawa-tawa")) {
                usage = R.array.tawa_usage;
            }else if (name.equalsIgnoreCase("Sampa-sampalukan")) {
                usage = R.array.sampalukan_usage;
            }else if (name.equalsIgnoreCase("Ampalaya Leaves")) {
                usage = R.array.amplaya_usage;
            }else if (name.equalsIgnoreCase("Pansit-pansitan")) {
                usage = R.array.pancit_usage;
            }
            showPlant(herbal.getName(), herbal.getScientific(), herbal.getDescriptions(), herbal.getDisease(),
                    usage, herbal.getImage_drawable(), herbal.getLocation(), herbal.getCommon(), herbal.getGood(), herbal.getTopical());
        }

        if (isShow) {
            button.setVisibility(View.VISIBLE);
        }else {
            button.setVisibility(View.GONE);
        }

        button.setOnClickListener(v -> finish());
    }

    private void init() {
        plant = findViewById(R.id.text_plant);
        desc = findViewById(R.id.text_desc);
        treatment = findViewById(R.id.text_treatment);
        usage = findViewById(R.id.text_usage);
        imageView = findViewById(R.id.detail_image);
        button = findViewById(R.id.try_again_btn);
        location = findViewById(R.id.text_location);
        classification = findViewById(R.id.text_classify);
        best_for = findViewById(R.id.text_best);
    }

    private void setUpToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void showPlant(String title, String name, String descc, String treatmentt, int usagess, int drawable, String locationn, String commonn, String goodn, String topicall) {
        toolbar.setTitle(title);
        plant.setText(name);
        desc.setText(descc); // Replace for common name in description
        treatment.setText(treatmentt);
        StringBuilder stringBuilder = new StringBuilder();
        String[] usages = getResources().getStringArray(usagess);
        for (String usage: usages) {
            stringBuilder.append("\u2022");
            stringBuilder.append(usage);
            stringBuilder.append("\n");
        }
        universalDesc = treatmentt;
        uniHerbalName = title;
        usage.setText(stringBuilder.toString());
        imageView.setImageResource(drawable);
        location.setText(locationn);
        best_for.setText(goodn);
        classification.setText(topicall);
        ((TextView) findViewById(R.id.text_common)).setText(commonn);
    }

    private String replaceForCommonDesc(String desc, List<String> herbalNames, String commonName) {
        String overrideString = desc;
        for (String herbalName: herbalNames) {
            overrideString = overrideString.replace(herbalName, commonName);
        }
        return overrideString;
    }

    private void processSearch(String search, TextView noRes) {
        array_sort.clear();
        for (int i = 0; i < herbalNamesArrayList.size(); i++) {
            if (search.length() > 0 && herbalNamesArrayList.get(i).getDisease().toLowerCase().trim().contains(search)) {
                if (!herbalNamesArrayList.get(i).getName().equalsIgnoreCase(uniHerbalName)) {
                    array_sort.add(herbalNamesArrayList.get(i));
                }
            }
        }

        if (array_sort.size() > 0) {
            String name = array_sort.get(0).getName().toLowerCase();
            Herbal herbal = array_sort.get(0);
            Intent intent = new Intent(HerbalDetailActivity.this, HerbalDetailActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("isShow", false);
            intent.putExtra("herbal", herbal);
            startActivity(intent);
        }

        if (array_sort.size() == 0) {
//            recyclerView.setVisibility(View.GONE);
            noRes.setVisibility(View.VISIBLE);
        }else {
            noRes.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter = new Adapter(HerbalDetailActivity.this, array_sort);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void populateTreatment(String treatments, ChipGroup chipGroup, TextView textView) {
        String[] treatmentArr = treatments.split(",");
        List<Chip> chips = new ArrayList<>();
        for (String treatment: treatmentArr) {
            Chip chip = new Chip(HerbalDetailActivity.this);
            chip.setText(treatment.trim());
            chip.setOnClickListener(v -> {
                for (Chip chip1: chips) {
                    chip1.setTextColor(getResources().getColor(R.color.semi_transparent));
                }
                chip.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                processSearch(treatment.toLowerCase().trim(), textView);
            });
            chips.add(chip);
            chipGroup.addView(chip);
        }
    }

    public void showSimilarDialog(View view1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Treatments");
        builder.setMessage("Identify similar leaves per treatment.");
        View view = LayoutInflater.from(this).inflate(R.layout.layout_similar, null);
        ChipGroup chipGroup = view.findViewById(R.id.chip_group);
        TextView textView = view.findViewById(R.id.text_no_result);
        builder.setNegativeButton("DONE", (dialog, which) -> dialog.dismiss());
        populateTreatment(universalDesc, chipGroup, textView);
        builder.setView(view);
        setUpRecyclerView(view);

        builder.show();
    }

    private void setUpRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setVisibility(View.GONE);

        herbalNamesArrayList = populateList();
        ArrayList<Herbal> emptyList = new ArrayList<>();
        adapter = new Adapter(this, emptyList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        array_sort = new ArrayList<>();
        array_sort = populateList();

        recyclerView.addOnItemTouchListener(new SearchHerbalActivity.RecyclerTouchListener(getApplicationContext(), recyclerView, new SearchHerbalActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String name = array_sort.get(position).getName().toLowerCase();
                Herbal herbal = array_sort.get(position);
                Intent intent = new Intent(HerbalDetailActivity.this, HerbalDetailActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("isShow", false);
                intent.putExtra("herbal", herbal);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));
    }

    private ArrayList<Herbal> populateList(){
        ArrayList<Herbal> list = new ArrayList<>();
        for(int i = 0; i < herbalList.length; i++){
            Herbal herbal = new Herbal();
            herbal.setName(herbalList[i]);
            herbal.setImage_drawable(drawables[i]);
            herbal.setScientific(scientificNames[i]);
            herbal.setDisease(diseases[i]);
            herbal.setDescriptions(descriptions[i]);
            herbal.setLocation(locations[i]);
            herbal.setGood(goods[i]);
            herbal.setCommon(commons[i]);
            herbal.setTopical(topicals[i]);
            list.add(herbal);
        }
        return list;
    }

    public void identifyAgain(View view) {
        if (classifier != null) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE);
        }
    }

    private static Bitmap getResizedBitmap(Bitmap bm, boolean isNecessaryToKeepOrig) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) ImageClassifier.DIM_IMG_SIZE_X) / width;
        float scaleHeight = ((float) ImageClassifier.DIM_IMG_SIZE_Y) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        if(!isNecessaryToKeepOrig){
            bm.recycle();
        }
        return resizedBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    new Handler().postDelayed(() -> {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(HerbalDetailActivity.this.getContentResolver(), uri);
                            Bitmap resize_bitmap = getResizedBitmap(bitmap, true);

                            List<Map<String, String>> result = classifier.classifyImage(resize_bitmap);
                            Log.e("RESULT", String.valueOf(result));
                            if (result == null || result.size() == 0) {
                            }else {
                                Collections.reverse(result);
                                Map<String, String> output = result.get(0);

                                Herbal herbal = new Herbal();
                                for (int i = 0; i < herbalList.length; i++) {
                                    String name = herbalList[i];
                                    if (name.equalsIgnoreCase(output.get("label"))) {
                                        herbal.setName(herbalList[i]);
                                        herbal.setImage_drawable(drawables[i]);
                                        herbal.setScientific(scientificNames[i]);
                                        herbal.setDisease(diseases[i]);
                                        herbal.setDescriptions(descriptions[i]);
                                        herbal.setLocation(locations[i]);
                                        herbal.setGood(goods[i]);
                                        herbal.setCommon(commons[i]);
                                        herbal.setTopical(topicals[i]);
                                        break;
                                    }
                                }
                                Intent intent = new Intent(HerbalDetailActivity.this, HerbalDetailActivity.class);
                                intent.putExtra("herbal", herbal);
                                startActivity(intent);
                            }
                        } catch (IOException e) {
                            Log.e(ImageClassifier.TAG, e.getMessage());
                            Toast.makeText(HerbalDetailActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, 150);
                }
            }
        }
    }

    public void diclaimer(View view) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HerbalDetailActivity.this);
        builder.setTitle("Disclaimer");
        builder.setMessage(R.string.plant_disclaimer);
        builder.setNegativeButton("DONE", (dialog, which) -> dialog.dismiss());
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTypeface(Typeface.DEFAULT_BOLD);
    }
}
