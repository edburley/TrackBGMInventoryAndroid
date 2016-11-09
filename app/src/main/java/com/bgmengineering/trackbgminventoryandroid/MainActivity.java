package com.bgmengineering.trackbgminventoryandroid;

import android.content.Intent;
import android.os.AsyncTask;
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


public class MainActivity extends AppCompatActivity
{
    private TextView textViewFirst;
    private TextView textViewSecond;
    private IntentIntegrator integrator;
    private String First_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewFirst = (TextView) findViewById(R.id.textViewFirst);
        textViewSecond = (TextView) findViewById(R.id.textViewSecond);
        integrator = new IntentIntegrator(this);
        integrator.setBeepEnabled(true);

        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        textViewFirst.setText(scanResult.getContents().toString());
        First_ID = scanResult.getContents().toString().substring(3);
        new GetKitInfo().execute();
    }

    private class GetKitInfo extends AsyncTask<Void, Void, Void>
    {
        String kitName;
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
            textViewSecond.setText(kitName);
        }
    }
}
