package com.ReDetect.redetect;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.ReDetect.redetect.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    JavaCameraView javaCameraView;
    Mat mRgba;
    BaseLoaderCallback mLoaderCallback;
    public static final String TAG = "MainActivity";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS= 7;
    AlertDialog alertDialog;
    boolean isFirstRun;

    /*
    Ensures permissions are allowed.
    Sets layout.
    If first time run or user has not accepted the terms, shows terms that user must agree to.
    Initalzies Camera.
    Stores SVM in internal storage.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        checkAndRequestPermissions();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        terms();

        javaCameraView = (JavaCameraView) findViewById(R.id.javaCameraView);
        javaCameraView.setMaxFrameSize(600,600);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);

        mLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status){
                super.onManagerConnected(status);
                switch(status){
                    case BaseLoaderCallback.SUCCESS:
                        javaCameraView.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;

                }
            }
        };

        storeFiletoInternalStorage("bottle-22", "bottle-22.svm");
        storeFiletoInternalStorage("cans-18", "cans-18.svm");
        storeFiletoInternalStorage("cartons-9", "cartons-9.svm");
        storeFiletoInternalStorage("gatorade-9", "gatorade-9.svm");
        storeFiletoInternalStorage("glass-11", "glass-11.svm");
        storeFiletoInternalStorage("jugs-6", "jugs-6.svm");
        storeFiletoInternalStorage("soda-11", "soda-11.svm");
        storeFiletoInternalStorage("tall-sodas-11", "tall-sodas-11.svm");
        storeFiletoInternalStorage("paper-8", "paper-8.svm");
        storeFiletoInternalStorage("detergent-8", "detergent-8.svm");
        storeFiletoInternalStorage("egg_shells-7", "egg_shells-7.svm");
        storeFiletoInternalStorage("foil-11", "foil-11.svm");
        storeFiletoInternalStorage("burrito-2", "burrito-2.svm");
	//Hiding rest.
	
	//Displays tip for first time use.
        if(isFirstRun == false){
            Toast.makeText(MainActivity.this, "Please take a clear picture of the object. The app works best if you detect one object at a time.", Toast.LENGTH_LONG).show();
        }

    }

    /*
    Displays Terms & Conditions and Privacy Policy.
    Called when the app is first run or if user has not accepted the terms.
    Exits app if user does not accept terms.
    */
    public void terms(){
        isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if(isFirstRun){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Terms & Conditions and Privacy Policy")
                    .setMessage("Please read through the Terms & Conditions and the Privacy Policy. In order to use the app, you must accept the Terms & Conditions and the Privacy Policy by clicking ACCEPT.")
                    .setNeutralButton("TERMS & CONDITIONS and PRIVACY POLICY", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int a){
                            Intent intent = new Intent(MainActivity.this, Note.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Decline", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i){
                            finish();
                            System.exit(0);
                        }
                    })
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which){
                        getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                            .edit()
                            .putBoolean("isFirstRun", false)
                            .apply();
                            Toast.makeText(MainActivity.this, "Take a look at the tips before you use the app. The app works best if you detect one object at a time.", Toast.LENGTH_LONG).show();
                        }
                    }).show();
        }
    }

    /*
    Takes in String 'resourceId" and String 'resourceName'.
    Find resource with identifier 'resourceId'.
    If resource with identifier 'resourceId' found, write with identifier 'resourceName'.
    */
    private void storeFiletoInternalStorage(String resourceId, String resourceName){
        try{
            InputStream in = getResources().openRawResource(
                    getResources().getIdentifier(resourceId,
                            "raw", getPackageName()));

            FileOutputStream out = null;
            out = openFileOutput(resourceName, Context.MODE_PRIVATE);
            byte[] buff = new byte[1024];
            int read = 0;
            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    Returns true if write, read, and camera permissions are allowed.
    If one or more permissions are not allowed, return false.
    */
    private boolean checkAndRequestPermissions(){
        int writePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
	if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(MainActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    /*

    */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("in fragment on request", "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();

                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
		perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

                //Fill 'perms' with user allowed permissions.
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    //Check for permissions.
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("in fragment on request", "WRITE_EXTERNAL_STORAGE & READ_EXTERNAL_STORAGE & CAMERA permission granted");
                        //Else when one or more permissions not granted.
                    } else {
                        Log.d("in fragment on request", "Some permissions are not granted.");

                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showDialogOK("Camera and Storage Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }

    }

    /*
    Shows ok and cancel dialog with custom message.
    */
    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    /*
    Ensure OpenCV
    */
    @Override
    protected void onResume(){
        super.onResume();

        if(OpenCVLoader.initDebug()){
            Log.i(TAG, "Opencv worked!");
            mLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
        else{
            Log.i(TAG, "Opencv did not work");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0,this,mLoaderCallback);
        }
    }

    /*
    Detach camera.
    */
    @Override
    protected void onPause(){
        super.onPause();
        if(javaCameraView!=null)
            javaCameraView.disableView();
    }

    /*
    Save image to External Files Directory.
    */
    private void saveImage(Mat subImg){

        Bitmap bmp=null;

        try{
            bmp = Bitmap.createBitmap(subImg.cols(), subImg.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(subImg, bmp);
        } catch (CvException e){
            Log.d(TAG, e.getMessage());
        }

        subImg.release();

        FileOutputStream out = null;

        String filename = "frame.png";

        File sd = new File(getExternalFilesDir(null) + "/frames");
        boolean success = true;

        if(!sd.exists()){
            success = sd.mkdir();
            //Toast.makeText(MainActivity.this, "frame folder made", Toast.LENGTH_LONG).show();
        }

        if(success){
            File dest = new File(sd,filename);

            try{
                out = new FileOutputStream(dest);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out);

            } catch (Exception e){
                e.printStackTrace();
                Log.d(TAG,e.getMessage());
            } finally{
                try{
                    if(out != null){
                        out.close();
                        Log.d(TAG, "OK!");
                    }
                } catch(IOException e){
                    Log.d(TAG,e.getMessage() + "Error");
                    e.printStackTrace();
                }
            }
        }
    }

    /*
    Goes to Detail Activity, where reyclable objects are boxed.
    */
    public void onClickGo(View view){
        saveImage(mRgba);
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
        finish();
    }

    /*
    Goes to note from creator.
    */
    public void showNote(View view){
        Intent intent = new Intent(this, Note2.class);
        startActivity(intent);
        finish();
    }

    /*
    Goes to camera tips.
    */
    public void showTips(View view){
        Intent intent = new Intent(this, Tips.class);
        startActivity(intent);
        finish();
    }

    /*
    Goes to recycling tips.
    */
    public void showRTips(View view){
        Intent intent = new Intent(this, Rtips.class);
        startActivity(intent);
        finish();
    }

    /*
    Neccessary camera functions
    */
    @Override
    public void onCameraViewStarted(int width, int height){
        mRgba = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped(){
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        mRgba = inputFrame.rgba();

        return mRgba;
    }

    /*
    Detaches camera.
    */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView!=null){
            javaCameraView.disableView();
        }
    }

}
