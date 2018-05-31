package com.example.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /** Called when the user taps List of patients button */
    public void patientsList(View view){
        Intent intent = new Intent(this, PatientListActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps Patients register */
    public void patientRegister(View view){
        Intent intent = new Intent(this, PatientRegisterActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps remove patients */
    public void patientsRemoval(View view){
        Intent intent = new Intent(this, PatientRemovalActivity.class);
        startActivity(intent);
    }
}
