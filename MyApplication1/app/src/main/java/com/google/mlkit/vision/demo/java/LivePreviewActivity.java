/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mlkit.vision.demo.java;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import com.google.android.gms.common.annotation.KeepName;
import com.google.mlkit.vision.demo.CameraSource;
import com.google.mlkit.vision.demo.CameraSourcePreview;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.PoseCorrection;
import com.google.mlkit.vision.demo.R;
//import com.google.mlkit.vision.demo.java.barcodescanner.BarcodeScannerProcessor;
//import com.google.mlkit.vision.demo.java.facedetector.FaceDetectorProcessor;
//import com.google.mlkit.vision.demo.java.labeldetector.LabelDetectorProcessor;
//import com.google.mlkit.vision.demo.java.objectdetector.ObjectDetectorProcessor;
import com.google.mlkit.vision.demo.java.posedetector.PoseDetectorProcessor;
//import com.google.mlkit.vision.demo.java.segmenter.SegmenterProcessor;
//import com.google.mlkit.vision.demo.java.textdetector.TextRecognitionProcessor;
import com.google.mlkit.vision.demo.preference.PreferenceUtils;
import com.google.mlkit.vision.demo.preference.SettingsActivity;
//import com.google.mlkit.vision.face.FaceDetectorOptions;
//import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;
//import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
//import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;
//import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Live preview demo for ML Kit APIs. */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity
    implements OnRequestPermissionsResultCallback,
        OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {
  private static final String POSE_DETECTION = "Pose Detection";

  private static final String TAG = "LivePreviewActivity";
  private static final int PERMISSION_REQUESTS = 1;

  private CameraSource cameraSource = null;
  private CameraSourcePreview preview;
  private GraphicOverlay graphicOverlay;
  private String selectedModel = POSE_DETECTION;

  public static double[] poseAngles;
  public static TextToSpeech textToSpeech;

  private AlertDialog alertDialog;

  public int counter1=0;
  TextView timerTv;
  TextView counttime;
  ImageButton up,down;
  Button done;
  RelativeLayout rl1,rl2;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setContentView(R.layout.activity_vision_live_preview);


    textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
      @Override
      public void onInit(int i) {

        // if No error is found then only it will run
        if(i!=TextToSpeech.ERROR){
          // To Choose language of speech
          textToSpeech.setLanguage(Locale.UK);
          textToSpeech.setSpeechRate(0.8f);
        }
      }
    });

    AlertDialog.Builder builder = new AlertDialog.Builder(LivePreviewActivity.this);
    ViewGroup viewGroup = findViewById(android.R.id.content);
    View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.customview_timer, viewGroup, false);
    builder.setView(dialogView);
    builder.setCancelable(false);
    alertDialog = builder.create();
    alertDialog.show();

    counttime=dialogView.findViewById(R.id.counttime);
    timerTv = dialogView.findViewById(R.id.tv1_ta);
    up = dialogView.findViewById(R.id.btn1_ta);
    down = dialogView.findViewById(R.id.btn2_ta);
    done = dialogView.findViewById(R.id.done);
    rl1= dialogView.findViewById(R.id.rl1_ta);
    rl2= dialogView.findViewById(R.id.rl2_ta);


    up.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ++counter1;
        timerTv.setText(String.valueOf(counter1));
      }
    });

    down.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        --counter1;
        if(counter1>=0){
          timerTv.setText(String.valueOf(counter1));
        }else{
          counter1=0;
        }
      }
    });

    poseAngles = new double[8];

    poseAngles[0] = getIntent().getDoubleExtra("rha", 0);
    poseAngles[1] = getIntent().getDoubleExtra("lha", 0);
    poseAngles[2] = getIntent().getDoubleExtra("rka", 0);
    poseAngles[3] = getIntent().getDoubleExtra("lka", 0);
    poseAngles[4] = getIntent().getDoubleExtra("rsa", 0);
    poseAngles[5] = getIntent().getDoubleExtra("lsa", 0);
    poseAngles[6] = getIntent().getDoubleExtra("rea", 0);
    poseAngles[7] = getIntent().getDoubleExtra("lea", 0);

    preview = findViewById(R.id.preview_view);
    if (preview == null) {
      Log.d(TAG, "Preview is null");
    }
    graphicOverlay = findViewById(R.id.graphic_overlay);
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }

    Spinner spinner = findViewById(R.id.spinner);
    List<String> options = new ArrayList<>();
