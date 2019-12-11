package com.rd.dmmr.tutosearch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rd.dmmr.tutosearch.notificaciones.Token;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.rd.dmmr.tutosearch.R.id.img_profileEst_nav;
import static com.rd.dmmr.tutosearch.R.id.txtNav_nombreEst;
import static com.rd.dmmr.tutosearch.R.id.txtCorreoEst_nav;

public class pantalla_principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Button btnTutoriasListadas, btnBuscarTutores, btnTutoriasAceptadas, btnMensajes;
    private TextView txtNamePrincipal;

    FirebaseAuth FAuth;
    FirebaseUser FUser;
    DatabaseReference DbReferenceUser;
    String miUID;
    FirebaseFirestore fdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Variables asignar

        btnTutoriasListadas = (Button) findViewById(R.id.btnTutoriasListadas);
        btnBuscarTutores = (Button) findViewById(R.id.btnBuscarProf);
        btnTutoriasAceptadas = (Button) findViewById(R.id.btnAceptadas);
        btnMensajes = (Button) findViewById(R.id.btnMensajes);

        txtNamePrincipal = (TextView) findViewById(R.id.namePrincipal);


        fdb = FirebaseFirestore.getInstance();

        FAuth= FirebaseAuth.getInstance();
        FUser = FAuth.getCurrentUser();
        miUID = FUser.getUid();
        DbReferenceUser= FirebaseDatabase.getInstance().getReference().child("usuarios").child("estudiantes").child(FUser.getUid());


        btnTutoriasListadas.setOnClickListener(this);
        btnBuscarTutores.setOnClickListener(this);
        btnTutoriasAceptadas.setOnClickListener(this);
        btnMensajes.setOnClickListener(this);

        //Variables asignar



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    public void updateToken(String token){
        CollectionReference collectionReference = fdb.collection("Tokens");

        Token mToken = new Token(token);

        collectionReference.document(miUID).set(mToken);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pantalla_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.perfil) {
            GoClass(PerfilEstudiante.class);

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.politicas_privacidad) {
            GoClass(ScrollingActivity.class);

        } else if (id == R.id.acercade) {
            GoClass(Acercade.class);
        } else if (id == R.id.logout) {
            FAuth.signOut();
            Intent intent = new Intent(pantalla_principal.this, Login.class);
            pantalla_principal.this.startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        try {
            FUser = FAuth.getCurrentUser();
            miUID = FUser.getUid();

            SharedPreferences sharedPreferences = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("current_userID", miUID);
            editor.apply();


            super.onStart();
            if (FUser==null){
                VolverInicio();
            }else{

                DocumentReference dc = fdb.collection("Estudiantes").document(FUser.getUid());
                dc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String nombresProf, apellidosProf, correoProf, url_pic;

                        DocumentSnapshot docS = task.getResult();

                        nombresProf = docS.getString("nombres");
                        apellidosProf = docS.getString("apellidos");
                        correoProf = docS.getString("correo");
                        url_pic= docS.getString("url_pic");

                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                        View headerview = navigationView.getHeaderView(0);

                        TextView txt_nombreprofNav =(TextView) headerview.findViewById(txtNav_nombreEst);

                        TextView txt_correoNav =(TextView) headerview.findViewById(txtCorreoEst_nav);
                        ImageView img_navEstu = (ImageView) headerview.findViewById(img_profileEst_nav);
                        txt_nombreprofNav.setText(nombresProf + " " + apellidosProf);
                        txtNamePrincipal.setText(nombresProf + " " + apellidosProf);
                        txt_correoNav.setText(correoProf);

                        if (url_pic.equals("defaultPicProf")){
                            img_navEstu.setImageResource(R.mipmap.profile_default);
                        } else {
                            try {
                                Glide.with(pantalla_principal.this)
                                        .load(url_pic)
                                        .fitCenter()
                                        .centerCrop()
                                        .into(img_navEstu);

                            }catch (Exception e){
                                Log.i("Error", ""+e.getMessage());
                            }

                        }

                    }
                });

            }





        }catch (Exception e){
            Toast.makeText(pantalla_principal.this,"Ha ocurrido un error al tratar de abrir la aplicación", Toast.LENGTH_SHORT).show();
        }
    }
    private void VolverInicio() {
        try {
            Intent startintent = new Intent(pantalla_principal.this, Login.class);
            startActivity(startintent);
            finish();
        }catch (Exception e){
            Toast.makeText(pantalla_principal.this,"Ha ocurrido un error al tratar de abrir la aplicación", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()){
            case R.id.btnTutoriasListadas : intent = new Intent(this,Tutorias.class);startActivity(intent); break;
            case R.id.btnMensajes : intent = new Intent(this,PantallaMensajes.class);startActivity(intent); break;
            case R.id.btnAceptadas : intent = new Intent(this,TutoriasAceptadas.class);startActivity(intent); break;
            case R.id.btnBuscarProf : intent = new Intent(this,BuscarTutores.class);startActivity(intent); break;
            default:break;
        }


    }

    private void GoClass(Class clase){
        Intent intent = new Intent(this, clase);
        startActivity(intent);
    }
}
