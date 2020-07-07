package com.example.twinkle.project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
//import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Twinkle on 3/2/2018.

 import android.os.Bundle;
 import android.support.annotation.NonNull;
 import android.support.design.widget.BottomNavigationView;
 import android.support.v4.app.Fragment;
 import android.support.v4.app.FragmentTransaction;
 import android.support.v7.app.AppCompatActivity;
 import android.view.MenuItem;*/

 public class MainActivity extends AppCompatActivity {
   /* private TensorFlowInferenceInterface inferenceInterface;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";
    private static final String MODEL_FILE = "file:///asset/graph.pb";
    private static final String LABEL_FILE =
            "file:///asset/label.txt";
    //private static final int INPUT_SIZE = 28;
    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
*/

    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;

/*    static {
        System.loadLibrary("tensorflow_inference");

    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
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
        sharedPreferences = getSharedPreferences("USER_D", Context.MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
        findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
    (new BottomNavigationView.OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.action_item1:
                selectedFragment = Gallery.newInstance();
                   // Log.e("back","Instance generated");
         //           Toast.makeText(getApplicationContext(),"heelo",Toast.LENGTH_LONG).show();
                break;
                case R.id.action_item2:
                selectedFragment = share.newInstance();
                break;
                case R.id.action_item3:
                selectedFragment = folder.newInstance();
                break;
            }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
       // Toast.makeText(getApplicationContext(),"jjjj",Toast.LENGTH_LONG).show();
        return true;
    }
    });

//Manually displaying the first fragment - one time only
   FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.frame_layout, Gallery.newInstance());
    transaction.commit();
        Toast.makeText(getApplicationContext(),"jkkkkjjj",Toast.LENGTH_LONG).show();
        Intent serv=new Intent(getApplicationContext(),Myservice.class);
        startService(serv);

//Used to select an item programmatically
//bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }
    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options, menu);//Menu Resource, Menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:

                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);


                startActivity(intent);
                return true;
            case R.id.logout:logout();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void logout(){
        //Creating an alert dialog to confirm logout
        firebaseAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }
}