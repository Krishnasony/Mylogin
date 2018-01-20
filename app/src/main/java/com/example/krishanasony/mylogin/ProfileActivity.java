package com.example.krishanasony.mylogin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;



import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.zip.Inflater;

public class ProfileActivity extends AppCompatActivity {
    private static final int CHOOSE_IMAGE =101 ;
    TextView textView;
    EditText editText;
    ImageView imageView;
    Uri uriImage;
    ProgressBar progressBar;

    String profileImageUrl;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        editText=findViewById(R.id.editDisplay);
        imageView =findViewById(R.id.chooseimage);
        progressBar= findViewById(R.id.profileprogressbar);

        textView=findViewById(R.id.textverified);
        mAuth=FirebaseAuth.getInstance();



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowImageChoose();

            }
        });

        LoadUserInformation();

        findViewById(R.id.Save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
    }
//Load User information 4
    private void LoadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl().toString()).into(imageView);
            }
            if (user.getDisplayName() != null) {
                editText.setText(user.getDisplayName());
            }

            if (user.isEmailVerified()){
                textView.setText("Email Verified");
            }
            else {
                textView.setText("Email Not Verified (Click To Verify)");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(ProfileActivity.this,"Verification Email Sent!",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }

        }

    }

    private void saveUserInformation() {
        String displayname = editText.getText().toString();
        if (displayname.isEmpty())
        {
            editText.setError("Name Required");
            editText.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null&&profileImageUrl!=null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayname)
                    .setPhotoUri(Uri.parse(profileImageUrl)).build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ProfileActivity.this,"Profile Updated SuccessFully!",Toast.LENGTH_LONG).show();
                    }


                }
            });
        }
    }
    //to choose image override a method called


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CHOOSE_IMAGE&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){

            uriImage=data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriImage);
                imageView.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadImageToFirebaseStorage() {
        StorageReference profileimgref = FirebaseStorage.getInstance().getReference("Profile/"+System.currentTimeMillis()+".jpg");
        if (uriImage!=null){
            progressBar.setVisibility(View.VISIBLE);
            profileimgref.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    progressBar.setVisibility(View.GONE);
                    profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                }
            });
        }
    }
//logout option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this,MainActivity.class));
                break;
        }
        return true;
    }

    private void ShowImageChoose()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select Profile image"),CHOOSE_IMAGE);

    }
}
