package com.example.silentemergencyapp;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    EditText etName, etDob, etCity, etCountry;
    Button btnSave, btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etName = findViewById(R.id.etName);
        etDob = findViewById(R.id.etDob);
        etCity = findViewById(R.id.etCity);
        etCountry = findViewById(R.id.etCountry);
        btnSave = findViewById(R.id.btnSave);
        btnEdit = findViewById(R.id.btnEdit);

        btnSave.setOnClickListener(v -> {
            setEditable(false);
            Toast.makeText(this,"Profile Saved",Toast.LENGTH_SHORT).show();
        });

        btnEdit.setOnClickListener(v -> setEditable(true));
    }

    private void setEditable(boolean val){
        etName.setEnabled(val);
        etDob.setEnabled(val);
        etCity.setEnabled(val);
        etCountry.setEnabled(val);
    }
}
