package com.govendergmail.ysherlin.barcodeplay;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BarcodeReader extends Activity
{
    //Create UI variables
    private Button btnScanBarcodeRef;
    private TextView txtBarcodeFormatRef;
    private TextView txtBarcodeTextRef;
    //Create operating variables
    private TextToSpeech TextToSpeehRef;
    private String Output;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);
        //Attach references
//        TextToSpeehRef = new TextToSpeech(this, this);
        btnScanBarcodeRef = (Button) findViewById(R.id.btnScan);
        txtBarcodeFormatRef = (TextView)findViewById(R.id.txtBarcodeType);
        txtBarcodeTextRef = (TextView)findViewById(R.id.txtBarcodeText);
        //Actions for Scan button
        btnScanBarcodeRef.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Read();
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
            String ScanFormat = scanResult.getFormatName();
//            //Convert Barcode text to speech
//            Output = ScanContent.toString();
//            TextToSpeehRef.speak("The Barcode Reads " + Output, TextToSpeech.QUEUE_FLUSH, null);
            //Write barcode information to text
            txtBarcodeTextRef.setText("Format: " + ScanFormat);
            txtBarcodeFormatRef.setText("Content: " +ScanContent);
        }
        else
        {
            Toast ErrorMessage = Toast.makeText(getApplicationContext(), "No Scan Data Received... ", Toast.LENGTH_LONG);
            ErrorMessage.show();
        }
    }
}
