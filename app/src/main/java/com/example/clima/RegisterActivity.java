package com.example.clima;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity  implements View.OnClickListener{

    EditText editTxtName, editTextEmail, editTextPassword;
    TextView regLoginLink;
    Button regButton2;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        regLoginLink = (TextView) findViewById(R.id.loginLink);
        regLoginLink.setOnClickListener(this);

        regButton2 = (Button) findViewById(R.id.regBtn);
        regButton2.setOnClickListener(this);

        editTxtName = (EditText) findViewById(R.id.regNameTxt);
        editTextEmail = (EditText) findViewById(R.id.regEmailTxt);
        editTextPassword = (EditText) findViewById(R.id.regPassTxt);

        progressBar = (ProgressBar) findViewById(R.id.regprogressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginLink:startActivity(new Intent(this, LoginActivity.class)); finish();
                break;
            case  R.id.regBtn:registerUser();
                break;
        }
    }

    private void registerUser() {
        String Name = editTxtName.getText().toString().trim();
        String Email = editTextEmail.getText().toString().trim();
        String Password = editTextPassword.getText().toString().trim();

        if (Name.isEmpty())
        {
            editTxtName.setError("Name is Required");
            editTxtName.requestFocus();
            return;
        }
        if (Email.isEmpty())
        {
            editTextEmail.setError("Email is Required");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
        {
            editTextEmail.setError("Enter a Valid Email!");
            editTextEmail.requestFocus();
            return;
        }
        if (Password.isEmpty())
        {
            editTextPassword.setError("Password is Required");
            editTextPassword.requestFocus();
            return;
        }
        if(Password.length()<8)
        {
            editTextPassword.setError("Password should have atleast 8 characters");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    User user = new User(Name, Email);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(RegisterActivity.this, "User Registered Successfully", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                            else {
                                Toast.makeText(RegisterActivity.this, "Registraction Failed! Try Again!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }else
                {
                    Toast.makeText(RegisterActivity.this, "Registraction Failed! Try Again!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }
}