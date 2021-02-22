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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.lorma.hilinggaling.HerbalDetailActivity;
import com.lorma.hilinggaling.HomeActivity;
import com.lorma.hilinggaling.R;
import com.lorma.hilinggaling.objecttsearch.ObjectAdapter.ProductViewHolder;
import com.lorma.hilinggaling.utils.Herbal;

import java.util.List;

/** Presents the list of product items from cloud product search. */
public class ObjectAdapter extends Adapter<ProductViewHolder> {

  public static String[] herbalList = HomeActivity.herbalList;
  public static Integer[] drawables = HomeActivity.drawables;
  public static String[] scientificNames = HomeActivity.scientificNames;
  public static String[] diseases = HomeActivity.diseases;
  public static String[] descriptions = HomeActivity.descriptions;

  public static String[] locations = HomeActivity.locations;
  public static String[] commons = HomeActivity.commons;
  public static String[] goods = HomeActivity.goods;
  public static String[] topicals = HomeActivity.topicals;

  static class ProductViewHolder extends RecyclerView.ViewHolder {

    static ProductViewHolder create(ViewGroup parent) {
      View view =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.object_item, parent, false);
      return new ProductViewHolder(view);
    }

    private final ImageView imageView;
    private final TextView titleView;
    private final TextView subtitleView;
    private final RelativeLayout relativeLayout;
    private final int imageSize;

    private String capitalize(String str) {
      if (str != null) {
        String[] strArray = str.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
          String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
          builder.append(cap).append(" ");
        }
        return builder.toString();
      }else {
        return "";
      }
    }

    private ProductViewHolder(View view) {
      super(view);
      imageView = view.findViewById(R.id.product_image);
      titleView = view.findViewById(R.id.product_title);
      subtitleView = view.findViewById(R.id.product_subtitle);
      relativeLayout = view.findViewById(R.id.container);
      imageSize = view.getResources().getDimensionPixelOffset(R.dimen.product_item_image_size);
    }

    void bindProduct(Object object, int pos) {
      imageView.setImageDrawable(null);
      if (!TextUtils.isEmpty(object.imageUrl)) {
        new ImageDownloadTask(imageView, imageSize).execute(object.imageUrl);
      } else {
        imageView.setImageResource(R.drawable.leaf);
      }
      titleView.setText(capitalize(object.title));
      float percentage = Float.valueOf(String.format("%4.2f", Float.valueOf(object.subtitle))) * 100;
      subtitleView.setText("Probability: " + String.format("%4.0f", percentage) + "%");

      if (pos == 0) {
        titleView.setTypeface(null, Typeface.BOLD);
      }else {
        titleView.setTypeface(null, Typeface.NORMAL);
      }
    }
  }

  private final List<Object> objectList;
  private Activity activity;

  public ObjectAdapter(List<Object> objectList, Activity activity) {
    this.objectList = objectList;
    this.activity = activity;
  }

  @Override
  @NonNull
  public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return ProductViewHolder.create(parent);
  }

  @Override
  public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
    holder.bindProduct(objectList.get(position), position);
    holder.relativeLayout.setOnClickListener(v -> {
      Intent intent = new Intent(activity, HerbalDetailActivity.class);
      Object result = objectList.get(position);
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
      activity.startActivity(intent);
    });
  }

  @Override
  public int getItemCount() {
    return objectList.size();
  }
}
