package za.co.salamandra.timeclock;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Handler;

import javax.net.ssl.HttpsURLConnection;

public class Employee extends AppCompatActivity {

    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtEmail;
    private EditText txtCellNumber;
    private ImageButton imgProfilePicture;
    private Button btnSaveEmployee;
    private ImageView imgBarcode;
    private EditText companyName,companyNumber;
    private String strCompanyName, strCompanyContact, image_file;
    final int CAMERA_CAPTURE = 1;
    final int CROP_PIC = 2;
    private Uri picUri;
    String server_response;
    //preferences
    SharedPreferences preferences;
    public static final String MyPREFERENCES = LoginActivity.MyPREFERENCES;
    private String apikey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        //Check if user has a company
        preferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        apikey = preferences.getString("apikey", null);
        //check if company exists
        new checkForCompany().execute();
        //UI Elements
        txtFirstName = (EditText) findViewById(R.id.edFirstName);
        txtLastName = (EditText) findViewById(R.id.edLastName);
        txtEmail = (EditText) findViewById(R.id.edEmail);
        txtCellNumber = (EditText) findViewById(R.id.edCellNumber);
        btnSaveEmployee = (Button) findViewById(R.id.btnSave);
        imgProfilePicture = (ImageButton) findViewById(R.id.imageButton);
        imgBarcode = (ImageView) findViewById(R.id.barcodeView);
        btnSaveEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new createEmployee().execute();
            }
        });
        imgProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.imageButton) {
                    try {
                        // Intent will handle the capture of image
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //the return data will be handle by onActivivtyResults
                        startActivityForResult(captureIntent, CAMERA_CAPTURE);
                    } catch (ActivityNotFoundException anfe) {
                        Toast toast = Toast.makeText(getApplicationContext(), "This device doesn't support the crop action!",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        Log.v("MyActivity", anfe.getMessage());
                    }
                    setImage_file();
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE) {
                // get the Uri for the captured image
                picUri = data.getData();
                performCrop(data);
            }
            // user is returning from cropping the image
            else if (requestCode == CROP_PIC) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
                ImageView picView = (ImageView) findViewById(R.id.imageButton);
                picView.setImageBitmap(thePic);
            }
        }
    }

    private void performCrop(Intent data) {
        //take care for exceptions
        try {
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
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
            Bitmap thePic = (Bitmap) data.getExtras().get("data");
            ImageView picView = (ImageView) findViewById(R.id.imageButton);
            picView.setImageBitmap(thePic);
        }
    }

    class checkForCompany extends AsyncTask<Void, Void, Boolean> {

        private Exception exception;

        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL("http://api.salamandra.co.za/v1/company");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("api", apikey);
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(conn.getInputStream());
                    Log.v("CatalogClient", server_response);

                } else {
                    server_response = null;
                    return false;
                }
                JSONObject login = new JSONObject(server_response);
                boolean error = login.getBoolean("error");
                if (!error) {
                    return true;
                }
            } catch (MalformedURLException mal) {
                mal.printStackTrace();
                return false;
            } catch (IOException io) {
                io.printStackTrace();
                return false;
            } catch (NetworkOnMainThreadException nomte) {
                nomte.printStackTrace();
                return false;
            } catch (Exception err) {
                err.printStackTrace();
                return false;
            }
            return true;
        }

        protected void onPostExecute(Boolean success) {
            if(!success) {
                openDialog();
            }
        }
    }


    public void openDialog() {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle("Create a company");

        companyName = (EditText)dialog.findViewById(R.id.companyName);
        companyNumber = (EditText) dialog.findViewById(R.id.companyNumber);
        Button btnSave = (Button) dialog.findViewById(R.id.save);
        Button btnCancel = (Button) dialog.findViewById(R.id.cancel);
        dialog.show();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.hide();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strCompanyName = companyName.getText().toString();
                strCompanyContact = companyNumber.getText().toString();
                new createCompany().execute();
                dialog.hide();
            }
        });
    }


    //Converting InputStream to string

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (Exception io) {
            io.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    public class createCompany extends AsyncTask<Void, Void, Boolean> {

        private Exception exception;

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url;
            HttpURLConnection conn = null;
            try {
                //create connection
                url = new URL("http://api.salamandra.co.za/v1/company");
                //you need to encode ONLY the values of the parameters
                String param = "company_name=" + strCompanyName + "&company_contact=" + strCompanyContact;

                conn = (HttpURLConnection) url.openConnection();
                //set the output to true, indicating you are outputting(uploading)POST data
                conn.setDoOutput(true);
                //once you set the output to true, you don't really need to set the request method to post
                conn.setRequestMethod("POST");
                //Android documentation suggested that you set the length of the data you are sending to the server, BUT
                // do NOT specify this length in the header by using conn.setRequestProperty(“Content-Length”, length);
                //use this instead.
                conn.setFixedLengthStreamingMode(param.getBytes().length);
                conn.setRequestProperty("api", apikey);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                //send the POST out
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(param);
                out.close();

                //build the string to store the response text from the server
                String response = "";

                //start listening to the stream
                Scanner inStream = new Scanner(conn.getInputStream());

                //process the stream and store it in StringBuilder
                while (inStream.hasNextLine()){
                    response += (inStream.nextLine());
                }
                //Log.v("MyActivity", param);
                //Log.v("MyActivity", response);
                JSONObject login = new JSONObject(response);
                boolean error = login.getBoolean("error");
                if(!error) {
                    return true;
                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (Exception err) {
                err.printStackTrace();
                return false;
            }
            return false;
        }

        protected void onPostExecute(Boolean success) {
            if(success) {
                Toast.makeText(Employee.this, "Company created!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class createEmployee extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                //create connection
                URL url = new URL("http://api.salamandra.co.za/v1/employee");
                //you need to encode ONLY the values of the parameters
                String param = "first_name=" + txtFirstName + "&last_name=" + txtLastName +
                        "&cell_num=" + txtCellNumber + "&email_address=" + txtEmail + "&photo=" +
                        getPhotoAsByte() + "&barcode=" + randomString() ;

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //set the output to true, indicating you are outputting(uploading)POST data
                conn.setDoOutput(true);
                //once you set the output to true, you don't really need to set the request method to post
                conn.setRequestMethod("POST");
                //Android documentation suggested that you set the length of the data you are sending to the server, BUT
                // do NOT specify this length in the header by using conn.setRequestProperty(“Content-Length”, length);
                //use this instead.
                conn.setFixedLengthStreamingMode(param.getBytes().length);
                conn.setRequestProperty("api", apikey);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                //send the POST out
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(param);
                out.close();

                //build the string to store the response text from the server
                String response = "";

                //start listening to the stream
                Scanner inStream = new Scanner(conn.getInputStream());

                //process the stream and store it in StringBuilder
                while (inStream.hasNextLine()){
                    response += (inStream.nextLine());
                }
                //Log.v("MyActivity", param);
                //Log.v("MyActivity", response);
                JSONObject login = new JSONObject(response);
                boolean error = login.getBoolean("error");
                if(!error) {
                    return true;
                }
            } catch (MalformedURLException mal) {
                mal.printStackTrace();
                return false;
            } catch (IOException io) {
                io.printStackTrace();
                return false;
            } catch (Exception err) {
                err.printStackTrace();
                return false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success) {
                Toast.makeText(Employee.this, "Employee " + txtFirstName.getText().toString() + " " +
                            txtLastName.getText().toString() + " created!", Toast.LENGTH_LONG ).show();
                txtFirstName.setText("");
                txtLastName.setText("");
                txtEmail.setText("");
                txtCellNumber.setText("");
            } else {
                Toast.makeText(Employee.this, "Error in creating a new employee!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String randomString() {
        char[] chars = "abcdefghijklmnopqrstuwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < 8; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        return output;
    }

    public byte[] getPhotoAsByte() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable)imgProfilePicture.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();

    }

    public Bitmap getImage_fileinBitmap() {
        
    }

    public void setImage_file() {

        Bitmap bitmap = ((BitmapDrawable)imgProfilePicture.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] data = bos.toByteArray();
        String file = Base64.encodeToString(data, 0);
        this.image_file = file;
    }

}
