package com.rd.dmmr.tutosearch;

import android.content.Intent;
import android.graphics.Color;
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

    private static Long Mdia = Long.parseLong("86400000");
    private static Long Mhora = Long.parseLong("3600000");
    private static Long Mminuto = Long.parseLong("60000");
    private static Long Msegundo = Long.parseLong("1000");

    Bundle datosTuto;

    FirebaseAuth FAuth;
    FirebaseUser user;
    DatabaseReference DBRefence;

    private FirebaseFirestore fdb;

    Button btnAsistir;

    private TextView profesor,fecha,hora, lugar, tiemporestante,titulo, descripcion;
    private ImageView imgDetalles;

    private Long milisInicial, milisActual, milisRestantes;

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

        profesor=(TextView) findViewById(R.id.textProf);
        fecha=(TextView) findViewById(R.id.textFecha);
        hora=(TextView) findViewById(R.id.textHora);
        lugar=(TextView) findViewById(R.id.textLugar);
        tiemporestante=(TextView) findViewById(R.id.textRestante);
        titulo=(TextView) findViewById(R.id.textTitulo);
        descripcion=(TextView) findViewById(R.id.textDescripcion);

        btnAsistir= (Button) findViewById(R.id.btnAsistir);

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

            milisInicial = Long.parseLong(datosTuto.getString("timestampI"));
            milisActual = System.currentTimeMillis();
            milisRestantes = milisInicial - milisActual;


            if (milisRestantes < 0) {
                tiemporestante.setText("Empezó hace: \n" + obtenerTiempoRestante(milisRestantes * -1));
                tiemporestante.setTextColor(Color.parseColor("#FFEE4747"));
            } else {
                tiemporestante.setText("Tiempo restante: \n" + obtenerTiempoRestante(milisRestantes));
            }


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
        //THilo tHilo = new THilo();
        //tHilo.start();



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
                        //btnAsistir.setBackgroundColor(Color.parseColor("#FFEE4747"));
                    } else {
                        vofAsistir= false;
                        btnAsistir.setText("Asistir");
                        //btnAsistir.setBackgroundColor(Color.parseColor("#49acd5"));
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

    private  class THilo extends Thread{

        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    do {

                        milisActual = System.currentTimeMillis();

                        milisRestantes = milisInicial - milisActual;


                        if (milisRestantes < 0) {
                            Log.i("tiempo", "entro");
                            tiemporestante.setText("Ya empezó");
                            tiemporestante.setText("Empezó hace: \n" + obtenerTiempoRestante(milisRestantes * -1));
                            tiemporestante.setTextColor(Color.parseColor("#FFEE4747"));
                        } else {
                            tiemporestante.setText("Tiempo restante: \n" + obtenerTiempoRestante(milisRestantes));
                        }

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (true);
                }
            });


        }


    }


    private String obtenerTiempoRestante(Long milisRestantes) {

        Long di, hor, min, seg;
        String textRestante="";

        if (milisRestantes>=Mdia){
            di = milisRestantes / Mdia;
            milisRestantes -= Mdia*di;
            textRestante += (milisRestantes >= Mhora ? di+" días, " :  di+" días");
        }
        if (milisRestantes>=Mhora){
            hor = milisRestantes / Mhora;
            milisRestantes -= Mhora*hor;
            textRestante += (milisRestantes >= Mminuto ? hor+" horas, " : hor+" horas");
        }
        if (milisRestantes>=Mminuto){
            min = milisRestantes / Mminuto;
            milisRestantes -= Mminuto*min;
            textRestante += (milisRestantes >= Msegundo ? min+" minutos y " : min+" minutos");
        }

        if (milisRestantes>=Msegundo){
            seg = milisRestantes / Msegundo;
            textRestante += seg+" segundos";
        }
        Log.i("tiempo", textRestante);
        return textRestante;
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
