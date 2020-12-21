package com.example.wedetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity {
EditText codeview;
Button submit;
private String vereificationId;
private FirebaseAuth mAuth;
private String PhoneNumberEntered;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        Intent intent=getIntent();
         PhoneNumberEntered=intent.getStringExtra("Phone");
        codeview=findViewById(R.id.coderesived);
              sendVerificationCode(PhoneNumberEntered);
              submit=findViewById(R.id.submitcode);
              mAuth=FirebaseAuth.getInstance();
              submit.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                        String code=codeview.getText().toString().trim();
                        if (code.isEmpty()|| code.length()<6){
                            codeview.setError("Enter Code....");
                            codeview.isFocusable();
                            return;
                        }
                      VerifyCode(code);
                  }
              });
    }
    private void VerifyCode(String code){
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(vereificationId,code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful()){
                   Intent  intent=new Intent(PhoneVerificationActivity.this,InsertProfileActivity.class);
                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                   intent.putExtra("usersPhone",PhoneNumberEntered);
                   startActivity(intent);


               }   else{
                   Toast.makeText(PhoneVerificationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
               }
            }
        });
    }

    private void sendVerificationCode(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
               "+251" + number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            vereificationId=s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code=phoneAuthCredential.getSmsCode();
            if (code!=null){
                codeview.setText(code);
                VerifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(PhoneVerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
}