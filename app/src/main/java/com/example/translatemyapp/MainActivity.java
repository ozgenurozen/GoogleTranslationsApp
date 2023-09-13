package com.example.translatemyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;


import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Spinner fromSpinner,toSpinner;
    private TextInputEditText sourceText;
    private ImageView micIv;
    private MaterialButton  translateBtn;
    private TextView  translateIV;
    String[] fromlanguage={"From","English","Afrikaans","Arabic","Belarusian","Bulgarian","Bengali","Catalan","Czech","Welsh","Hindi","Urdu"};
    String[] tolanguage={"To","English","Afrikaans","Arabic","Belarusian","Bulgarian","Bengali","Catalan","Czech","Welsh","Hindi","Urdu"};

    private static final int REQUEST_PERMISSION_CODE=1;

    String languageCode,fromLanguageCode,toLanguageCode= "";
    //int fromLanguageCode,toLanguageCode=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromSpinner =findViewById(R.id.idFromSpinner);
        toSpinner=findViewById(R.id.idToSpinner);
        sourceText= findViewById(R.id.idEditSource);
        micIv= findViewById(R.id.idIVMic);
        translateBtn=findViewById(R.id.idBtnTranslation);
        translateIV=findViewById(R.id.idTranslatedTV);

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fromLanguageCode=getLanguageCode(fromlanguage[i]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter fromAdapter = new ArrayAdapter(this,R.layout.spinner_item,fromlanguage);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);


        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toLanguageCode=getLanguageCode(tolanguage[i]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter toAdapter = new ArrayAdapter(this,R.layout.spinner_item,tolanguage);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);


        micIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something to translate");

                try{
                    startActivityForResult(intent,REQUEST_PERMISSION_CODE);

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateIV.setVisibility(View.VISIBLE);
                translateIV.setText("as");
                if(sourceText.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter text to translate ",Toast.LENGTH_SHORT).show();
                }else if(fromLanguageCode==""){
                    Toast.makeText(MainActivity.this,"Please select Source Language",Toast.LENGTH_SHORT).show();
                }else if (toLanguageCode=="0"){
                    Toast.makeText(MainActivity.this,"Please select the language to make translation",Toast.LENGTH_SHORT).show();

                }else{
                    translateText(fromLanguageCode,toLanguageCode,sourceText.getText().toString());

                }
            }
        });


    }

    private void translateText(String fromLanguageCode, String toLanguageCode, String source) {

        translateIV.setText("Downloading model,please wait...");


        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(String.valueOf(fromLanguageCode))
                        .setTargetLanguage(String.valueOf(toLanguageCode))
                        .build();
        final Translator languagesTranslator =
                Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        languagesTranslator.downloadModelIfNeeded(conditions);
        translateIV.setText("Translating...");


        languagesTranslator.translate(source)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String source) {
                                translateIV.setText(source);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Faied to translate!! try again",Toast.LENGTH_SHORT).show();
                            }
                        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_PERMISSION_CODE){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            sourceText.setText(result.get(0));
        }
    }

    //String[] fromlanguage={"From","English","Afrikaans","Arabic","Belarusian","Bulgarian","Bengali","Catalan","Czech","Welsh","Hindi","Urdu"};
    private String getLanguageCode(String language) {
        int laguageCode =0;
        switch(language){
            case "English":
                languageCode = TranslateLanguage.ENGLISH;
                break;
            case "Afrikaans":
                languageCode= TranslateLanguage.AFRIKAANS;
                break;
            case "Arabic":
                languageCode= TranslateLanguage.ARABIC;
                break;
            case "Belarusian":
                languageCode= TranslateLanguage.BELARUSIAN;
                break;
            case "Bulgarian":
                languageCode= TranslateLanguage.BULGARIAN;
                break;
            case "Bengali":
                languageCode= TranslateLanguage.BENGALI;
                break;
            case "Catalan":
                languageCode= TranslateLanguage.CATALAN;
                break;
            case "Czech":
                languageCode= TranslateLanguage.CZECH;
                break;
            case "Welsh":
                languageCode= TranslateLanguage.WELSH;
                break;
            case "Hindi":
                languageCode= TranslateLanguage.HINDI;
                break;
            case "Urdu":
                languageCode= TranslateLanguage.URDU;
                break;

            default:
                languageCode="";

        }
        return languageCode;

    }
}