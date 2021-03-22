package com.example.bookstory.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.bookstory.R;
import com.example.bookstory.models.Carte;
import com.example.bookstory.models.Gen;

public class AdaugaCarteActivity extends AppCompatActivity {
    EditText etTitlu;
    EditText etNrCopii;
    EditText etISBN;
    Spinner spinner;
    Button btnAddImagine;
    Button btnSalvare;
    Intent intent;
    ImageView imageView;
    public Uri bookCoverUri = Uri.EMPTY;
    public static final String ADD_BOOK = "addBook";
    public static final int GALLERY_REQUEST_CODE = 105;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adauga_carte);
        initializeazaCampuri();
        intent = getIntent();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.add_book_genre, R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        btnAddImagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });
        btnSalvare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if(etTitlu.getText().toString().isEmpty()){
                 etTitlu.setError(getString(R.string.introdu_titlul));
             }else if(etISBN.getText().toString().isEmpty()){
                 etISBN.setError(getString(R.string.introdu_isbn));
             }else if(etNrCopii.getText().toString().isEmpty()){
                 etNrCopii.setError(getString(R.string.introdu_nr_de_copii_disponibile));
             }else{
                 Carte carte = new Carte(etTitlu.getText().toString(),etISBN.getText().toString(),
                         Gen.valueOf(spinner.getSelectedItem().toString()),Integer.parseInt(etNrCopii.getText().toString()),bookCoverUri.toString());
                 intent.putExtra(ADD_BOOK, carte);
                 setResult(RESULT_OK, intent);
                 finish();
             }
            }
        });

    }

    private void initializeazaCampuri(){
        etISBN = findViewById(R.id.etISBN);
        etTitlu = findViewById(R.id.editTextTitle);
        etNrCopii = findViewById(R.id.etNrCopii);
        spinner = findViewById(R.id.spinnerGenre);
        btnAddImagine = findViewById(R.id.btnAddCoperta);
        btnSalvare = findViewById(R.id.btnSalvare);
        imageView = findViewById(R.id.bookCover);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri contentUri = data.getData();
            this.bookCoverUri = contentUri;
            this.imageView.setImageURI(contentUri);
        }
    }
}