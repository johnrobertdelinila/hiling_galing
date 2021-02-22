/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lorma.hilinggaling.objecttsearch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.lorma.hilinggaling.HomeActivity;
import com.lorma.hilinggaling.ImageClassifier;
import com.lorma.hilinggaling.objectdetection.DetectedObject;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.ColorInfo;
import com.google.api.services.vision.v1.model.DominantColorsAnnotation;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageProperties;
import com.google.api.services.vision.v1.model.SafeSearchAnnotation;
import com.google.api.services.vision.v1.model.WebDetection;
import com.google.api.services.vision.v1.model.WebEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** A fake search engine to help simulate the complete work flow. */
public class SearchEngine {

  private static final String TAG = "SearchEngine";
  private ImageClassifier imageClassifier;
  // Akapulco, gotukola,

  private Feature feature;
  private String[] visionAPI = new String[]{"LANDMARK_DETECTION", "LOGO_DETECTION", "SAFE_SEARCH_DETECTION", "IMAGE_PROPERTIES", "WEB_DETECTION"};
  private String api = visionAPI[4];
  private static final String CLOUD_VISION_API_KEY = "AIzaSyBnf1YT0ieddqXV5dRcUG0k76WzExV5rTE";
  private boolean isHerbal = false;

  public interface SearchResultListener {
    void onSearchCompleted(DetectedObject object, List<Object> objectList);
  }

  private SearchResultListener mListener;

    final public void setListener(SearchResultListener listener) {
        mListener = listener;
    }

  private final RequestQueue searchRequestQueue;
  private final ExecutorService requestCreationExecutor;

  public SearchEngine(Context context, AppCompatActivity activity) {
    searchRequestQueue = Volley.newRequestQueue(context);
    requestCreationExecutor = Executors.newSingleThreadExecutor();
      try {
          imageClassifier = new ImageClassifier(activity);
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  public void searchPhoto(Bitmap bitmapm) {
      isHerbal = false;
      callCloudVision(bitmapm, null, null);
  }

  public void search(DetectedObject object, SearchResultListener listener) {
    // Crops the object image out of the full image is expensive, so do it off the UI thread.
      /*identifyImage.runModelInference(labels -> {
          finalizeObjects(labels, object, listener);
      });*/
//      finalizeObjects(imageClassifier.classifyImage(getResizedBitmap(LiveObjectDetectionActivity.searchedBitmap)), object, listener);
      isHerbal = false;
      callCloudVision(object.getBitmap(), listener, object);
  }

  @SuppressLint("StaticFieldLeak")
  private void callCloudVision(final Bitmap bitmap, SearchResultListener listener, DetectedObject object) {
        final List<Feature> featureList = new ArrayList<>();

          feature = new Feature();
          feature.setType(visionAPI[4]);
          feature.setMaxResults(20);
        featureList.add(feature);

        final List<AnnotateImageRequest> annotateImageRequests = new ArrayList<>();

        AnnotateImageRequest annotateImageReq = new AnnotateImageRequest();
        annotateImageReq.setFeatures(featureList);
        annotateImageReq.setImage(getImageEncodeImage(bitmap));
        annotateImageRequests.add(annotateImageReq);

        new AsyncTask<java.lang.Object, Void, List<Object>>() {
            @Override
            protected List<Object> doInBackground(java.lang.Object... params) {
                try {

                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY);

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(annotateImageRequests);

                    Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    if (convertResponseToString(response).size() <= 0 && isHerbal) {
                        Log.e("HERBALS", "NO RESULT FROM GVISION");
                        return finalizeObjects(imageClassifier.classifyImage(getResizedBitmap(bitmap)), object, listener);
                    }else {
                        return convertResponseToString(response);
                    }
                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " + e.getMessage());
                }
//                return "Cloud Vision API request failed. Check logs for details.";
                Log.e(TAG, "Cloud Vision API request failed. Check logs for details.");
                return new ArrayList<>();
            }

            protected void onPostExecute(List<Object> identifiedList) {
                if (listener == null) {
                    mListener.onSearchCompleted(object, identifiedList);
                }else {
                    listener.onSearchCompleted(object, identifiedList);
                }


            }
        }.execute();
    }

    private List<Object> formatAnnotation(WebDetection webDetection) {
        List<Object> identifiedList = new ArrayList<>();
        if (webDetection != null && webDetection.getWebEntities().size() > 0) {
            for (WebEntity entity : webDetection.getWebEntities()) {
                Log.e("HERBALS", String.valueOf(entity));
                if (entity != null && entity.getDescription() != null) {
                    if (checkContains(HomeActivity.herbals, entity.getDescription().trim()) && !containsName(identifiedList, entity.getDescription().trim())) {
                        Object product = new Object("", searchScientific(entity.getDescription()), String.valueOf(entity.getScore()));
                        identifiedList.add(product);
                    }else if (entity.getDescription().trim().equalsIgnoreCase("herbal") || entity.getDescription().trim().equalsIgnoreCase("plants")
                            || entity.getDescription().trim().equalsIgnoreCase("herb") || entity.getDescription().trim().equalsIgnoreCase("fruit")
                            || entity.getDescription().trim().equalsIgnoreCase("Leaf")) {
                        isHerbal = true;
                    }
                }
            }
        }else {
            Log.e("HERBALS", "NO RESULT");
        }
        Log.e("HERBALS", "END...");
        return identifiedList;
    }

    public boolean containsName(final List<Object> list, final String name){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return list.stream().anyMatch(o -> o.getTitle().equalsIgnoreCase(name));
        }else {
            return false;
        }
    }

