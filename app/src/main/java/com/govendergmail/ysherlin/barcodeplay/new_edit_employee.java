package com.govendergmail.ysherlin.barcodeplay;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Graeme on 2016-11-04.
 */

public class new_edit_employee extends Activity
{
    //Declaring all the widgets used in new_edit_Employee
    //Image will have an onclicklistener to capture/crop and save an image
    ImageView image;
    Button empCheck,empCanel;
    EditText empLname,empFname, empID;
    TextView displayBarcode;
    //Variables used for camera/crop and save images;
    final int CAMERA_CAPTURE = 1;
    final int CROP_PIC = 2;
    private Uri picUri;


    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_edit_employee);
        //Instance the widgets
        image = (ImageView)findViewById(R.id.EmpPic);
        //editText
        empID = (EditText)findViewById(R.id.tbEmpId);
        empFname = (EditText)findViewById(R.id.tbFName);
        empLname = (EditText)findViewById(R.id.tbLName);
        //textView
        displayBarcode = (TextView)findViewById(R.id.lblEmpCode);
        //Buttons
        empCheck = (Button)findViewById(R.id.btnCheck);
        empCanel = (Button)findViewById(R.id.btnCancel);

        //Will need to have sql to save the image of the employee
       image.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v)
           {
               if(v.getId()==R.id.EmpPic)
               {
                   try
                   {
                       // Intent will handle the capture of image
                       Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                       //the return data will be handle by onActivivtyResults
                       startActivityForResult(captureIntent,CAMERA_CAPTURE);
                   }
                   catch (ActivityNotFoundException anfe)
                   {
                       Toast toast = Toast.makeText(getApplicationContext(), "This device doesn't support the crop action!",
                               Toast.LENGTH_SHORT);
                       toast.show();
                   }
               }
           }
       });

    }

   

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE) {
                // get the Uri for the captured image
                picUri = data.getData();
                performCrop();
            }
            // user is returning from cropping the image
            else if (requestCode == CROP_PIC) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
                ImageView picView = (ImageView) findViewById(R.id.EmpPic);
                picView.setImageBitmap(thePic);
            }
        }
    }

    private void performCrop()
    {
        //take care for exceptions
        try
        {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1.5);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        catch (ActivityNotFoundException anfe)
        {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
