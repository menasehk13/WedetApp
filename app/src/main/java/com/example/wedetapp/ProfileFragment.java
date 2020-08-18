package com.example.wedetapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment {
    EditText name,email1,phone1;
    Button edit,logout;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    public ProfileFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name=view.findViewById(R.id.username);
        email1=view.findViewById(R.id.email);
        phone1=view.findViewById(R.id.userphone);
        logout=view.findViewById(R.id.logout);
        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        DocumentReference reference=firestore.collection("UserInfo").document(user.getUid());
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if (task.isSuccessful()){
                   String username,email,phone;
                   DocumentSnapshot snapshot=task.getResult();
                   username= (String) snapshot.get("First Name"+" "+"Last Name" );
                   email= (String) snapshot.get("Email");
                   phone= (String) snapshot.get("Phone");
                   name.setText(username);
                   email1.setText(email);
                   phone1.setText(phone);
               }else {
                   Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
               }
            }
        });
           logout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   auth.signOut();
                   Intent intent=new Intent(getActivity(),SignUpActivity.class);
                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                   startActivity(intent);
               }
           });

    }
}