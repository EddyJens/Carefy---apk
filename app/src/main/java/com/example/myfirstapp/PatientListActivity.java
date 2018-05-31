package com.example.myfirstapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PatientListActivity extends AppCompatActivity {

    private ListView listView;

    ArrayList<HashMap<String, String>> patientList;

    String User_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        patientList = new ArrayList<>();
        listView = findViewById(R.id.list_view);

        User_id = MainActivity.USER_ID;

        new SendPostRequest().execute();

    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {}

        protected String doInBackground(String... arg0) {

            String response, test;

            try {
                URL url = new URL("http://globalbombas.com.br/prosel_carefy/Mobile/get_patients");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("user_id", User_id);//arrumar, ele deve vir desde o login
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
                    try {
                        JSONObject jsonObj = new JSONObject(response);

                        JSONObject database = jsonObj.getJSONObject("database");

                        //getting JSON Array node
                        JSONArray patients = database.getJSONArray("patients");

                        //looping through all patients
                        for (int i = 0; i < patients.length(); i++) {
                            JSONObject p = patients.getJSONObject(i);
                            String name = "Patient: " + p.getString("name");
                            String hospital = "Hospital: " + p.getString("hospital");

                            //tmp hash map for single patient
                            HashMap<String, String> patient = new HashMap<>();

                            // adding each child node to HashMap key => value
                            patient.put("name", name);
                            patient.put("hospital", hospital);

                            //adding contact to contact list
                            patientList.add(patient);
                        }
                    } catch(final JSONException e){
                        return new String("Json parsing error: " + e.getMessage());
                    }

                    return response;
                } else {
                    return new String("false : "+responseCode);
                }

            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {

            ListAdapter adapter = new SimpleAdapter(
                    PatientListActivity.this, patientList,
                    R.layout.list_item, new String[]{ "name","hospital"},
                    new int[]{R.id.name, R.id.hospital});
            listView.setAdapter(adapter);
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