package com.ReDetect.redetect;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ReDetect.redetect.R;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.cvtColor;

public class DetailActivity extends AppCompatActivity{
    ImageView imageView;
    Button btnProcess;
    Bitmap bmpInput, bmpOutput;
    Mat matInput, matOutput;

    static{
        System.loadLibrary("MyLibs");
    }
    
    /*
    Sets layout.
    Retrieves Bitmap of photo taken - 'frame.png' - into 'bmpInput'.
    Sets screen as 'bmpInput'    
    When user presses button to process, converts 'bmpInput' to Mat to add boxes; converts Mat with processing back to Bitmap called bitmap 'bmpOutput'.
    Sets screen as 'bmpOutput'.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageView = (ImageView)findViewById(R.id.imageView);
        btnProcess = (Button)findViewById(R.id.btnProcess);

        String photoPath = getExternalFilesDir(null) + "/frames/frame.png";

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bmpInput = BitmapFactory.decodeFile(photoPath, options);

        imageView.setImageBitmap(bmpInput);
	
	//Display bitmap; convert bitmap to mat to process.
	//matOutput will have the recyclables boxed or message indicating no recyclables.
        matInput = convertBitMapToMat(bmpInput);
        matOutput = new Mat(matInput.rows(), matInput.cols(), CvType.CV_8UC3);

        btnProcess.setOnClickListener((v) -> {
            Toast.makeText(DetailActivity.this, "Detecting!", Toast.LENGTH_SHORT).show();
	    
	    //C++
            NativeClass.LandmarkDetection(matInput.getNativeObjAddr(), matOutput.getNativeObjAddr());

            bmpOutput = convertMatToBitmap(matOutput);
            imageView.setImageBitmap(bmpOutput);

        });
    }

    /*
    Takes in Mat 'img'.
    Returns 'img' converted to Bitmap.
    */
    Bitmap convertMatToBitmap(Mat img){
        int width = img.width();
        int height = img.height();

        Bitmap bmp = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Mat tmp = img.channels() == 1 ? new Mat(width,height,CvType.CV_8UC1,new Scalar(1)): new Mat(width,height,CvType.CV_8UC3,new Scalar(3));

        try{
	    //color
            if (img.channels() == 3) cvtColor(img, tmp, Imgproc.COLOR_RGB2BGRA);
            else if(img.channels() == 1) cvtColor(img, tmp, Imgproc.COLOR_GRAY2RGBA);
            Utils.matToBitmap(tmp, bmp);
        }
        catch (CvException e){
            Log.d("Exception", e.getMessage());
        }
        return bmp;
    }

    /*
    Takes in Bitmap 'rbgaImage'
    Returns 'rgbaImage' converted to Mat.
    */
    Mat convertBitMapToMat(Bitmap rbgaImage){
	//a is alpha channel.
        Mat rgbaMat = new Mat(rbgaImage.getHeight(), rbgaImage.getWidth(), CvType.CV_8UC4);
        Bitmap bmp32 = rbgaImage.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, rgbaMat);
	
	//Convert just to rgb.
        Mat rgbMat = new Mat(rbgaImage.getHeight(), rbgaImage.getWidth(), CvType.CV_8UC3);
        cvtColor(rgbaMat,rgbMat,Imgproc.COLOR_RGBA2BGR,3);
        return rgbMat;
    }

    /*
    Goes back to MainActivity, which has the camera utility.
    */
    public void onClickGo(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
