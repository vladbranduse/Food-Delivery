package com.example.fooddeliveryserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fooddeliveryserver.Model.Common;
import com.example.fooddeliveryserver.Model.User;
import com.example.fooddeliveryserver.Model.Common;
import com.example.fooddeliveryserver.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    DatabaseReference database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fooddeliverydatabase-5ac23-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText phone = findViewById(R.id.phone);
        final EditText password = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.loginButton);
        final TextView registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String phoneTxt = phone.getText().toString();
                final String passwordTxt = password.getText().toString();

                if(phoneTxt.isEmpty() || passwordTxt.isEmpty()){
                    Toast.makeText(Login.this, "Te rugam introduce numarul de teelfon sau parola!", Toast.LENGTH_SHORT).show();
                }
                else{
                    database.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(phoneTxt)){
                                User user = snapshot.child(phoneTxt).getValue(User.class);
                                Log.i("asd", user.toString());
                                final String getPassword = snapshot.child(phoneTxt).child("password").getValue(String.class);

                                if(Boolean.parseBoolean(user.getIsStaff())){
                                    if(user.getPassword().equals(passwordTxt)) {
                                        //Toast.makeText(Login.this, "Succes logare!!", Toast.LENGTH_SHORT).show();
                                        user.setPhone(phone.getText().toString());
                                        Log.i("tag", user.toString());
                                        Intent homeIntent = new Intent(Login.this, Home.class);
                                        Common.currentUser = user;
                                        startActivity(homeIntent);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(Login.this, "Parola gresita", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    Toast.makeText(Login.this, "Va rugam sa intrati cu un cont de admin", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(Login.this, "Acest user nu exista in baza de date!", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
}