package com.rd.dmmr.tutosearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class DetallesEstudentToProf extends AppCompatActivity implements View.OnClickListener {

    Bundle datosProf;

    ImageView imgPerfilProf;
    private TextView txtNombres, txtApellidos, txtMaterias, txtProvincia, txtAboutMe;
    private FloatingActionButton btnFabSendRequest;
    String idProf, urlpic, nombreC, keyid;

    //Firebase Variables
    private FirebaseFirestore fdb;
    private FirebaseAuth FAuth;
    private FirebaseUser FUser;
    private DatabaseReference DBRefGetid;


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_estudent_to_prof);

        imgPerfilProf= (ImageView) findViewById(R.id.imgProfPerfil);

        txtNombres = (TextView) findViewById(R.id.txtNombreProfDetalle);
        txtApellidos = (TextView) findViewById(R.id.txtApellidosProfDetalle);
        txtMaterias = (TextView) findViewById(R.id.txtMateriasProfDetalle);
        txtProvincia= (TextView) findViewById(R.id.txtProvinciaProfPerfil);
        txtAboutMe = (TextView) findViewById(R.id.txtAboutMeProfDetalle);

        fdb= FirebaseFirestore.getInstance();
        FAuth = FirebaseAuth.getInstance();
        FUser = FAuth.getCurrentUser();
        DBRefGetid = FirebaseDatabase.getInstance().getReference();

        btnFabSendRequest = (FloatingActionButton) findViewById(R.id.fltSendRequest);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        //LlAMANDO EL TOOLBAR
        final Toolbar toolbar = (Toolbar) findViewById(R.id.MyToolbarProfPerfil);
        //  toolbar.setTitleTextColor(Color.parseColor("#00FF00"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Colapsando la barra
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbarProfPerfil);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);

        //Color de la barra
        Context context = this;
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(context, R.color.colorPrimary));

        Intent intent= getIntent();

        datosProf=intent.getExtras();

        if (datosProf!=null) {
            idProf = datosProf.getString("idProf");
            Log.i("Prueba", idProf);

            txtNombres.setText(datosProf.getString("nombres"));
            txtApellidos.setText(datosProf.getString("apellidos"));
            txtMaterias.setText(datosProf.getString("materias"));
            txtProvincia.setText(datosProf.getString("provincia"));
            if (!datosProf.getString("aboutme").equals("null")) {
                txtAboutMe.setText(datosProf.getString("aboutme"));
            } else {
                txtAboutMe.setText("No se ha especificado este campo.");
            }
            urlpic = datosProf.getString("urlpic");
            if (!urlpic.equals("defaultPicProf")) {
                try {
                    Glide.with(DetallesEstudentToProf.this)
                            .load(urlpic)
                            .fitCenter()
                            .centerCrop()
                            .into(imgPerfilProf);

                } catch (Exception e) {
                    Log.i("ErrorImg", "" + e.getMessage());
                }
                Log.i("ProbandoDetallesProf", "" + datosProf.getString("nombres"));
            }
            nombreC = txtNombres.getText().toString()+" "+txtApellidos.getText().toString();
            collapsingToolbarLayout.setTitle(nombreC);
        }

        Comprobar();
        btnFabSendRequest.setOnClickListener(this);

    }

    private void Comprobar(){

        DocumentReference docRef = fdb.collection("Amigos").document(FUser.getUid()).collection("Aceptados").document(idProf);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot docS = task.getResult();

                if (!docS.exists()){
                    btnFabSendRequest.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fltSendRequest:

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Solciitud de amistad");
                builder.setMessage("¿Está seguro que desea enviar una solicitud de amistad a "+nombreC+"?");

                builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressDialog.setTitle("Enviando solicitud");
                        progressDialog.setMessage("Por favor espere hasta que se complete la solicitud");
                        progressDialog.show();
                        final String idDEnvia = FUser.getUid();
                        keyid = DBRefGetid.child("tutorias").child("institucionales").push().getKey();

                        HashMap<String, String> mapReceptor = new HashMap<>();
                        mapReceptor.put("emisor", idDEnvia);
                        mapReceptor.put("estado", "NoAcept");
                        mapReceptor.put("tipoUser", "Estudiante");

                        final HashMap<String, String> mapEmisor = new HashMap<>();
                        mapEmisor.put("receptor", idProf);
                        mapEmisor.put("estado", "NoAcept");
                        mapEmisor.put("tipoUser", "Profesor");



                        fdb.collection("Solicitudes").document(idProf).collection("Recibidas").document(keyid)
                                .set(mapReceptor)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        fdb.collection("Solicitudes").document(idDEnvia).collection("Enviadas").document(keyid)
                                                .set(mapEmisor)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(DetallesEstudentToProf.this, "Se envió la solicitud de amistad", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(DetallesEstudentToProf.this, "Ha ocurrido un error y no se pudo enviar la solicitud", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DetallesEstudentToProf.this, "Ha ocurrido un error y no se pudo enviar la solicitud", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });


                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();

                break;
        }
    }
}
