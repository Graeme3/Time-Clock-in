package za.co.salamandra.timeclock;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.onbarcode.barcode.android.AndroidColor;
import com.onbarcode.barcode.android.AndroidFont;
import com.onbarcode.barcode.android.Code128;
import com.onbarcode.barcode.android.IBarcode;

import java.util.Random;

public class BarcodeGenerator extends AppCompatActivity {
    //Create UI variables
    private ImageView imgBarcodeRef;
    private EditText txtInputRef;
    private Button btnGenerateRef;
    //Create operating variables
    private String barcodeText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_generator);
        //UI Elements
        imgBarcodeRef = (ImageView)findViewById(R.id.imgBarcode);
        txtInputRef = (EditText)findViewById(R.id.txtInput);
        btnGenerateRef = (Button)findViewById(R.id.btnGenerate);
        //action
        btnGenerateRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtInputRef.setText(random());
                Check();
            }
        });
    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(6);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
    //Draw the barcode
    public void GenerateBarcode()
    {
        try
        {
            Code128 barcode = new Code128();
            barcode.setData(barcodeText);
            barcode.setProcessTilde(false);
            //Unit of Measure, pixel, cm, or inch
            barcode.setUom(IBarcode.UOM_PIXEL);
            //Barcode bar module width (X) in pixel
            barcode.setX(1f);
            //Barcode bar module height (Y) in pixel
            barcode.setY(75f);
            //Barcode image margins
            barcode.setLeftMargin(10f);
            barcode.setRightMargin(10f);
            barcode.setTopMargin(10f);
            barcode.setBottomMargin(10f);
            //Barcode image resolution in dpi
            barcode.setResolution(600);
            //Display barcode encoding data below the barcode
            barcode.setShowText(true);
            //Barcode encoding data font style
            barcode.setTextFont(new AndroidFont("Arial", Typeface.NORMAL, 12));
            //Space between barcode and barcode encoding data
            barcode.setTextMargin(6);
            barcode.setTextColor(AndroidColor.black);
            //Barcode bar color and background color in Android device
            barcode.setForeColor(AndroidColor.black);
            barcode.setBackColor(AndroidColor.white);
            //Specify your barcode drawing area
            RectF bounds = new RectF(0, 0, 0, 0);
            //Specify your barcode drawing area
            Bitmap bitmap = Bitmap.createBitmap(190, 190, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            barcode.drawBarcode(canvas, bounds);
            imgBarcodeRef.setImageBitmap(bitmap);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //Check the user input
    public void Check()
    {
        try
        {
            if(!txtInputRef.getText().toString().equals(""))
            {
                if(txtInputRef.getText().toString().length() < 13)
                {
                    if(!txtInputRef.getText().toString().contains(" "))
                    {
                        barcodeText = txtInputRef.getText().toString().toUpperCase();
                        GenerateBarcode();
                    }
                    else
                    {
                        Toast.makeText(this, "Must not contain any spaces",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(this, "Too Long",Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(this, "The Input is empty",Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception E)
        {
            E.printStackTrace();
        }
    }
}
