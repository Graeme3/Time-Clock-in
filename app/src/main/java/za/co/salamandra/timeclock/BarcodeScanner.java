package za.co.salamandra.timeclock;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BarcodeScanner extends AppCompatActivity {

    private Button btnScan;
    private TextView txtBarcodeMain;
    private TextView txtBarcodeDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
        //UI variables
        btnScan = (Button)findViewById(R.id.btnScan);
        txtBarcodeMain = (TextView)findViewById(R.id.txtBarcodeType);
        txtBarcodeDescription = (TextView)findViewById(R.id.txtBarcodeText);
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
        //action
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Read();
            }
        });

    }
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

            txtBarcodeMain.setText(ScanFormat);
            txtBarcodeDescription.setText(ScanContent);
        }
        else
        {
            Toast ErrorMessage = Toast.makeText(getApplicationContext(), "No Scan Data Received... ", Toast.LENGTH_LONG);
            ErrorMessage.show();
        }
    }
}
