package com.example.instagram.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, name, email, password;
    private Button register;
    private ProgressDialog progressDialog;
    private TextView textView;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username_edittext_register_page);
        name = findViewById(R.id.fullname_edittext_register_page);
        email = findViewById(R.id.email_edittext_register_page);
        password = findViewById(R.id.password_edittext_register_page);
        register = findViewById(R.id.register_button_register_page);
        textView = findViewById(R.id.logintext);

        firebaseAuth = FirebaseAuth.getInstance();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                String UserName = username.getText().toString();
                String Name = name.getText().toString();
                String Email = email.getText().toString();
                String Password = password.getText().toString();

                if (TextUtils.isEmpty(UserName) || TextUtils.isEmpty(Name) || TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else if (Password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password length less than 6", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(UserName, Name, Email, Password);
                }

            }
        });

    }

    private void registerUser(final String usernameText, final String nameText, String emailText, String passwordText) {

        firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();
                            String UID = firebaseUser.getUid();

                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", UID);
                            hashMap.put("username", usernameText.toLowerCase());
                            hashMap.put("fullname", nameText);
                            hashMap.put("bio", "");
                            hashMap.put("imageURL", "https://firebasestorage.googleapis.com/v0/b/instagram-f84da.appspot.com/o/profile_pic.png?alt=media&token=b8e0627f-ea77-4251-bbd8-43898baad7f8");

                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();

                                    Toast.makeText(RegisterActivity.this, "Welcome", Toast.LENGTH_SHORT).show();

                                    Intent mainPageIntentFromRegister = new Intent(RegisterActivity.this, MainActivity.class);
                                    mainPageIntentFromRegister.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainPageIntentFromRegister);
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "You can't register with this email & password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
