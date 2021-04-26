package com.example.bookstory.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.bookstory.R;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Autor;
import com.example.bookstory.models.Carte;
import com.example.bookstory.models.Gen;

import java.util.List;

public class AdaugaCarteActivity extends AppCompatActivity {
    EditText etTitlu;
    EditText etNrCopii;
    EditText etISBN;
    Spinner spinner;
    Button btnAddImagine;
    Button btnSave;
    Intent intent;
    ImageView imageView;
    public Uri bookCoverUri = Uri.EMPTY;
    public static final String ADD_BOOK = "addBook";
    public static final int GALLERY_REQUEST_CODE = 105;
    public static boolean isUpdate = false;
    private long editBookId;
    private LibraryDB dbInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adauga_carte);
        initializeUI();
        dbInstance = LibraryDB.getInstanta(getApplicationContext());
        intent = getIntent();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.add_book_genre, R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if(intent.hasExtra(ListareCartiActivity.EDIT_BOOK)) {
            populeazaCampuri();
        }
        btnAddImagine.setOnClickListener(v -> {
            Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQUEST_CODE);
        });
        btnSave.setOnClickListener(v -> {
         if(etTitlu.getText().toString().isEmpty()) {
             etTitlu.setError(getString(R.string.introdu_titlul));
         } else if(etISBN.getText().toString().isEmpty()) {
             etISBN.setError(getString(R.string.introdu_isbn));
         } else if(etNrCopii.getText().toString().isEmpty()) {
             etNrCopii.setError(getString(R.string.introdu_nr_de_copii_disponibile));
         } else {
             Carte carte = new Carte(etTitlu.getText().toString(),etISBN.getText().toString(),
                     Gen.valueOf(spinner.getSelectedItem().toString()),Integer.parseInt(etNrCopii.getText().toString()),bookCoverUri.toString());
             if(isUpdate) {
                 carte.setId((int) editBookId);
                 dbInstance.getCartiDao().update(carte);
             } else if(!isUpdate){
                 carte.setId((int) dbInstance.getCartiDao().insert(carte));
             }

             intent.putExtra(ADD_BOOK, carte);
             setResult(RESULT_OK, intent);
             finish();
         }
        });

    }

    private void initializeUI(){
        etISBN = findViewById(R.id.etISBN);
        etTitlu = findViewById(R.id.editTextTitle);
        etNrCopii = findViewById(R.id.etNrCopii);
        spinner = findViewById(R.id.spinnerGenre);
        btnAddImagine = findViewById(R.id.btnAddCoperta);
        btnSave = findViewById(R.id.btnSalvare);
        imageView = findViewById(R.id.bookCover);

    }

    private void populeazaCampuri() {

       Carte carte = (Carte)intent.getSerializableExtra(ListareCartiActivity.EDIT_BOOK);
       isUpdate = true;
       editBookId = carte.getIdCarte();
       etISBN.setText(carte.getISBN());
       etTitlu.setText(carte.getTitlu());
       etNrCopii.setText(String.valueOf(carte.getNrCopiiDisponibile()));
       ArrayAdapter<String> adaptor = (ArrayAdapter<String>)spinner.getAdapter();
       for(int i=0;i<adaptor.getCount();i++)
            if(adaptor.getItem(i).equals(String.valueOf(carte.getGenCarte()).toUpperCase()))
            {
                spinner.setSelection(i);
                break;
            }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri contentUri = data.getData();
            this.bookCoverUri = contentUri;
            this.imageView.setImageURI(contentUri);
        }
    }
}