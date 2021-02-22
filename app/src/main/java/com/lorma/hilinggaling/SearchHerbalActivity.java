package com.lorma.hilinggaling;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lorma.hilinggaling.utils.Adapter;
import com.lorma.hilinggaling.utils.Herbal;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;

public class SearchHerbalActivity extends AppCompatActivity {

    private TextInputEditText etsearch;

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

    int textlength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_herbal);
        setUpToolbar();

        setUpRecyclerView();

        etsearch = findViewById(R.id.editText);
        etsearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textlength = etsearch.getText().length();
                processSearch(etsearch.getText().toString().toLowerCase().trim());
            }
        });

    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recycler);

        herbalNamesArrayList = populateList();
        ArrayList<Herbal> emptyList = new ArrayList<>();
        adapter = new Adapter(this, emptyList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        array_sort = new ArrayList<>();
        array_sort = populateList();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Herbal herbal = array_sort.get(position);
                Intent intent = new Intent(SearchHerbalActivity.this, HerbalDetailActivity.class);

                intent.putExtra("herbal", herbal);
                intent.putExtra("isShow", false);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));
    }

    private void processSearch(String search) {
        array_sort.clear();
        for (int i = 0; i < herbalNamesArrayList.size(); i++) {
            if (textlength > 0 && herbalNamesArrayList.get(i).getDisease().toLowerCase().trim().contains(search)) {
                array_sort.add(herbalNamesArrayList.get(i));
            }
        }
        Collections.sort(array_sort, (v1, v2) -> v1.getName().compareTo(v2.getName()));
        adapter = new Adapter(SearchHerbalActivity.this, array_sort);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
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

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) { }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
