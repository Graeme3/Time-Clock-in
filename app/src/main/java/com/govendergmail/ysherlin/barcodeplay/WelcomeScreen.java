package com.govendergmail.ysherlin.barcodeplay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeScreen extends AppCompatActivity
{
    //Create UI variables
    private Button btnBarcodeReaderRef;
    private Button btnBarcodeGenerateRef;
    private Button btnHelpFilesRef;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        //Attach references
        btnBarcodeReaderRef = (Button)findViewById(R.id.btnCheckIn);
        btnBarcodeGenerateRef = (Button)findViewById(R.id.btnNewEmp);
        btnHelpFilesRef = (Button)findViewById(R.id.btnEditEmp);
        //Set actions for barcode reader button
        btnBarcodeReaderRef.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent BarcodeReader = new Intent(view.getContext(), BarcodeReader.class);
                startActivity(BarcodeReader);
            }
        });
        //Set actions for barcode generator button
        btnBarcodeGenerateRef.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent BarcodeGenerater = new Intent(view.getContext(), BarcodeGenerator.class);
                startActivity(BarcodeGenerater);
            }
        });
        //Set actions for help files button
       /* btnHelpFilesRef.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent Help = new Intent(view.getContext(), );
                startActivity(Help);
            }
        });*/
    }
}
