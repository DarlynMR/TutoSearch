package com.rd.dmmr.tutosearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrarUsuario extends AppCompatActivity {



    private FirebaseAuth FAutentic;
    private FirebaseAuth.AuthStateListener FInicionIndicdor;
    private DatabaseReference DBReference;


    private ProgressDialog progressDialog;


    private Button JbtnRegistrar;

    private EditText Matricula, NombreCompleto, Telefono, Carrera, Correo, Password, Password2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);


        Matricula= (EditText) findViewById(R.id.txtMatricula);
        NombreCompleto= (EditText) findViewById(R.id.txtNombreRegistrar);
        Telefono= (EditText) findViewById(R.id.txtTelefono);
        Carrera= (EditText) findViewById(R.id.txtCarrera);
        Correo= (EditText) findViewById(R.id.txtCorreo);
        Password= (EditText) findViewById(R.id.txtPassword);
        Password2= (EditText) findViewById(R.id.txtPassword2);

        FAutentic = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        JbtnRegistrar = (Button) findViewById(R.id.btnRegistrar);


        FInicionIndicdor = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser usuario = firebaseAuth.getCurrentUser();
                if (usuario != null) {
                    Mtoast("Iniciado como "+firebaseAuth.getCurrentUser().getEmail());
                } else {
                    Mtoast("Cierre de sesión correcto");

                }
            }
        };


       JbtnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Registrar(Correo.getText().toString(),Password.getText().toString());
            }
        });



    }

   public void Registrar(String email, String password){

        progressDialog.setMessage("Creando cuenta...");
        progressDialog.show();


        FAutentic.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String Matr = Matricula.getText().toString();
                String Nom = NombreCompleto.getText().toString();
                String Tel = Telefono.getText().toString();
                String Carr = Carrera.getText().toString();
                String Mail= Correo.getText().toString();
                if(!task.isSuccessful()){
                     Alerta("Ocurrio un error", "Direccion de correo en uso, falla de conexión o correo mal escrito.");
                     Log.i("Probando", task.getException().toString());

                }else {
                    Alerta("Estado de registro","Su cuenta ha sido creada correctamente, solo resta ir a su correo y " +
                            "entrar al enlace de verificación para activarla");


                    final FirebaseUser user=FAutentic.getCurrentUser();
                    DBReference= FirebaseDatabase.getInstance().getReference().child("UCATECI").child("Estudiantes").child(user.getUid());


                    HashMap<String,String> hashMap= new HashMap<>();
                    hashMap.put("Nombre",Nom);
                    hashMap.put("Telefono",Tel);
                    hashMap.put("Carrera",Carr);
                    hashMap.put("Correo",Mail);
                    hashMap.put("Matricula",Matr);


                    DBReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                FirebaseUser user= FAutentic.getCurrentUser();
                                user.sendEmailVerification();
                                FAutentic.signOut();

                                progressDialog.dismiss();
                                Intent i = new Intent(RegistrarUsuario.this, Login.class);
                                RegistrarUsuario.this.startActivity(i);

                                Mtoast("Usuario registrado correctamente");
                            }else {

                                Mtoast("Ocurrio un error al registrar el usuario");
                                user.delete();
                                FAutentic.signOut();
                                progressDialog.dismiss();
                            }

                        }
                    });


                }
                progressDialog.dismiss();
            }
        });


    }


    public void Alerta (String Titulo, String Mensaje){
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(RegistrarUsuario.this).setNegativeButton("Ok",null).create();
        alertDialog.setTitle(Titulo);
        alertDialog.setMessage(Mensaje);
        alertDialog.show();

    }

    public void Mtoast(String mensaje){

        Toast toast= Toast.makeText(RegistrarUsuario.this, mensaje,Toast.LENGTH_SHORT);
        toast.show();
    }
}
