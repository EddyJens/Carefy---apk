package com.example.myfirstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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

public class MainActivity extends Activity {

    EditText username, password;
    String Username, Password, Status, User_id;

    public static String USER_ID = "com.example.myfirstapp.USER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the reference to the views
        username = findViewById(R.id.editText2);
        password = findViewById(R.id.editText3);

        final Button button = findViewById(R.id.button6);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                login(v);
            }
        });

    }

    public void login(View view){

        Username = username.getText().toString();
        Password = password.getText().toString();

        new SendPostRequest().execute();
    }

    public void validate(String confirmation){

        if(confirmation.equals("200")){
            Intent intent = new Intent(this, LoginActivity.class);
            USER_ID = User_id;
            startActivity(intent);
        }
        else
            Toast.makeText(getApplicationContext(), "Invalid password or username", Toast.LENGTH_LONG).show();

    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {}

        protected String doInBackground(String... arg0) {

            String response;

            try {
                URL url = new URL("http://globalbombas.com.br/prosel_carefy/Mobile/login");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("username", Username);
                postDataParams.put("password", Password);
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

                        //getting useful data
                        response = sb.toString();

                        JSONObject jsonobj = new JSONObject(response);
                        Status = jsonobj.getString("status");
                        if(Status == "500"){
                            User_id = null;
                            return null;
                        }else{
                            User_id = jsonobj.getString("user_id");
                        }

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
            validate(Status);
        }
    }

    public String getPostDataString(JSONObject params) throws Exception{

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

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
