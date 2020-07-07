package com.example.twinkle.project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

/**
 * Created by Twinkle on 11/27/2017.
 */
public class ProfileActivity extends AppCompatActivity {

    private Button emailVerify;
    private FirebaseAuth firebaseAuth;
    private TextView tv_email;
    private TextView email_status;
    private TextView resetpass;
    private SharedPreferences sharedPreferences;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        if (!isNetworkAvailable()) {
            // Create an Alert Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Set the Alert Dialog Message
            builder.setMessage("Internet Connection Required")
                    .setCancelable(false)
                    .setPositiveButton("Retry",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    // Restart the Activity
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);

        //initializing shared preference
        sharedPreferences = getSharedPreferences("USER_D", Context.MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();

        //Welcome Message on toolbar
        // setTitle("Welcome: " + firebaseAuth.getCurrentUser().getDisplayName());
        firebaseUser = firebaseAuth.getCurrentUser();

        //Initializing Widgets
        TextView tv_name = (TextView) findViewById(R.id.textView9);
        resetpass=(TextView)findViewById(R.id.resetpass);
        tv_email = (TextView) findViewById(R.id.textView13);
        email_status = (TextView) findViewById(R.id.textView5);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        emailVerify = (Button) findViewById(R.id.button2);

        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               changepass();
            }
        });
        //Initializing FirebaseAuthentication


        if (firebaseUser != null) {
            tv_name.setText(firebaseUser.getDisplayName());
            tv_email.setText(firebaseUser.getEmail());

            //get verification status from Firebase
            if (firebaseUser.isEmailVerified()) {
                email_status.setText(R.string.verified);
                email_status.setTextColor(Color.GREEN);
                emailVerify.setVisibility(View.GONE);
                sharedPreferences.edit().putBoolean("verification_email", false).apply();
            }

            //setting the verification/send email button based on information regarding verification email send previously or not
            if (!sharedPreferences.getBoolean("verification_email", false))
                emailVerify.setText(R.string.verify_email);
            else
                emailVerify.setText(R.string.check_verification_status);

            emailVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseUser.reload();
                    if (firebaseUser.isEmailVerified()) {
                        email_status.setText(R.string.verified);
                        email_status.setTextColor(Color.GREEN);
                        emailVerify.setVisibility(View.GONE);
                    } else {
                        if (!sharedPreferences.getBoolean("verification_email", false)) {
                            //Verification email has not been send previously
                            sendVerification();
                        } else
                            //verification email has been send previously
                            Snackbar.make(findViewById(android.R.id.content), R.string.email_check, Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

            //Downloading and displaying user profile image
            StorageReference profileDownload = FirebaseStorage.getInstance().getReference("profile_images/"+firebaseUser.getDisplayName()).child(firebaseUser.getDisplayName());
            try {
                final File tempFile = File.createTempFile("profile picture", "png");
                profileDownload.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap profileImage = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                        imageView.setImageBitmap(profileImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendVerification() {
        final Snackbar sendingEmailSnackBar = Snackbar.make(findViewById(android.R.id.content), R.string.sending_email, Snackbar.LENGTH_INDEFINITE);
        sendingEmailSnackBar.show();
        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i("Email Verification", "onCompleteListener");
                sendingEmailSnackBar.dismiss();
                //verification email has been send, saving this information
                sharedPreferences.edit().putBoolean("verification_email", true).apply();
                Snackbar.make(findViewById(android.R.id.content), R.string.email_sent_success, Snackbar.LENGTH_SHORT).show();
                emailVerify.setText(R.string.check_verification_status);
            }
        });
    }
    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void changepass()
    {

        final AlertDialog.Builder passReset = new AlertDialog.Builder(ProfileActivity.this);
        //Inflating the reset_pass view
        View resetView = getLayoutInflater().inflate(R.layout.change_pass, null);
        //Setting the view inside the alertDialog
        passReset.setView(resetView);
        passReset.setCancelable(false);
        passReset.setTitle("Password Reset");
        final AlertDialog alertDialog = passReset.create();
        //initializing the widgets in the reset_pass view
        final EditText til_resetPass = (EditText) resetView.findViewById(R.id.textInputLayoutReset);
        Button bt_send = (Button) resetView.findViewById(R.id.button5);
        Button bt_cancel = (Button) resetView.findViewById(R.id.button6);
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onClickListener for password reset email send button
                if(firebaseAuth.getInstance().getCurrentUser()!=null)
                {
                    if ((til_resetPass.getText().toString()).equals("")) {
                     til_resetPass.setError("Enter Your Password");

                    } else if (til_resetPass.getText().toString().length() > 32) {
                        til_resetPass.setError("Maximum 32 Characters");

                    } else if (til_resetPass.getText().toString().length() < 8) {
                        til_resetPass.setError("Minimum 8 Characters");

                    }else
                    {

                        firebaseAuth.getInstance().getCurrentUser().updatePassword(til_resetPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    Snackbar.make(findViewById(android.R.id.content), "Password Changed Successfuly", Snackbar.LENGTH_SHORT).show();
                                else
                                   // Snackbar.make(findViewById(android.R.id.content), "Could not change your password. try again", Snackbar.LENGTH_SHORT).show();
                                    Toast.makeText(ProfileActivity.this,"ku6 mhi hua",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                }

            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }





}