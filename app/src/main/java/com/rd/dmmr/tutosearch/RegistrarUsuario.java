package com.rd.dmmr.tutosearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import id.zelory.compressor.Compressor;

public class RegistrarUsuario extends AppCompatActivity implements View.OnClickListener {


    //firebase variables
    private FirebaseAuth FAutentic;
    private FirebaseAuth.AuthStateListener FInicionIndicdor;
    private DatabaseReference DBReference;

    //Variables Firebase de Storage
    private StorageReference imgStorage;
    private StorageReference urlStorage;

    //Variables
    private static final int GALLERY_INTENT = 1;
    private Uri uri = null;
    byte[] thumb_byte;
    String download_url, thumb_downloadUrl;


    private ProgressDialog progressDialog;

    //botones
    private Button btnRegistrar, btnCargarImagen;

    private ImageView imgCircularProfileUser;

    //campos de datos del profesor
    private EditText  nombres,apellidos, telefono, correo, password, password2;


    private FloatingActionButton fback_button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);


        nombres= (EditText) findViewById(R.id.txt_nombres_registrar);
        apellidos= (EditText) findViewById(R.id.txt_apellidos_registrar);
        telefono= (EditText) findViewById(R.id.txtTelefono);
        correo= (EditText) findViewById(R.id.txtCorreo);
        password= (EditText) findViewById(R.id.txtPassword);
        password2= (EditText) findViewById(R.id.txtPassword2);

        FAutentic = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        btnCargarImagen = (Button) findViewById(R.id.btncargarimageUser);

        fback_button = (FloatingActionButton) findViewById(R.id.fBackButton);

        imgCircularProfileUser= (ImageView) findViewById(R.id.circularprofileUser);

        imgStorage = FirebaseStorage.getInstance().getReference();
        urlStorage = FirebaseStorage.getInstance().getReference();


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

        fback_button.setOnClickListener(this);
        btnRegistrar.setOnClickListener(this);
        btnCargarImagen.setOnClickListener(this);
        imgCircularProfileUser.setOnClickListener(this);


    }

   public void Registrar(String email, String password){

        progressDialog.setMessage("Creando cuenta...");
        progressDialog.show();


        FAutentic.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                     Alerta("Ocurrio un error", "Direccion de correo en uso, falla de conexión o correo mal escrito.");
                     Log.i("Probando", task.getException().toString());

                }else {
                    Alerta("Estado de registro","Su cuenta ha sido creada correctamente, solo resta ir a su correo y " +
                            "entrar al enlace de verificación para activarla");


                    final FirebaseUser user=FAutentic.getCurrentUser();
                    DBReference= FirebaseDatabase.getInstance().getReference().child("usuarios").child("estudiantes").child(user.getUid());


                    HashMap<String,String> hashMap= new HashMap<>();
                    hashMap.put("Nombres",nombres.getText().toString());
                    hashMap.put("Apellidos",apellidos.getText().toString());
                    hashMap.put("Telefono",telefono.getText().toString());
                    hashMap.put("Correo",correo.getText().toString());
                    hashMap.put("url_pic", "defaultPicUser");
                    hashMap.put("url_thumb_pic", "defaultPicUser");


                    DBReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                FirebaseUser user= FAutentic.getCurrentUser();
                                user.sendEmailVerification();
                                if (uri!=null){
                                    SubirImagen(user.getUid());
                                } else {
                                    FAutentic.signOut();
                                }

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

    private void SubirImagen(final String UIDProf) {

        if (uri != null) {

            StorageReference filePath = imgStorage.child("img_profile").child(UIDProf + ".jpg");
            final StorageReference thumb_filePath = imgStorage.child("img_profile").child("thumbs").child(UIDProf + ".jpg");

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Cargando imagen...");
            progressDialog.setMessage("Cargando imagen espere mientras se carga la imagen.");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            File thumb_filepath = new File(uri.getPath());

            final Bitmap thumb_bitmap = new Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(75)
                    .compressToBitmap(thumb_filepath);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] thumb_byte = baos.toByteArray();

            try {

                filePath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            final StorageReference url_filePath = urlStorage.child("img_profile").child(UIDProf + ".jpg");
                            url_filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    download_url = uri.toString();
                                    Map update_hashmMap=new HashMap();
                                    update_hashmMap.put("url_pic",download_url);
                                    DBReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child("estudiantes").child(UIDProf);
                                    DBReference.updateChildren(update_hashmMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });

                                }

                            });

                            //
                            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    final StorageReference url_thumb_filePath = urlStorage.child("img_profile").child("thumbs").child(UIDProf + ".jpg");
                                    url_thumb_filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            thumb_downloadUrl = uri.toString();
                                            Map update_hashmMap=new HashMap();
                                            update_hashmMap.put("url_thumb_pic",thumb_downloadUrl);
                                            DBReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child("estudiantes").child(UIDProf);
                                            DBReference.updateChildren(update_hashmMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(RegistrarUsuario.this, "Se ha subido la imagen con exito.", Toast.LENGTH_LONG).show();
                                                        FAutentic.signOut();
                                                        progressDialog.dismiss();
                                                    }
                                                    if (task.isCanceled()){
                                                        Log.i("PruebaError", "Excepcion: "+task.getResult()+ " Result: "+task.getResult());
                                                    }
                                                }
                                            });

                                        }

                                    });


                                    if (thumb_task.isSuccessful()) {
                                        FAutentic.signOut();

                                    } else {
                                        Toast.makeText(RegistrarUsuario.this, "Error al subir la imagen.", Toast.LENGTH_LONG).show();
                                        Log.i("PruebaError", "Excepcion: "+task.getResult()+ " Result: "+task.getResult());
                                        FAutentic.signOut();
                                        progressDialog.dismiss();
                                    }
                                }
                            });

                            //


                        } else {
                            Toast.makeText(RegistrarUsuario.this, "Error al subir la imagen.", Toast.LENGTH_LONG).show();
                            FAutentic.signOut();
                            Log.i("PruebaError", "Excepcion: "+task.getResult()+ " Result: "+task.getResult());
                            progressDialog.dismiss();
                        }
                    }
                });

            } catch (Exception e) {
                Log.i("PruebaError", "Causa: "+e.getCause() + " Mensaje: "+e.getMessage());
                FAutentic.signOut();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            Uri imageurl = data.getData();

            CropImage.activity(imageurl)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            uri = result.getUri();

            File thumb_filePath = new File(uri.getPath());

            final Bitmap thumb_bitmap = new Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(75)
                    .compressToBitmap(thumb_filePath);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            thumb_byte = baos.toByteArray();
        }

        imgCircularProfileUser.setImageURI(uri);

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

    public void campos_vacios(EditText campo, View view){
        campo.setError("No puede dejar este campo vacío.");
        view= campo;
    }


    @Override
    public void onClick(View view) {

        if (view==btnRegistrar){
                View ViewFocus = null;
                String pass,pass2;
                pass= password.getText().toString(); pass2= password2.getText().toString();

                if (nombres.getText().toString().length() == 0) {
                    campos_vacios(nombres,ViewFocus);
                } else if (apellidos.getText().toString().length() == 0) {
                    campos_vacios(apellidos,ViewFocus);
                }else if (telefono.getText().toString().length() == 0) {
                    campos_vacios(telefono,ViewFocus);
                }else if (correo.getText().toString().length() == 0) {
                    campos_vacios(correo,ViewFocus);
                }else if (apellidos.getText().toString().length() == 0) {
                    campos_vacios(apellidos,ViewFocus);
                } else if (password.getText().toString().length() == 0) {
                    campos_vacios(password,ViewFocus);
                } else if (password2.getText().toString().length() == 0) {
                    campos_vacios(password2,ViewFocus);
                }else if (!pass.equals(pass2)) {
                    Log.i("Prueba", ""+pass.equals(pass2));
                    password2.setError("Las contraseñas no coinciden.");
                    ViewFocus =password2;
                } else {
                    Registrar(correo.getText().toString(), password.getText().toString());
                }

        }
        if (view==fback_button){
            onBackPressed();
        }

        if (view== btnCargarImagen || view== imgCircularProfileUser){
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), GALLERY_INTENT);
        }
    }
}
