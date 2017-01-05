package com.technoindians.portfolio;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dataappsinfo.viralfame.R;
import com.soundcloud.android.crop.Crop;
import com.technoindians.constants.Constants;
import com.technoindians.database.UpdateOperations;
import com.technoindians.directory.DirectoryList;
import com.technoindians.library.CheckFileType;
import com.technoindians.library.FileOperations;
import com.technoindians.library.ShowLoader;
import com.technoindians.library.UploadFile;
import com.technoindians.library.Utils;
import com.technoindians.network.JsonArrays_;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class CropImageActivity extends Activity {

    private ImageView mImageView, closeButton;
    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST = 0x4;
    static final Integer CAMERA = 0x5;
    private String outputFile;
    private boolean fileSelected = false;
    Toast toast;
    Button selectBtn, uploadBtn;
    private ShowLoader showLoader;
    private UpdateOperations updateOperations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.crop_activity_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Preferences.initialize(getApplicationContext());

        showLoader = new ShowLoader(CropImageActivity.this);
        updateOperations = new UpdateOperations(getApplicationContext());

        selectBtn = (Button) findViewById(R.id.SelectImageBtn);
        uploadBtn = (Button) findViewById(R.id.UploadImageBtn);

        mImageView = (ImageView) findViewById(R.id.crop_activity_profile_photo);

        closeButton = (ImageView) findViewById(R.id.crop_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onBackPressed();
            }
        });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hasWriteContactsPermission = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_CONTACTS);

                    if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXST);
                    } else {
                        ShowToast.toast(getApplicationContext(), "Unable to open storage");
                    }
                } else {
                    fileChooser();
                }
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outputFile.isEmpty() || outputFile == null) {
                    toast = Toast.makeText(getApplicationContext(), "Please Select Image", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 80);
                    toast.show();
                } else {
                    new UploadPhoto().execute();
                }
            }
        });
    }

    private class UploadPhoto extends AsyncTask<Void, Void, HashMap<String, String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader.showUploadingDialog();
        }

        @Override
        protected HashMap<String, String> doInBackground(Void... params) {
            HashMap<String, String> responseMap = UploadFile.
                    uploadFile(outputFile, Preferences.get(Constants.USER_ID));

            int result = Integer.parseInt(responseMap.get(Constants.STATUS));
            if (result == 1) {
                try {
                    JSONObject jsonObject = new JSONObject(responseMap.get(Constants.RESPONSE));
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.PROFILE_PIC);
                    JSONObject object = jsonArray.getJSONObject(0);
                    String profile_pic = object.getString(Constants.PROFILE_PIC);

                    updateOperations.updateProfile(Preferences.get(Constants.USER_ID), Constants.PROFILE_PIC, profile_pic);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return responseMap;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> map) {
            super.onPostExecute(map);
            showLoader.dismissUploadingDialog();
            int result = Integer.parseInt(map.get(Constants.STATUS));
            if (result == 1) {
                String destination = DirectoryList.DIR_MAIN + Preferences.get(Constants.USER_ID) + ".jpg";
                try {
                    FileOperations.copy(new File(outputFile), new File(destination));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Preferences.contains(Constants.LOCAL_PATH);
                Preferences.save(Constants.LOCAL_PATH, destination);
                updateOperations.updateProfile(Preferences.get(Constants.USER_ID), Constants.LOCAL_PATH, destination);
                onBackPressed();
            } else {
                ShowToast.actionFailed(getApplicationContext());
            }
        }
    }


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(CropImageActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CropImageActivity.this, permission)) {
                ActivityCompat.requestPermissions(CropImageActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(CropImageActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            fileChooser();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                //Write external Storage
                case 3:
                    break;
                //Read External Storage
                case 4:
                    fileChooser();
                    break;
                //Camera
                case 5:
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, 12);
                    }
                    break;
            }
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }

    }

    private void fileChooser() {
        fileSelected = false;
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("image/jpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, Constants.INTENT_IMAGE);
        } else {
            Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imageIntent.setType("image/*");
            startActivityForResult(imageIntent, Constants.INTENT_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            mImageView.setImageURI(Crop.getOutput(data));
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.INTENT_IMAGE:
                    String imagePath = Utils.getPath(getApplicationContext(), data.getData());
                    if (CheckFileType.imageFile(imagePath) == 1) {
                        File folder = new File(DirectoryList.DIR_MAIN);
                        if (!folder.exists()) {
                            folder.mkdir();
                        }
                        outputFile = DirectoryList.DIR_MAIN + Preferences.get(Constants.USER_ID) + "_temp" + ".jpg";
                        Crop.of(data.getData(), Uri.fromFile(new File(outputFile))).asSquare().start(CropImageActivity.this);
                    } else {
                        ShowToast.toast(getApplicationContext(), "Please Select Valid Image File");
                    }
                    fileSelected = true;
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (fileSelected == true) {
            PortfolioMainActivity.portfolioMainActivity.changeDp();
            if (new File(outputFile).exists()) {
                new File(outputFile).delete();
            }
            Log.e("onBack","outputFile -> "+outputFile);
        }
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
    }
}