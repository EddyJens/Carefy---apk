package com.example.myfirstapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class PatientRemovalActivity extends AppCompatActivity {

    EditText patient_id;
    String Patient_id, User_id, Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_removal);

        //get the reference to the views
        patient_id = findViewById(R.id.editText4);

        User_id = MainActivity.USER_ID;

        final Button button = findViewById(R.id.button35);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                remove(v);
            }
        });

    }

    public void remove(View view){

        Intent intent = new Intent(this, PatientRemovalActivity.class);

        Patient_id = patient_id.getText().toString();

        new SendPostRequest().execute();

        startActivity(intent);

    }

    public void validate(String confirmation){

        if(confirmation.equals("200")){
            Toast.makeText(getApplicationContext(), "patient removed", Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(getApplicationContext(), "error removing patient", Toast.LENGTH_LONG).show();

    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {}

        protected String doInBackground(String... arg0) {

            String response;

            try {
                URL url = new URL("http://globalbombas.com.br/prosel_carefy/Mobile/mobile_remove_patient");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("user_id", User_id);//arrumar, deve acompanhar desde o login
                postDataParams.put("patient_id", Patient_id);
                Log.e("params", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line ="";

                    while((line = in.readLine()) != null){
                        sb.append(line);
                        break;
                    }

                    in.close();

                    response = sb.toString();

                    JSONObject jsonobj = new JSONObject(response);
                    Status = jsonobj.getString("status");

                    return Status;
                } else {
                    return new String("false : "+responseCode);
                }

            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            validate(result);
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }

        return result.toString();
    }
}
