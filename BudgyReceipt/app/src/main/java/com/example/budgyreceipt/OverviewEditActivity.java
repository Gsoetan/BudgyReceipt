package com.example.budgyreceipt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;

import static com.example.budgyreceipt.MainCalculations.stringParse;
import static com.example.budgyreceipt.MainCalculations.getArrayIndex;
// If you can get to it, add something so that if not all information was filled - a prompt appears letting the user know this and that they will have to supplement the missing info
public class OverviewEditActivity extends AppCompatActivity {

    private ReceiptDatabase mReceiptDb;
    private long mOverviewId;
    private Overview mOverview;
    private Receipt mReceipt;

    private EditText oTotalEt, oSubTotalEt, oPaymentEt;
    private TextView oDateEt, oTitleEt;
    private ImageView oPhotoIv;
    private Spinner tags;
    private FloatingActionButton save;

    private int year, month, day; // for setting the calendar date

    public static final String EXTRA_RECEIPT_ID = "com.example.budgyreceipt.receipt_id";
    public static final String EXTRA_OVERVIEW_ID = "com.example.budgyreceipt.overview_id";

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String[] resource_tags; // resource array of tags
    String[] cameraPermission;
    String[] storagePermission;
    String[] entries;

    Uri image_uri; // image source

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_overview);

        oTitleEt = findViewById(R.id.merchant);
        oDateEt = findViewById(R.id.date);
        oTotalEt = findViewById(R.id.total);
        oSubTotalEt = findViewById(R.id.subTotal);
        tags = findViewById(R.id.tag); //https://developer.android.com/guide/topics/ui/controls/spinner.html
        oPaymentEt = findViewById(R.id.payment);
        oPhotoIv = findViewById(R.id.photo);

        resource_tags = getResources().getStringArray(R.array.tags);

        mReceiptDb = ReceiptDatabase.getInstance(getApplicationContext());

        // Get overview from OverviewActivity
        Intent intent = getIntent();
        mOverviewId = intent.getLongExtra(EXTRA_OVERVIEW_ID, -1);

        if (mOverviewId == -1) {
            // create dummy overview
            mOverview = new Overview();
            setTitle("Edit Overview");
        } else {
            mOverview = mReceiptDb.overviewDao().getOverview(mOverviewId);
            mReceipt = mReceiptDb.receiptDao().getReceipt(mOverviewId);
            oTitleEt.setText(mReceipt.getMerchant());
            oDateEt.setText(mOverview.getDate());
            oTotalEt.setText(mOverview.getTotal());
            oSubTotalEt.setText(mOverview.getSubtotal());
            oPaymentEt.setText(mOverview.getPayment());
        }

        long receiptId = intent.getLongExtra(EXTRA_RECEIPT_ID, 0);
        mOverview.setReceiptId(receiptId);


        save = findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOverview();
            }
        });


        // Set listener to the date text box so that when clicked it brings up a dialogue box to chnage the date
        oDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(OverviewEditActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = month + "/" + day + "/" + year;
                        oDateEt.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });


        //camera permissions
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //storage permission
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tags, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tags.setAdapter(adapter);
        tags.setSelection(getArrayIndex(resource_tags, mOverview.getTag())); // set selection after adapter to avoid default selection of the first tag
    }


    private void saveOverview() {
        mOverview.setDate(oDateEt.getText().toString());
        mOverview.setTotal(oTotalEt.getText().toString());
        mOverview.setSubtotal(oSubTotalEt.getText().toString());
        mOverview.setTag(tags.getSelectedItem().toString());
        mOverview.setPayment(oPaymentEt.getText().toString());

        mReceipt.setTotal(oTotalEt.getText().toString()); // set total to new total

        mReceiptDb.overviewDao().updateOverview(mOverview);
        mReceiptDb.receiptDao().updateReceipt(mReceipt);

        Intent intent = new Intent();
        intent.putExtra(EXTRA_OVERVIEW_ID, mOverview.getId());
        setResult(RESULT_OK, intent);
        finish(); // end activity so user cannot go back (glitch prevention)
    }


    public void addPhoto(View view) {
        showImageImportDialog();
    }

    private void showImageImportDialog(){
        //items to display in dialog
        String [] items = {"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        // Set title
        dialog.setTitle("Select source");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //camera option clicked
                    checkCameraPermission(); //check if camera permission allowed
                    pickCamera();
                }
                if (which == 1) {
                    //gallery option clicked
                    checkStoragePermission(); //check if storage permission allowed
                    pickGallery();
                }
            }
        });
        dialog.create().show(); // show dialog
    }

    private void pickGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK); //
        //set intent to open up your images
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues values = new ContentValues(); // https://developer.android.com/guide/topics/providers/content-providers
        values.put(MediaStore.Images.Media.TITLE, "New Picture"); //title of picture
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image converted to text"); // description
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void checkStoragePermission() { // check to see if user has granted application access to photos on device
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != (PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
        }
        return;
    }

    private void checkCameraPermission() { // check to see if user has granted permission for application to take photos using camera as well as permission to save it (more for camera option)
        boolean camPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        if (camPermission != writePermission){
            ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
        }
        return;
    }

    //handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { // https://stackoverflow.com/questions/52502453/import-image-from-camera-or-gallery-app-crash
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickGallery();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    //handle image result
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //got image from gallery, now crop it
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON) // enable image guidelines
                        .start(this);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //got image from camera, now crop it
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON) // enable image guidelines
                        .start(this);
            }
        }
        //get cropped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri(); //get image uri
                oPhotoIv.setImageURI(resultUri); //set image to image view

                //make drawable bitmap for text recognition
                BitmapDrawable bitmapDrawable = (BitmapDrawable) oPhotoIv.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational()) { // if there was a problem with the recognizer return an error
                    Toast.makeText(this, "there was an error", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    //get text for sb until there is nothing left to read
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        sb.append("\n");
                    }
                    //set all entries to their respective values
                    entries = stringParse(sb); //returns a array in format {date, payment, total, subtotal}
                    oDateEt.setText(entries[0]);
                    oPaymentEt.setText(entries[1]);
                    oTotalEt.setText(entries[2]);
                    oSubTotalEt.setText(entries[3]);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) { // if there is a problem with the crop tool
                //if there is any error show it
                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}