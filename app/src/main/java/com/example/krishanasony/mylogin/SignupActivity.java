package com.example.krishanasony.mylogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{
    EditText textemail,textpassword, textUsername;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        textemail =findViewById(R.id.email);
        textpassword=findViewById(R.id.password);
        progressBar=findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.Signup).setOnClickListener(this);

        findViewById(R.id.Signup).setOnClickListener(this);
    }

    //validate email and password

    private void registeruser()
    {
        String Email = textemail.getText().toString().trim();
        String Password = textpassword.getText().toString().trim();
//         String Username = textUsername.getText().toString().trim();

        if(Email.isEmpty())
        {
            textemail.setError("Email is required");
            textemail.requestFocus();

            return;
        }
        //check email validation
        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){

            textemail.setError("Please Enter Valid Email Address");
            textemail.requestFocus();

            return;


        }
    /*    if (Username.isEmpty())
        {
          //  Toast.makeText(SignupActivity.this,"Name is Require!",Toast.LENGTH_LONG).show();
            textUsername.setError("Name is Required!");
            textUsername.requestFocus();
            return;
        }*/
        //check password validation

        if(Password.length()<6){
            textpassword.setError("Password Must have atleast 6 digit/character");
            textpassword.requestFocus();

            return;
        }
 // check password
        if(Password.isEmpty())
        {
            textpassword.setError("Password is required");
            textpassword.requestFocus();

            return;
        }

        progressBar.setVisibility(View.VISIBLE);
//mAuth from fire base database
        mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()){
                    finish();
                    startActivity(new Intent(SignupActivity.this,ProfileActivity.class));

                    /*Intent intent =new Intent(SignupActivity.this,ProfileActivity.class);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(intent);*/
                   // Toast.makeText(getApplicationContext(),"User Registration is Successful",Toast.LENGTH_LONG).show();
                }

                else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException){

                        Toast.makeText(getApplicationContext(),"You Are Already Registered!",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }

                    //Toast.makeText(getApplicationContext(),"Some Error occured !!",Toast.LENGTH_LONG).show();
                }


            }
        });







    }
//ssssssss
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Signup:

                registeruser();

                break;

            case R.id.textlogin:
                finish();

                startActivity(new Intent(SignupActivity.this,MainActivity.class));
                break;
        }
    }
}
