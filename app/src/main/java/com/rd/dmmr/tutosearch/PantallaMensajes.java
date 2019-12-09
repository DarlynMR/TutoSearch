package com.rd.dmmr.tutosearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PantallaMensajes extends AppCompatActivity {


    private BottomNavigationView btnBottonNavView;
    FrameLayout frameLayout;

    private fragment_mensajes fragment_mensajes;
    private fragment_amigos fragment_amigos;
    private fragment_solicitudes fragment_solicitudes;

    private FloatingActionButton fBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_mensajes);


        frameLayout = (FrameLayout) findViewById(R.id.frameLY);
        btnBottonNavView = (BottomNavigationView) findViewById(R.id.btnButtonNavView);

        fBack = (FloatingActionButton) findViewById(R.id.fBackButton) ;

        fBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        fragment_mensajes = new fragment_mensajes();
        fragment_amigos = new fragment_amigos();
        fragment_solicitudes = new fragment_solicitudes();
        setFragment(fragment_mensajes);


        btnBottonNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.iconMensajes:
                        setFragment(fragment_mensajes);
                        return  true;

                    case R.id.iconAmigos:
                        setFragment(fragment_amigos);
                        return  true;

                    case R.id.iconSolicitudes:
                        setFragment(fragment_solicitudes);
                        return  true;

                    default:
                        return false;


                }




            }
        });
    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLY, fragment);
        fragmentTransaction.commit();
    }
}
