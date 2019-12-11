package com.rd.dmmr.tutosearch;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {



    private FirebaseDatabase FDatabase;
    private FirebaseAuth FAuth;
    private Query ReferenciaTutoria;
    private DatabaseReference DBReference;

    private List<ModelTutorias> mListTutoria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        FAuth=FirebaseAuth.getInstance();

        FirebaseUser user= FAuth.getCurrentUser();

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(user!=null){
            Intent intent= new Intent(MainActivity.this, pantalla_principal.class);
            startActivity(intent);
        }else {
            Intent intent= new Intent(MainActivity.this,Login.class);
            startActivity(intent);
        }





    }



}
