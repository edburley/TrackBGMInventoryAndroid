    package com.bgmengineering.trackbgminventoryandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeView;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.BarcodeView;


public class MainActivity extends AppCompatActivity
{
    private TextView textViewFirst;
    private TextView textViewSecond;
    private IntentIntegrator integrator;
    private String First_ID;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    int permissionCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Assume thisActivity is the current activity
        permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                MY_PERMISSIONS_REQUEST_CAMERA);
        }
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            textViewFirst = (TextView) findViewById(R.id.textViewFirst);
            textViewSecond = (TextView) findViewById(R.id.textViewSecond);
            integrator = new IntentIntegrator(this);
            integrator.setBeepEnabled(true);
            integrator.initiateScan();
        }
        else
        {
            /* No Camera Permission.  Exit app? */
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    permissionCheck = PackageManager.PERMISSION_GRANTED;

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        textViewFirst.setText(scanResult.getContents().toString());
        String type = scanResult.getContents().toString().substring(0,3);
        First_ID = scanResult.getContents().toString().substring(3);

        if(type.equals("KIT")) {
            new GetKitInfo().execute();
        }
        else if (type.equals("LOC"))
        {

        }
        else if (type.equals("INV"))
        {

        }
    }

    private class GetKitInfo extends AsyncTask<Void, Void, Void>
    {
        String kitName;
        String kitDescription;
        String kitStatus;

        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                URL url = new URL("http://www.bgmeng.com/TrackBGMphp/Get_Kit_Info.php?Kit=" + First_ID);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();

                kitName = doc.getElementsByTagName("name").item(0).getTextContent();
                kitDescription = doc.getElementsByTagName("description").item(0).getTextContent();
                kitStatus = doc.getElementsByTagName("status").item(0).getTextContent();
                return null;
            }
            catch(Exception e)
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            textViewSecond.setText(kitName + " - " + kitDescription);
        }
    }
}
