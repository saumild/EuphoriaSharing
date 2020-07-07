package com.example.twinkle.project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.database.Cursor;




/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Gallery.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Gallery#newInstance} factory method to
 * create an instance of this fragment.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.twinkle.project.helper.ImageHelper;
import com.example.twinkle.project.helper.SampleApp;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.android.volley.VolleyLog.TAG;


public class Gallery extends Fragment {


    private TensorFlowInferenceInterface inferenceInterface;
    public static ArrayList<Model_images> al_images = new ArrayList<>();
    boolean boolean_folder;
    Adapter_PhotosFolder obj_adapter;
    GridView gv_folder;
    Bitmap bitmap;
    JSONArray jsonArray = new JSONArray();
    Context conttext;
    private static final int REQUEST_PERMISSIONS = 100;

    //Bitmap[] faceThumbnails= new Bitmap[];
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";
    private static final String MODEL_FILE = "file:///android_asset/graph.pbtxt";
    private static final String LABEL_FILE =
            "file:///android_asset/label.txt";
    //private static final int INPUT_SIZE = 28;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final int INPUT_SIZE = 224;
    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();

    static {
        System.loadLibrary("tensorflow_inference");

    }

    public static Gallery newInstance() {
        Gallery fragment = new Gallery();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  conttext=getActivity();
       // inferenceInterface = new TensorFlowInferenceInterface();
       // inferenceInterface.initializeTensorFlow(getContext().getAssets(), MODEL_FILE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getString(R.string.subscription_key).startsWith("Please")) {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.add_subscription_key_tip_title))
                    .setMessage(getString(R.string.add_subscription_key_tip))
                    .setCancelable(false)
                    .show();
        }

        View rootview= inflater.inflate(R.layout.fragment_gallery, container, false);
        gv_folder = (GridView)rootview.findViewById(R.id.gv_folder);

        gv_folder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), PhotosActivity.class);
                // Toast.makeText(getContext(),i+"",Toast.LENGTH_LONG).show();
                intent.putExtra("value",i);
                startActivity(intent);
            }
        });


        if ((ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        }else {
            Log.e("Else","Else");
            fn_imagespath();
        }

        return rootview;
    }


    public ArrayList<Model_images> fn_imagespath() {
        al_images.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));

            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }


            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                al_path.add(absolutePathOfImage);
                al_images.get(int_position).setAl_imagepath(al_path);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                Model_images obj_model = new Model_images();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setAl_imagepath(al_path);

                al_images.add(obj_model);


            }


        }



        obj_adapter = new Adapter_PhotosFolder(getContext(),al_images);
        gv_folder.setAdapter(obj_adapter);

        return al_images;
    }
    public void ontask(JSONObject obj)
    {

        // Log.e("check","chl rha h");
        jsonArray.put(obj);

       /*Intent i=new Intent(this,CheckView.class);
       i.putExtra("json",jsonArray.toString());

       startActivity(i);*/
        Toast.makeText(getContext(),jsonArray.toString(),Toast.LENGTH_LONG).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        fn_imagespath();
                    } else {
                        Toast.makeText(getContext(), "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

  /*  private class RecognitionTask extends AsyncTask<Bitmap, String, Face[]> {
        //private boolean mSucceed = true;
        //String imagepath;


        private taskcmplt mCallback;

        @Override
        protected List<String> doInBackground(Bitmap... params) {
            // Get an instance of face service client to detect faces in image.



            } catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
                Log.e("error",e.getMessage().toString());
                //  addLog(e.getMessage());
                return null;
            }
        }*/
}