    private boolean checkContains(List<String> arr, String searchHerb) {
      boolean result = false;
      for (String herbal: arr) {
          if (herbal.equalsIgnoreCase(searchHerb)) {
              result = true;
              break;
          }
      }
      return result;
    }

    private String searchScientific(String scientific) {
      String herbal = null;
      for (String herbalName: HomeActivity.herbalsTakki.keySet()) {
          List<String> scientificNames = HomeActivity.herbalsTakki.get(herbalName);
          Log.e("SCIENCE", String.valueOf(scientificNames));
          if (scientificNames != null && checkContains(scientificNames, scientific)) {
              herbal = herbalName;
              break;
          }
      }
      return herbal;
    }

    private List<Object> convertResponseToString(BatchAnnotateImagesResponse response) {

        AnnotateImageResponse imageResponses = response.getResponses().get(0);

        List<EntityAnnotation> entityAnnotations;

        List<Object> identifiedList = new ArrayList<>();
        /*case "LANDMARK_DETECTION":
                entityAnnotations = imageResponses.getLandmarkAnnotations();
                message = formatAnnotation(entityAnnotations);
                break;
            case "LOGO_DETECTION":
                entityAnnotations = imageResponses.getLogoAnnotations();
                message = formatAnnotation(entityAnnotations);
                break;
            case "SAFE_SEARCH_DETECTION":
                SafeSearchAnnotation annotation = imageResponses.getSafeSearchAnnotation();
                message = getImageAnnotation(annotation);
                break;
            case "IMAGE_PROPERTIES":
                ImageProperties imageProperties = imageResponses.getImagePropertiesAnnotation();
                message = getImageProperty(imageProperties);
                break;*/

        if ("WEB_DETECTION".equals(api)) {
//            entityAnnotations = imageResponses.getLabelAnnotations();
//            entityAnnotations = imageResponses.getWebDetection();
            WebDetection annotation = imageResponses.getWebDetection();
            identifiedList = formatAnnotation(annotation);
        }
        return identifiedList;
    }

    private String getImageAnnotation(SafeSearchAnnotation annotation) {
        return String.format("adult: %s\nmedical: %s\nspoofed: %s\nviolence: %s\n",
                annotation.getAdult(),
                annotation.getMedical(),
                annotation.getSpoof(),
                annotation.getViolence());
    }

    private String getImageProperty(ImageProperties imageProperties) {
        String message = "";
        DominantColorsAnnotation colors = imageProperties.getDominantColors();
        for (ColorInfo color : colors.getColors()) {
            message = message + "" + color.getPixelFraction() + " - " + color.getColor().getRed() + " - " + color.getColor().getGreen() + " - " + color.getColor().getBlue();
            message = message + "\n";
        }
        return message;
    }

    @NonNull
    private Image getImageEncodeImage(Bitmap bitmap) {
        Image base64EncodedImage = new Image();
        // Convert the bitmap to a JPEG
        // Just in case it's a format that Android understands but Cloud Vision
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Base64 encode the JPEG
        base64EncodedImage.encodeContent(imageBytes);
        return base64EncodedImage;
    }

  private List<Object> finalizeObjects(List<Map<String, String>> labels, DetectedObject object, SearchResultListener listener) {
      List<Object> identifiedList = new ArrayList<>();
      for (Map<String, String> result: labels) {
          Object product = new Object("", result.get("label"), result.get("value"));
          Log.e("HERBALS", String.valueOf(product));
          identifiedList.add(product);
      }
      Collections.reverse(identifiedList);
      return identifiedList;
//      listener.onSearchCompleted(object, identifiedList);
  }

    private static Bitmap getResizedBitmap(Bitmap bm) {
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
        if(!true){
            bm.recycle();
        }
        return resizedBitmap;
    }

  public void shutdown() {
    searchRequestQueue.cancelAll(TAG);
    requestCreationExecutor.shutdown();
  }
}
