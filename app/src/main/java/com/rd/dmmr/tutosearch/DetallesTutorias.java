package com.rd.dmmr.tutosearch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DetallesTutorias extends AppCompatActivity implements View.OnClickListener {

    Bundle datosTuto;

    FirebaseAuth FAuth;
    FirebaseUser user;
    DatabaseReference DBRefence;

    private FirebaseFirestore fdb;

    Button btnAsistir;

    private TextView profesor,fecha,hora, lugar, tiemporestante,titulo, descripcion;
    private ImageView imgDetalles;

    private String idTuto, tutoes, urlTuto;
    private Boolean vofAsistir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_tutorias);



        fdb = FirebaseFirestore.getInstance();
        FAuth=FirebaseAuth.getInstance();
        user = FAuth.getCurrentUser();

        imgDetalles= (ImageView) findViewById(R.id.imgTuto);

        profesor=(TextView) findViewById(R.id.textProfLive);
        fecha=(TextView) findViewById(R.id.textFechaLive);
        hora=(TextView) findViewById(R.id.textHoraLive);
        lugar=(TextView) findViewById(R.id.textLugarLive);
        tiemporestante=(TextView) findViewById(R.id.textRestanteLive);
        titulo=(TextView) findViewById(R.id.textTitulo);
        descripcion=(TextView) findViewById(R.id.textDescripcionLive);

        btnAsistir= (Button) findViewById(R.id.btnAsistirLive);

        Intent intent= getIntent();

        datosTuto=intent.getExtras();
        if (datosTuto!=null) {
            idTuto = datosTuto.getString("idTuto");

            Calendar calInicial = Calendar.getInstance();
            Calendar calFinal = Calendar.getInstance();

            calInicial.setTimeInMillis(Long.parseLong(datosTuto.getString("timestampI")));

            String fechaCal =  calInicial.get(Calendar.DAY_OF_MONTH)+"/"+(calInicial.get(Calendar.MONTH)+1)+"/"+calInicial.get(Calendar.YEAR);
            String horaCal = "", tipoEs=datosTuto.getString("TipoEs");



            profesor.setText(datosTuto.getString("Profesor"));
            fecha.setText("Fecha: "+fechaCal);
            lugar.setText(datosTuto.getString("Lugar"));
            titulo.setText(datosTuto.getString("Titulo"));
            descripcion.setText(datosTuto.getString("Descripcion"));

            urlTuto = datosTuto.getString("imgTuto");
            try {
                Glide.with(this)
                        .load(urlTuto)
                        .fitCenter()
                        .centerCrop()
                        .into(imgDetalles);

            }catch (Exception e){
                Toast.makeText(this, "Error al cargar la imagen de portada", Toast.LENGTH_SHORT).show();
            }

            if (tipoEs.equals("Live")){
                horaCal = calInicial.get(Calendar.HOUR)+":"+calInicial.get(Calendar.MINUTE);
            }else if (tipoEs.equals("Presencial")){
                calFinal.setTimeInMillis(Long.parseLong(datosTuto.getString("timestampF")));
                horaCal = calInicial.get(Calendar.HOUR)+":"+calInicial.get(Calendar.MINUTE)+" - " + calFinal.get(Calendar.HOUR)+":"+ calFinal.get(Calendar.MINUTE);
            }
            hora.setText("Hora: "+horaCal);
        }





        btnAsistir.setOnClickListener(this);
        Comprobar();



    }

    private void Comprobar() {


        DocumentReference docRef = fdb.collection("Tutorias_institucionales").document(idTuto).collection("Lista_asistir").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        vofAsistir= true;
                        btnAsistir.setText("Abandonar");
                    } else {
                        vofAsistir= false;
                        btnAsistir.setText("Asistir");
                    }
                } else {
                    Log.d("ErrorAsistir", "Failed with: ", task.getException());
                }
            }
        });

    }


    public void Asistir(){
        //try {

        Intent intent= getIntent();

        datosTuto=intent.getExtras();


        HashMap<String,String> hashMap= new HashMap<>();

        hashMap.put("timestamp",String.valueOf(System.currentTimeMillis()));

        fdb.collection("Tutorias_institucionales").document(idTuto).collection("Lista_asistir").document(user.getUid())
                .set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Mtoast("Ha marcado que asistirá a esta tutoría");
                        vofAsistir= true;
                        btnAsistir.setText("Abandonar");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Mtoast("Ocurrió un error, por favor intente de nuevo");
            }
        });



    }

    private void Abandonar() {

        fdb.collection("Tutorias_institucionales").document(idTuto).collection("Lista_asistir").document(user.getUid())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Mtoast("Ha indicado que no asistirá a esta tutoría");
                        vofAsistir= false;
                        btnAsistir.setText("Asistir");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Mtoast("Ocurrió un error, por favor intente de nuevo");
            }
        });



    }


    public void Alerta (String Titulo, String Mensaje){
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(DetallesTutorias.this).setNegativeButton("Ok",null).create();
        alertDialog.setTitle(Titulo);
        alertDialog.setMessage(Mensaje);
        alertDialog.show();

    }
    public void Mtoast(String mensaje){

        Toast toast= Toast.makeText(DetallesTutorias.this, mensaje,Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onClick(View view) {
        if (view==btnAsistir){
            if (vofAsistir){
                Abandonar();
            }else {
                Asistir();
            }
        }
    }


}
