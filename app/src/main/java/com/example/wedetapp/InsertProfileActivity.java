package com.example.wedetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class InsertProfileActivity extends AppCompatActivity {
EditText firstname,lastname,email,Phone;
AppCompatCheckBox checkBox;
Button create;
FirebaseAuth auth;
FirebaseFirestore firestore;
String UserId;
String phone2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_profile);
        firstname=findViewById(R.id.firstname);
        lastname=findViewById(R.id.lastname);
        email=findViewById(R.id.EmailId);
        Phone=findViewById(R.id.phoneofuser);
       phone2 =getIntent().getStringExtra("usersPhone");
        Phone.setText(phone2);
        checkBox=findViewById(R.id.CheckBox);
        create=findViewById(R.id.create);
        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        UserId=auth.getCurrentUser().getUid();

            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentReference document = firestore.collection("UserInfo").document(UserId);
                    String first, last, email2;
                    first = firstname.getText().toString().trim();
                    last = lastname.getText().toString().trim();
                    email2 = email.getText().toString().trim();
                    if (first.isEmpty() || last.isEmpty() || email2.isEmpty()) {
                        firstname.setError("EnterFirstName");
                        firstname.isFocusable();
                        lastname.setError("EnterLastName");
                        lastname.isFocusable();
                        email.setError("EnterYourEmail");
                        email.isFocusable();
                    }


                    Map<String, Object> profile = new HashMap<>();
                    profile.put("First Name", first);
                    profile.put("Last Name", last);
                    profile.put("Email", email2);
                    profile.put("Phone", phone2);
                    document.set(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(InsertProfileActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                    });
                }
            });


    }
}