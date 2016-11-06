package com.govendergmail.ysherlin.barcodeplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BarcodeReader extends Activity
{
    //Create UI variables
    private Button btncCancel;
    private TextView txtBarcodeFormatRef;
    private TextView txtBarcodeTextRef;
    //Create operating variables
    //private TextToSpeech TextToSpeehRef;
    //private String Output;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);
        Read();
        //Attach references
//        TextToSpeehRef = new TextToSpeech(this, this);
        btncCancel = (Button) findViewById(R.id.btnCancel);
        txtBarcodeFormatRef = (TextView)findViewById(R.id.txtBarcode);
        txtBarcodeTextRef = (TextView)findViewById(R.id.txtEmpFName);
        //Actions for Scan button
        btncCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent Back = new Intent(v.getContext(),WelcomeScreen.class);
                startActivity(Back);
            }
        });
    }

//    @Override
//    public void onInit(int status)
//    {
//        if (status == TextToSpeech.SUCCESS)
//        {
//            int result = TextToSpeehRef.setLanguage(Locale.US);
//            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
//            {
//                Log.e("TTS", "This Language is not supported");
//            }
//            else
//            {
//                btnScanBarcodeRef.setEnabled(true);
//            }
//        }
//        else
//        {
//            Log.e("TTS", "Initilization Failed!");
//        }
//    }

//    //Stop text to speech
//    @Override
//    public void onDestroy()
//    {
//        // Don't forget to shutdown tts!
//        if (TextToSpeehRef != null)
//        {
//            TextToSpeehRef.stop();
//            TextToSpeehRef.shutdown();
//        }
//        super.onDestroy();
//    }

    //Scan a barcode
    public void Read()
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    //Pull result of barcode to variables
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null)
        {
            String ScanContent = scanResult.getContents();
            //Check the database after barcode has been scanned
            GetEmpData(ScanContent);
//            String ScanFormat = scanResult.getFormatName();
//            //Convert Barcode text to speech
//            Output = ScanContent.toString();
//            TextToSpeehRef.speak("The Barcode Reads " + Output, TextToSpeech.QUEUE_FLUSH, null);
            //Write barcode information to text

        }
        else
        {
            Toast ErrorMessage = Toast.makeText(getApplicationContext(), "No Scan Data Received... ", Toast.LENGTH_LONG);
            ErrorMessage.show();
        }
    }
    //Gets and Sets employee data from the database
    private void GetEmpData(String scanContent)
    {
        txtBarcodeFormatRef.setText("Employee Code : " + scanContent);
//        txtBarcodeTextRef.setText("Format: " + ScanFormat);
    }
}
