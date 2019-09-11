package com.rd.dmmr.tutosearch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class Tutorias extends AppCompatActivity implements View.OnClickListener {



    private TabLayout tabLayout;
    private ViewPager viewPager;

    Button btnBuscar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorias);

        tabLayout = (TabLayout) findViewById(R.id.tab_tutorias);
        viewPager = (ViewPager) findViewById(R.id.pagerTutorias);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.AddFragment(new FragmentOficial(), "Oficiales");
        viewPagerAdapter.AddFragment(new FragmentExtraoficial(), "Extraoficial");
        viewPagerAdapter.AddFragment(new FragmentHelptuto(), "Ayuda un compañero");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);



        btnBuscar= (Button) findViewById(R.id.btnBuscar);






        btnBuscar.setOnClickListener(this);




    }




    @Override
    public void onClick(View view) {
       /* if (view==btnBuscar){
            limpiarRC();
        }*/
    }




}
