package com.nishan.textrecognitionapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView textView;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button captureImageButton = findViewById(R.id.capture_image);
        Button detectTextButton = findViewById(R.id.detect_image);
        Button btnGallery = findViewById(R.id.btn_gallery);
        imageView = findViewById(R.id.image_view);
        textView = findViewById(R.id.text_display);

        btnGallery.setOnClickListener(v -> checkGalleryPermission());

        captureImageButton.setOnClickListener(v -> {
            dispatchTakePictureIntent();
            textView.setText("");
        });

        detectTextButton.setOnClickListener(v -> detectTextFromImage());



    }


    // Take a photo with a camera app
    // https://developer.android.com/training/camera/photobasics#TaskCaptureIntent
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // https://developer.android.com/training/camera/photobasics#TaskPhotoView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void detectTextFromImage() {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(this::displayTextFromImage).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error occurred: "+e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
        if (blockList.size() == 0){
            Toast.makeText(this, "There is no test to detect", Toast.LENGTH_SHORT).show();
        }
        else {
            StringBuilder text = new StringBuilder();
            for(FirebaseVisionText.Block block : firebaseVisionText.getBlocks()){
                text.append(block.getText()).append("\n");
            }
            textView.setText(text.toString());
        }
    }

    private void checkGalleryPermission() {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            switchGalleryActivity();
        }
    }

    private void switchGalleryActivity() {
        Intent i = new Intent(MainActivity.this, GalleryActivity.class);
        startActivity(i);
    }
}