//    options.add(OBJECT_DETECTION);
//    options.add(OBJECT_DETECTION_CUSTOM);
//    options.add(CUSTOM_AUTOML_OBJECT_DETECTION);
//    options.add(FACE_DETECTION);
//    options.add(TEXT_RECOGNITION);
//    options.add(BARCODE_SCANNING);
//    options.add(IMAGE_LABELING);
//    options.add(IMAGE_LABELING_CUSTOM);
//    options.add(CUSTOM_AUTOML_LABELING);
    options.add(POSE_DETECTION);
    //options.add(SELFIE_SEGMENTATION);

    // Creating adapter for spinner
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_style, options);
    // Drop down layout style - list view with radio button
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // attaching data adapter to spinner
    spinner.setAdapter(dataAdapter);
    spinner.setOnItemSelectedListener(this);

    ToggleButton facingSwitch = findViewById(R.id.facing_switch);
    facingSwitch.setOnCheckedChangeListener(this);

    ImageView settingsButton = findViewById(R.id.settings_button);
    settingsButton.setOnClickListener(
        v -> {
          Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
          intent.putExtra(
              SettingsActivity.EXTRA_LAUNCH_SOURCE, SettingsActivity.LaunchSource.LIVE_PREVIEW);
          startActivity(intent);
        });

      if (allPermissionsGranted()) {
          createCameraSource(selectedModel);
      } else {
          getRuntimePermissions();
      }
  }

  @Override
  public synchronized void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    // An item was selected. You can retrieve the selected item using
    // parent.getItemAtPosition(pos)
    selectedModel = parent.getItemAtPosition(pos).toString();
    Log.d(TAG, "Selected model: " + selectedModel);
    preview.stop();
    if (allPermissionsGranted()) {
        createCameraSource(selectedModel);
        startCameraSource();
    } else {
      getRuntimePermissions();
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    Log.d(TAG, "Set facing");
    if (cameraSource != null) {
      if (isChecked) {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
      } else {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
      }
    }
    preview.stop();
    startCameraSource();
  }

  private void createCameraSource(String model) {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = new CameraSource(this, graphicOverlay);
    }

    try {
      switch (model) {
        case POSE_DETECTION:
          PoseDetectorOptionsBase poseDetectorOptions =
              PreferenceUtils.getPoseDetectorOptionsForLivePreview(this);
          Log.i(TAG, "Using Pose Detector with options " + poseDetectorOptions);
          boolean shouldShowInFrameLikelihood =
              PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this);
          boolean visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this);
          boolean rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this);
          boolean runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this);
          cameraSource.setMachineLearningFrameProcessor(new PoseDetectorProcessor(
              this, poseDetectorOptions, shouldShowInFrameLikelihood, visualizeZ, rescaleZ,
              runClassification, /* isStreamMode = */true));
          break;
        default:
          Log.e(TAG, "Unknown model: " + model);
      }
    } catch (RuntimeException e) {
      Log.e(TAG, "Can not create image processor: " + model, e);
      Toast.makeText(
              getApplicationContext(),
              "Can not create image processor: " + e.getMessage(),
              Toast.LENGTH_LONG)
          .show();
    }
  }

  /**
   * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  private void startCameraSource() {
    if (cameraSource != null) {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl1.setVisibility(View.GONE);
                rl2.setVisibility(View.VISIBLE);
                new CountDownTimer(counter1 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                      textToSpeech.speak(Integer.toString(counter1), TextToSpeech.QUEUE_ADD, null);
                      counttime.setText(String.valueOf(counter1));
                      counter1--;
                    }

                    @Override
                    public void onFinish() {
                        alertDialog.dismiss();
                      textToSpeech.speak("Go!", TextToSpeech.QUEUE_ADD, null);

                        try {
                            if (preview == null) {
                                Log.d(TAG, "resume: Preview is null");
                            }
                            if (graphicOverlay == null) {
                                Log.d(TAG, "resume: graphOverlay is null");
                            }
                            preview.start(cameraSource, graphicOverlay);

                          PoseCorrection poseCorrection = new PoseCorrection(getIntent().getStringExtra("name"));
                          poseCorrection.startCorrecting();
                        } catch (IOException e) {
                            Log.e(TAG, "Unable to start camera source.", e);
                            cameraSource.release();
                            cameraSource = null;
                        }
                    }
                }.start();
            }
        });
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    createCameraSource(selectedModel);
    startCameraSource();
  }

  /** Stops the camera. */
  @Override
  protected void onPause() {
    super.onPause();
    preview.stop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
    }
  }

  private String[] getRequiredPermissions() {
    try {
      PackageInfo info =
          this.getPackageManager()
              .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
      String[] ps = info.requestedPermissions;
      if (ps != null && ps.length > 0) {
        return ps;
      } else {
        return new String[0];
      }
    } catch (Exception e) {
      return new String[0];
    }
  }

  private boolean allPermissionsGranted() {
    for (String permission : getRequiredPermissions()) {
      if (!isPermissionGranted(this, permission)) {
        return false;
      }
    }
    return true;
  }

  private void getRuntimePermissions() {
    List<String> allNeededPermissions = new ArrayList<>();
    for (String permission : getRequiredPermissions()) {
      if (!isPermissionGranted(this, permission)) {
        allNeededPermissions.add(permission);
      }
    }

    if (!allNeededPermissions.isEmpty()) {
      ActivityCompat.requestPermissions(
          this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
    }
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, String[] permissions, int[] grantResults) {
    Log.i(TAG, "Permission granted!");
    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private static boolean isPermissionGranted(Context context, String permission) {
    if (ContextCompat.checkSelfPermission(context, permission)
        == PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "Permission granted: " + permission);
      return true;
    }
    Log.i(TAG, "Permission NOT granted: " + permission);
    return false;
  }
}
