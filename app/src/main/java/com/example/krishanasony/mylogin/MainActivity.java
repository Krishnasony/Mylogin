package com.example.krishanasony.mylogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

    FirebaseAuth mAuth;

    EditText textemail,textpassword;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      findViewById(R.id.Signup).setOnClickListener(this);
      findViewById(R.id.Login).setOnClickListener(this);//login button

        textemail =findViewById(R.id.editTextmail);
        textpassword=findViewById(R.id.editTextpass);
        progressBar=findViewById(R.id.progressbar);

        mAuth =FirebaseAuth.getInstance();//initialize the auth object
    }






    //user login custom  method
    private void userlogin()
    {

        //validate email and password
        String Email = textemail.getText().toString().trim();
        String Password = textpassword.getText().toString().trim();
//        String Username = textUsername.getText().toString().trim();

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



        mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()){
                    finish();
                    Intent intent =new Intent(MainActivity.this,ProfileActivity.class);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(intent);

                }
                else {

                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()!=null)
        {
            finish();
            startActivity(new Intent(this,ProfileActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Signup:
                finish();
                startActivity(new Intent(this,SignupActivity.class));


                break;

            case R.id.Login:

                userlogin();
                break;
        }

    }
}
