package com.example.twinkle.project;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.example.twinkle.project.helper.ImageHelper;
import com.example.twinkle.project.helper.SampleApp;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class Myservice extends IntentService{

    private TensorFlowInferenceInterface inferenceInterface;
    ByteArrayInputStream inputStream;
    public static ArrayList<Model_images> al_images = new ArrayList<>();
    boolean boolean_folder;
    Bitmap bitmap;
    boolean mSucceed = true;
    Face[] result;

    public Myservice() {
        super("service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        for (int i = 0; i < al_images.size(); i++) {
           // Log.e("FOLDER", al_images.get(i).getStr_folder());
            for (int j = 0; j < al_images.get(i).getAl_imagepath().size(); j++) {
              //  Log.e("FILE", al_images.get(i).getAl_imagepath().get(j));
               if(i>=9 && i<=11 && j>=0 && j<=25) {
                    String p = al_images.get(i).getAl_imagepath().get(j).toString();

                    Uri u = Uri.fromFile(new File(p));
                    bitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            u,getContentResolver());

                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                     inputStream = new ByteArrayInputStream(output.toByteArray());
                  // Toast.makeText(this,inputStream.toString(),Toast.LENGTH_LONG).show();
                   //result detectioncall(inputStream);
                   new DetectionTask().execute(inputStream);
                  //  onPostExecute(result);
               }
    }
}
        return super.onStartCommand(intent, flags, startId);
    }
    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
        private boolean mSucceed = true;


        @Override
        protected Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try {
                //publishProgress("Detecting...");

                // Start detection.
                return faceServiceClient.detect(
                        params[0],  /* Input stream of image to detect */
                        true,       /* Whether to return face ID */
                        true,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                        new FaceServiceClient.FaceAttributeType[]{
                                FaceServiceClient.FaceAttributeType.Age,
                                FaceServiceClient.FaceAttributeType.Gender,
                                FaceServiceClient.FaceAttributeType.Smile,
                                FaceServiceClient.FaceAttributeType.Glasses,
                                FaceServiceClient.FaceAttributeType.FacialHair,
                                FaceServiceClient.FaceAttributeType.Emotion,
                                FaceServiceClient.FaceAttributeType.HeadPose,
                                FaceServiceClient.FaceAttributeType.Accessories,
                                FaceServiceClient.FaceAttributeType.Blur,
                                FaceServiceClient.FaceAttributeType.Exposure,
                                FaceServiceClient.FaceAttributeType.Hair,
                                FaceServiceClient.FaceAttributeType.Makeup,
                                FaceServiceClient.FaceAttributeType.Noise,
                                FaceServiceClient.FaceAttributeType.Occlusion
                        });
            } catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
                Log.e("error",e.getMessage().toString());
                //  addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            //  mProgressDialog.show();
            //addLog("Request: Detecting in image " + mImageUri);
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            // mProgressDialog.setMessage(progress[0]);
            // setInfo(progress[0]);
        }

        @Override
        protected void onPostExecute(Face[] result) {
            if (mSucceed) {
                Log.e("msuceed", "sucesful");
            }

            // Show the result on screen when detection is done.
            setUiAfterDetection(result, mSucceed);
        }


        // Show the result on screen when detection is done.
        private void setUiAfterDetection(Face[] result, boolean succeed) {


            if (succeed) {
                // The information about the detection result.
                String detectionResult;
                if (result != null) {
                    detectionResult = result.length + " face"
                            + (result.length != 1 ? "s" : "") + " detected";
                    Toast.makeText(getApplicationContext(),"ku6 mila", Toast.LENGTH_LONG).show();
                    // ImageView imageView = (ImageView) findViewById(R.id.image);
               /*     imageView.setImageBitmap(ImageHelper.drawFaceRectanglesOnBitmap(
                            bitmap, result, true));*/
                    detectface(result);
                } else {
                    detectionResult = "0 face detected";
                }

            }
            else
                Toast.makeText(getApplicationContext(),"ku6  nhi mila", Toast.LENGTH_LONG).show();



        }


        // The adapter of the GridView which contains the details of the detected faces.
        public void detectface(Face[] detectionResult) {
            // The detected faces.
            List<Face> faces;
            List<String> facedetail;

            // The thumbnails of detected faces.
            List<Bitmap> faceThumbnails;

            // Initialize with detection result.

            faces = new ArrayList<>();
            faceThumbnails = new ArrayList<>();
            facedetail = new ArrayList<>();

            if (detectionResult != null) {
                faces = Arrays.asList(detectionResult);
                for (Face face : faces) {
                    try {
                        // Crop face thumbnail with five main landmarks drawn from original image.
                        faceThumbnails.add(ImageHelper.generateFaceThumbnail(
                                bitmap, face.faceRectangle));
                        //String a="";
                        /*for(int i=0;i<faceThumbnails.size();i++){

                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        classifier = TensorFlowImageClassifier.create(
                                                getActivity().getApplicationContext().getAssets(),
                                                MODEL_FILE,
                                                LABEL_FILE,
                                                INPUT_SIZE,
                                                IMAGE_MEAN,
                                                IMAGE_STD,
                                                INPUT_NAME,
                                                OUTPUT_NAME);
                                        //  makeButtonVisible();

                                        Log.d(TAG, "Load Success");
                                    } catch (final Exception e) {
                                        throw new RuntimeException("Error initializing TensorFlow!", e);
                                    }
                                }
                            });
                            final List<Classifier.Recognition> results = classifier.recognizeImage(faceThumbnails.get(i));

                            if (results.size() > 0) {
                                String value = " person is : " +results.get(0).getTitle();
                                Toast.makeText(getContext(),value,Toast.LENGTH_LONG).show();
                                // mResultText.setText(value);
                            }

                            //Intent t=new Intent(getContext(),ProfileActivity.class);
                            //startActivity(t);
                            //some background method call
                            // new RecognitionTask().execute(faceThumbnails.get(i));
                        }*/
                        facedetail.add(getStringImage(ImageHelper.generateFaceThumbnail(
                                bitmap, face.faceRectangle)));
                    } catch (IOException e) {
                        // Show the exception when generating face thumbnail fails.
                        // setInfo(e.getMessage());
                    }
                }
                    Toast.makeText(getApplicationContext(),facedetail.toString(), Toast.LENGTH_LONG).show();

            }
            /*JSONObject obj=new JSONObject();
            try{
                obj.put(imagepath,facedetail);
                //Toast.makeText(getContext(),imagepath,Toast.LENGTH_LONG).show();

            }catch (Exception e)
            {
                Log.e("imagepath",e.getMessage().toString());
            }

            mCallback.ontask(obj);
            */


        }

        public String getStringImage(Bitmap bmp) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();

            al_images.clear();

            int int_position = 0;
            Uri uri;
            Cursor cursor;
            int column_index_data, column_index_folder_name;

            String absolutePathOfImage = null;
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                Log.e("Column", absolutePathOfImage);
                Log.e("Folder", cursor.getString(column_index_folder_name));
               // Toast.makeText(getApplicationContext(),"calkkkl",Toast.LENGTH_LONG).show();
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
        /*for (int i = 0; i < al_images.size(); i++) {
            Log.e("FOLDER", al_images.get(i).getStr_folder());
            for (int j = 0; j < al_images.get(i).getAl_imagepath().size(); j++) {
                Log.e("FILE", al_images.get(i).getAl_imagepath().get(j));}}*/
        Toast.makeText(getApplicationContext(),"call",Toast.LENGTH_LONG).show();

        }

}

