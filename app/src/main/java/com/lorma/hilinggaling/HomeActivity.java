package com.lorma.hilinggaling;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lorma.hilinggaling.objecttsearch.Object;
import com.lorma.hilinggaling.objecttsearch.SearchEngine;
import com.lorma.hilinggaling.utils.Feedback;
import com.lorma.hilinggaling.utils.Herbal;
import com.lorma.hilinggaling.utils.ScreenUtil;
import com.lorma.hilinggaling.utils.SquareImageView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public static final int REQUEST_IMAGE = 1997;

    private ImageClassifier classifier;

    public static String[] herbalList;
    public static Integer[] drawables;
    public static String[] scientificNames;
    public static String[] diseases;
    public static String[] descriptions;

    public static String[] locations;
    public static String[] commons;
    public static String[] goods;
    public static String[] topicals;
    private SearchEngine searchEngine;

    private android.app.AlertDialog loadingDialog;
    public static Map<String, List<String>> herbalsTakki = new HashMap<>();
    public static List<String> herbals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();

        herbalList = getResources().getStringArray(R.array.herbalList);
        drawables = new Integer[]{R.drawable.lagundi_image, R.drawable.yerba_buena, R.drawable.tsaang_gubat, R.drawable.niyog, R.drawable.akapulko, R.drawable.bayabas,
                R.drawable.sambong, R.drawable.neem_tree, R.drawable.malunggay, R.drawable.oregano, R.drawable.saluyot, R.drawable.tawa_tawa,
                R.drawable.sampalukan, R.drawable.ampalaya, R.drawable.pansit_pansitan};
        scientificNames = getResources().getStringArray(R.array.scientificNames);
        diseases = getResources().getStringArray(R.array.diseases);
        descriptions = getResources().getStringArray(R.array.descriptions);

        locations = getResources().getStringArray(R.array.locations);
        commons = getResources().getStringArray(R.array.common);
        goods = getResources().getStringArray(R.array.good);
        topicals = getResources().getStringArray(R.array.topical);

        searchEngine = new SearchEngine(getApplicationContext(), HomeActivity.this);
        loadingDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(getResources().getString(R.string.wait))
                .build();

        /*if (MainActivity.isValidated != null && !MainActivity.isValidated) {
            Toast.makeText(this, "HAHAHAHA", Toast.LENGTH_SHORT).show();
            this.finish();
        }*/

        searchEngine.setListener((object, objectList) -> {
            loadingDialog.dismiss();
            Intent intent = new Intent(HomeActivity.this, HerbalDetailActivity.class);
            if (objectList.size() > 0) {
                Object result = objectList.get(0);
                Herbal herbal = new Herbal();
                for (int i = 0; i < herbalList.length; i++) {
                    String name = herbalList[i];
                    if (result.getTitle() != null && name.equalsIgnoreCase(result.getTitle())) {
                        herbal.setName(name);
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
                intent.putExtra("herbal", herbal);
            }else {
                intent = new Intent(HomeActivity.this, NoResultActivity.class);
            }
            startActivity(intent);
        });

        for (String herbal: herbalList) {
            List<String> haha = new ArrayList<>();
            String title = null;
            if (herbal.equalsIgnoreCase(getString(R.string.lagundi_title))) {
                title = getString(R.string.lagundi_title);
                haha = Arrays.asList(getResources().getStringArray(R.array.lagundi));
            }else if (herbal.equalsIgnoreCase(getString(R.string.akapulko_title))) {
                title = getString(R.string.akapulko_title);
                haha = Arrays.asList(getResources().getStringArray(R.array.akapulko));
            }else if (herbal.equalsIgnoreCase(getString(R.string.gotu_title))) {
                title = getString(R.string.gotu_title);
                haha = Arrays.asList(getResources().getStringArray(R.array.gotu_kola));
            }else if (herbal.equalsIgnoreCase(getString(R.string.tsaang_title))) {
                title = getString(R.string.tsaang_title);
                haha = Arrays.asList(getResources().getStringArray(R.array.tsaang_gubat));
            }else if (herbal.equalsIgnoreCase(getString(R.string.yerba_title))) {
                title = getString(R.string.yerba_title);
                haha = Arrays.asList(getResources().getStringArray(R.array.yerba_buena));
            }else if (herbal.equalsIgnoreCase(getString(R.string.malunggay_title))) {
                title = getString(R.string.malunggay_title);
                haha = Arrays.asList(getResources().getStringArray(R.array.malunggay));
            }else if (herbal.equalsIgnoreCase(getString(R.string.niyog_title))) {
                title = getString(R.string.niyog_title);
                haha = Arrays.asList(getResources().getStringArray(R.array.niyog_niyogan));
            }else if (herbal.equalsIgnoreCase(getString(R.string.garlic_title))) {
                title = getString(R.string.garlic_title);
                haha = Arrays.asList(getResources().getStringArray(R.array.bayabas));
            }else if (herbal.equalsIgnoreCase(getString(R.string.sambong_title))) {
                title = getString(R.string.sambong_title);
                haha = Arrays.asList(getResources().getStringArray(R.array.sambong));
            }else if (herbal.equalsIgnoreCase(getString(R.string.oregano_title))) {
                title = getString(R.string.oregano_title);
                haha = Arrays.asList(getResources().getStringArray(R.array.oregano));
            }else if (herbal.equalsIgnoreCase(getString(R.string.tawa_title))) {
                title = getString(R.string.tawa_title);
                haha = Arrays.asList(getResources().getStringArray(R.array.tawa_tawa));
            }else if (herbal.equalsIgnoreCase(getString(R.string.saluyot_title))) {
                title = getString(R.string.saluyot_title);
                haha = Arrays.asList(getResources().getStringArray(R.array.saluyot));
            }else if (herbal.equalsIgnoreCase(getString(R.string.sampalukan_title))) {
                title = getString(R.string.sampalukan_title);
                haha = Arrays.asList(getResources().getStringArray(R.array.sampalukan));
            }else if (herbal.equalsIgnoreCase(getString(R.string.amplaya_title))) {
                title = getString(R.string.amplaya_title);
                haha = Arrays.asList(getResources().getStringArray(R.array.ampalaya));
            }else if (herbal.equalsIgnoreCase(getString(R.string.pancit_title))) {
                title = getString(R.string.pancit_title);
                haha = Arrays.asList(getResources().getStringArray(R.array.pancit_pancitan));
            }
            herbals.addAll(haha);
            herbalsTakki.put(title, haha);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        float spaceSize = ScreenUtil.dp2px(24, this);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int adapterPosition = parent.getChildViewHolder(view).getAdapterPosition();
                GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
                int spanSize = spanSizeLookup.getSpanSize(adapterPosition);
                if (spanSize == 2) {
                    return;
                }
                int spanIndex = spanSizeLookup.getSpanIndex(adapterPosition, gridLayoutManager.getSpanCount());
                if (spanIndex == 0) {
                    outRect.set((int) spaceSize, (int) spaceSize, ((int) (spaceSize / 2)), 0);
                } else {
                    outRect.set(((int) (spaceSize / 2)), (int) spaceSize, (int) spaceSize, 0);
                }
            }
        });
        recyclerView.setAdapter(new MyRecyclerViewAdapter(this));

        askPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*FirebaseFirestore.getInstance().collection("validation").document("sample-id").get()
                .addOnCompleteListener(task -> {
                    if (task.getException() == null) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null) {
                            MainActivity.isValidated = (Boolean) documentSnapshot.get("isValidated");
                            if (MainActivity.isValidated != null && !MainActivity.isValidated) {
                                Toast.makeText(this, "HAHAHAHA", Toast.LENGTH_SHORT).show();
                                this.finish();
                            }
                        }
                    }
                });*/
    }

    private void init() {
        try {
            classifier = new ImageClassifier(HomeActivity.this);
        } catch (IOException e) {
            Log.e(ImageClassifier.TAG, "Failed to initialize an image classifier.");
        }

        recyclerView = findViewById(R.id.recycler_view);
    }

    public void searchPlant(View view) {
        startActivity(new Intent(HomeActivity.this, SearchHerbalActivity.class));
    }

    private void askPermission() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.isAnyPermissionPermanentlyDenied()){
                            Toast.makeText(HomeActivity.this, "You have denied some permissions permanently, if the app force close try granting permission from settings.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

    }

    public void diclaimer(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Disclaimer");
        builder.setMessage(R.string.disclaimer);
        builder.setNegativeButton("DONE", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void feedback(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Feedback");
        builder.setMessage("Please tell us your concern so that we can improve our service.");
        View view1 = LayoutInflater.from(this).inflate(R.layout.layout_feedback, null);
        EditText editText = view1.findViewById(R.id.input_feedback);
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("SUBMIT", (dialog, which) -> {
            if (editText != null && editText.getText() != null) {
                CollectionReference feedbacks = FirebaseFirestore.getInstance().collection("feedbacks");
                Feedback feedback = new Feedback();
                feedback.setFeedback(editText.getText().toString());
                feedbacks.add(feedback)
                        .addOnCompleteListener(task -> {
                            if (task.getException() != null) {
                                Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(this, "Your feedback has been successfully submitted! Thank you for your feedback.", Toast.LENGTH_LONG).show();
                        });
            }
        });
        builder.setView(view1);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTypeface(Typeface.DEFAULT_BOLD);
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTypeface(Typeface.DEFAULT_BOLD);

    }


    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        SquareImageView imageView;
        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_card);
            imageView = itemView.findViewById(R.id.imageView);
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

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MenuViewHolder> {

        Context context;

        public MyRecyclerViewAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_row, viewGroup, false);
            return new MenuViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MenuViewHolder menuViewHolder, int i) {
            if (i == 0) {
                menuViewHolder.title.setText(R.string.camera);
                menuViewHolder.imageView.setImageResource(R.drawable.camera);
                menuViewHolder.itemView.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, LiveObjectDetectionActivity.class)));
            }else if (i == 1) {
                menuViewHolder.title.setText(R.string.photo);
                menuViewHolder.imageView.setImageResource(R.drawable.photo);
                menuViewHolder.itemView.setOnClickListener(v -> {
                    if (classifier != null) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(intent, REQUEST_IMAGE);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {

                    try {
                        loadingDialog.show();
                        searchEngine.searchPhoto(MediaStore.Images.Media.getBitmap(HomeActivity.this.getContentResolver(), uri));
                    } catch (IOException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }

                    /*new Handler().postDelayed(() -> {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(HomeActivity.this.getContentResolver(), uri);
                            Bitmap resize_bitmap = getResizedBitmap(bitmap, true);

                            List<Map<String, String>> result = classifier.classifyImage(resize_bitmap);
                            Log.e("RESULT", String.valueOf(result));
                            if (result == null || result.size() == 0) {
                                startActivity(new Intent(HomeActivity.this, NoResultActivity.class));
                            }else {
                                Collections.reverse(result);
                                Map<String, String> output = result.get(0);

                                Herbal herbal = new Herbal();
                                for (int i = 0; i < herbalList.length; i++) {
                                    String name = herbalList[i];
                                    if (name.equalsIgnoreCase(output.get("label"))) {
                                        herbal.setName(name);
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
                                Intent intent = new Intent(HomeActivity.this, HerbalDetailActivity.class);
                                intent.putExtra("herbal", herbal);
                                startActivity(intent);
                            }
                        } catch (IOException e) {
                            Log.e(ImageClassifier.TAG, e.getMessage());
                            Toast.makeText(HomeActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, 150);*/
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        classifier.close();
        searchEngine.shutdown();
        super.onDestroy();
    }
}
