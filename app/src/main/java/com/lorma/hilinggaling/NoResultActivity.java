package com.lorma.hilinggaling;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lorma.hilinggaling.objecttsearch.Object;
import com.lorma.hilinggaling.objecttsearch.SearchEngine;
import com.lorma.hilinggaling.utils.Herbal;

import java.io.IOException;

import dmax.dialog.SpotsDialog;

import static com.lorma.hilinggaling.HomeActivity.REQUEST_IMAGE;

public class NoResultActivity extends AppCompatActivity {

    private ImageClassifier classifier;

    public static String[] herbalList = HomeActivity.herbalList;
    public static Integer[] drawables = HomeActivity.drawables;
    public static String[] scientificNames = HomeActivity.scientificNames;
    public static String[] diseases = HomeActivity.diseases;
    public static String[] descriptions = HomeActivity.descriptions;

    public static String[] locations = HomeActivity.locations;
    public static String[] commons = HomeActivity.commons;
    public static String[] goods = HomeActivity.goods;
    public static String[] topicals = HomeActivity.topicals;

    private SearchEngine searchEngine;

    private android.app.AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_result);

        searchEngine = new SearchEngine(getApplicationContext(), NoResultActivity.this);
        loadingDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(getResources().getString(R.string.wait))
                .build();

        searchEngine.setListener((object, objectList) -> {
            loadingDialog.dismiss();
            Intent intent = new Intent(NoResultActivity.this, HerbalDetailActivity.class);
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
                startActivity(intent);
            }else {
                Toast.makeText(this, "We cannot identify your image. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            classifier = new ImageClassifier(NoResultActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        findViewById(R.id.try_again_btn).setOnClickListener(v -> {
            if (classifier != null) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        findViewById(R.id.cancel_btn).setOnClickListener(v -> finish());
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
    protected void onDestroy() {
        classifier.close();
        searchEngine.shutdown();
        super.onDestroy();
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
                        searchEngine.searchPhoto(MediaStore.Images.Media.getBitmap(NoResultActivity.this.getContentResolver(), uri));
                    } catch (IOException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                    /*new Handler().postDelayed(() -> {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(NoResultActivity.this.getContentResolver(), uri);
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
                                Intent intent = new Intent(NoResultActivity.this, HerbalDetailActivity.class);
                                intent.putExtra("herbal", herbal);
                                startActivity(intent);
                            }
                        } catch (IOException e) {
                            Log.e(ImageClassifier.TAG, e.getMessage());
                            Toast.makeText(NoResultActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, 150);*/
                }
            }
        }
    }
}
