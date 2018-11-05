package com.example.subrahmanyamvaddi.detect;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private Button btSpeak,capImage, showIm, btReceive;
    private EditText etSpeak;
    private ImageView dispImage;

    private String curImagePath = null;
    private static final int IMAGE_REQUEST = 1;

    private SpeechRecognizer speechRecognizer;
    private Intent mIntent;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        btSpeak = view.findViewById(R.id.button);
        etSpeak = view.findViewById(R.id.editText2);
        capImage = view.findViewById(R.id.button4);
        dispImage = view.findViewById(R.id.imageViewMain);
        showIm = view.findViewById(R.id.button6);
        btReceive = view.findViewById(R.id.buttonSend);

        checkPermission();

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
        mIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(matches != null)
                    etSpeak.setText(matches.get(0));
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        capImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(cameraIntent.resolveActivity(getActivity().getPackageManager())!= null)
                {
                    File imagefile = null;

                    try {
                        imagefile = getImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(imagefile != null){
                        Uri imageUri = FileProvider.getUriForFile(getActivity(),"com.example.android.fileprovider",imagefile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                        startActivityForResult(cameraIntent,IMAGE_REQUEST);
                    }

                }
            }
        });

        showIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeFile(curImagePath);
                dispImage.setImageBitmap(bitmap);
            }
        });

        btSpeak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        etSpeak.setHint("You will see the output here!");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        etSpeak.setText("");
                        etSpeak.setHint("Listening..!");
                        speechRecognizer.startListening(mIntent);
                        break;
                }

                return false;
            }
        });

        btReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),BluetoothActivity.class));
            }
        });

        return view;
    }

    private File getImageFile() throws IOException
    {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "jpg_"+timestamp+ "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imgfile = File.createTempFile(filename,".jpg",storageDir);
        curImagePath = imgfile.getAbsolutePath();
        return  imgfile;
    }

    private void checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:"+ getActivity().getPackageName()));
                startActivity(intent);
                getActivity().finish();
            }
        }
    }

}
