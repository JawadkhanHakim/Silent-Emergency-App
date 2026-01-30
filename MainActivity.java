package com.example.silentemergencyapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.SparseBooleanArray;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.navigation.NavigationView;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    EditText etName, etPhone;
    Button btnAdd, btnDelete, btnSOS;
    ListView listContacts;
    ArrayList<String> contacts;
    ArrayAdapter<String> adapter;
    SharedPreferences sp;
    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        btnAdd = findViewById(R.id.btnAdd);
        btnDelete = findViewById(R.id.btnDelete);
        btnSOS = findViewById(R.id.btnSOS);
        listContacts = findViewById(R.id.listContacts);

        sp = getSharedPreferences("CONTACTS", MODE_PRIVATE);
        contacts = new ArrayList<>(sp.getStringSet("list", new HashSet<>()));
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice,
                contacts);
        listContacts.setAdapter(adapter);
        listContacts.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            if (!name.isEmpty() && !phone.isEmpty()) {
                contacts.add(name + " - " + phone);
                saveContacts();
                adapter.notifyDataSetChanged();
                etName.setText("");
                etPhone.setText("");
            }
        });

        btnDelete.setOnClickListener(v -> {
            SparseBooleanArray checked = listContacts.getCheckedItemPositions();
            for (int i = contacts.size() - 1; i >= 0; i--) {
                if (checked.get(i)) contacts.remove(i);
            }
            saveContacts();
            adapter.notifyDataSetChanged();
        });

        btnSOS.setOnClickListener(v -> sendSOS());

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        NavigationView nav = findViewById(R.id.navigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(v -> drawer.openDrawer(nav));

        nav.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile)
                startActivity(new Intent(this, ProfileActivity.class));
            if (item.getItemId() == R.id.nav_logout) showLogout();
            drawer.closeDrawers();
            return true;
        });
    }

    private void saveContacts() {
        sp.edit().putStringSet("list", new HashSet<>(contacts)).apply();
    }

    private void sendSOS() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(loc -> {
                    String msg = "I am in danger. Please help.";
                    if (loc != null)
                        msg += " Location: https://maps.google.com/?q=" +
                                loc.getLatitude() + "," + loc.getLongitude();

                    SmsManager sms = SmsManager.getDefault();
                    for (String c : contacts) {
                        String phone = c.split("-")[1].trim();
                        sms.sendTextMessage(phone, null, msg, null, null);
                    }
                    Toast.makeText(this, "SOS Sent", Toast.LENGTH_LONG).show();
                });
    }

    private void showLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Do you really want to logout?")
                .setPositiveButton("Yes", (d,w)->{
                    getSharedPreferences("LOGIN",MODE_PRIVATE)
                            .edit().putBoolean("isLoggedIn",false).apply();
                    startActivity(new Intent(this,LoginActivity.class));
                    finish();
                }).setNegativeButton("Cancel",null).show();
    }
}
