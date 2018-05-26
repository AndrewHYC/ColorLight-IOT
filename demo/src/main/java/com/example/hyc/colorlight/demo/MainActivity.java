/*
 * Copyright (C) 2017 Jared Rummler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.hyc.colorlight.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

//implements ColorPickerDialogListener
public class MainActivity extends AppCompatActivity  {

  private static final String TAG = "MianFragment";

  // Give your color picker dialog unique IDs if you have multiple dialogs.
  private static final int DIALOG_ID = 0;

  public static final String LIGHT_NAME = "light_name";
  public static final String LIGHT_IMAGE_ID = "light_image_id";
  public static final String LIGHT_ID = "light_id";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Intent intent = getIntent();
    String lightName = intent.getStringExtra(LIGHT_NAME);
    int lightImageId = intent.getIntExtra(LIGHT_IMAGE_ID, 0);
    String lightId = intent.getStringExtra(LIGHT_ID);
    Log.d(TAG, "onCreate: lightId="+lightId);

    Bundle bundle = new Bundle();       //Activity向Fragment传值
    bundle.putString("LIGHT_ID", lightId);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
    ImageView homeImageView = (ImageView)findViewById(R.id.light_image_view);
    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    if(actionBar != null){
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    collapsingToolbarLayout.setTitle(lightName);
    Glide.with(this).load(lightImageId).into(homeImageView);


    if(savedInstanceState == null) {

      DemoFragment demoFragment = new DemoFragment();
      demoFragment.setArguments(bundle);

      ColorFragment colorFragment = new ColorFragment();
      colorFragment.setArguments(bundle);

      getFragmentManager().beginTransaction()
              .add(R.id.light_card_view1, demoFragment)
              .commit();

      getFragmentManager().beginTransaction()
              .add(R.id.light_card_view2, colorFragment)
              .commit();
    }



  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }


}
 