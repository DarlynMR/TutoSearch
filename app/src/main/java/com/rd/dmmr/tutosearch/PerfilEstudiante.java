package com.rd.dmmr.tutosearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import id.zelory.compressor.Compressor;

public class PerfilEstudiante extends AppCompatActivity {


    private FirebaseFirestore fdb;
    private StorageReference urlStorage;

    private FirebaseUser mCurrentUser;
    private TextView txtNombre, txtApellido, txtFechaNacimiento, txtCorreo, txtTelefono;
    private static int currentPage = 0;
    private static final int GALERRY_PICK = 1;
    private ProgressDialog mProgressDialog;
    private ImageView img_perfil;

    //variables
    private String download_url, thumb_downloadUrl;


    private StorageReference mImageStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_perfil_estudiante);

            urlStorage = FirebaseStorage.getInstance().getReference();

            mImageStorage = FirebaseStorage.getInstance().getReference();
            //llamando la base de datos
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            //identificando el id del que tiene la sesion iniciada
            final String current_uid = mCurrentUser.getUid();
            //especificando donde se buscara


            fdb = FirebaseFirestore.getInstance();

            txtNombre = (TextView) findViewById(R.id.txtNombre);
            txtApellido = (TextView) findViewById(R.id.txtApellidos);
            txtFechaNacimiento = (TextView) findViewById(R.id.txtFechaNacimiento);
            txtCorreo = (TextView) findViewById(R.id.txtCorreo);
            txtTelefono = (TextView) findViewById(R.id.txtTelefono);
            img_perfil= (ImageView) findViewById(R.id.imgPerfil);

            //LlAMANDO EL TOOLBAR
            final Toolbar toolbar = (Toolbar) findViewById(R.id.MyToolbar);
            //  toolbar.setTitleTextColor(Color.parseColor("#00FF00"));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //Colapsando la barra
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
            collapsingToolbarLayout.setTitle("Estudiante");
            collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
            collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);

            //Color de la barra
            Context context = this;
            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(context, R.color.colorPrimary));

            //llenando la pantalla con los datos del doctor

           DocumentReference docRef = fdb.collection("Estudiantes").document(current_uid);
           docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                   //Log.i("Probando id", ""+dataSnapshot);

                   DocumentSnapshot docS = task.getResult();

                   String name = docS.getString("nombres");
                   String apellido = docS.getString("apellidos");
                   String fechaNacimiento = docS.getString("fecha_nacimiento");
                   String telefono = docS.getString("telefono");
                   String correo = docS.getString("correo");
                   String img = docS.getString("url_pic");

                   CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
                   collapsingToolbarLayout.setTitle(name + " " + apellido);

                   //para separar el numero de telefono
                   String te = telefono.replace(" ", "");
                   String tress = te.substring(0, 3);
                   String seis = te.substring(3, 6);
                   String cuatro = te.substring(6, 10);
                   String telefonoo = tress + "-" + seis + "-" + cuatro;

                   txtNombre.setText(name);
                   txtApellido.setText(apellido);
                   txtFechaNacimiento.setText(fechaNacimiento);
                   txtTelefono.setText(telefonoo);
                   txtCorreo.setText(correo);

                   if (!img.equals("defaultPicUser")){
                       try {
                           Glide.with(PerfilEstudiante.this)
                                   .load(img)
                                   .fitCenter()
                                   .centerCrop()
                                   .into(img_perfil);

                       }catch (Exception e){
                           Log.i("ErrorImg", ""+e.getMessage());
                       }

                   }else{
                       img_perfil.setImageResource(R.mipmap.default_user);
                   }



               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(PerfilEstudiante.this, "Error de la base de datos", Toast.LENGTH_SHORT).show();
               }
           });


            //boton agregar imagen
            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fltImage);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(galleryIntent, "Seleccione una imagen"), GALERRY_PICK);
                     /* .setAction("Action", null).show();
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Detalles_Usuario.this);*/


                }
            });

        } catch (Exception e) {

            Toast.makeText(PerfilEstudiante.this, "Error", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == GALERRY_PICK && resultCode == RESULT_OK) {
                Uri imageurl = data.getData();

                CropImage.activity(imageurl)
                        .setAspectRatio(1, 1)
                        .start(this);

                //Toast.makeText(Detalles_Usuario.this, imageurl,Toast.LENGTH_LONG).show();
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                Uri resultUri = result.getUri();

                //CREANDO EL PROGREES BAR
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setTitle("Cargando imagen...");
                mProgressDialog.setMessage("Cargando imagen espere mientras se carga la imagen.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                final String current_user_id=mCurrentUser.getUid();

                File thumb_filePath = new File(resultUri.getPath());

                final Bitmap thumb_bitmap=new Compressor(this)
                        .setMaxWidth(100)
                        .setMaxHeight(100)
                        .setQuality(100)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                final byte[] thumb_byte=baos.toByteArray();

                StorageReference filepath=mImageStorage.child("img_profile").child(current_user_id + ".jpg");
                final StorageReference thumb_filepath =mImageStorage.child("img_profile").child("thumbs").child(current_user_id+".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            final StorageReference url_filePath = urlStorage.child("img_profile").child(current_user_id + ".jpg");
                            url_filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    download_url = uri.toString();
                                    Map update_hashmMap=new HashMap();
                                    update_hashmMap.put("url_pic",download_url);
                                    fdb.collection("Estudiantes").document(current_user_id)
                                            .update(update_hashmMap)
                                            .addOnSuccessListener(new OnSuccessListener() {
                                                @Override
                                                public void onSuccess(Object o) {
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(PerfilEstudiante.this,"Error al subir la imagen.",Toast.LENGTH_LONG).show();
                                            mProgressDialog.dismiss();
                                        }
                                    });

                                }

                            });


                            UploadTask uploadTask=thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    if(thumb_task.isSuccessful()){

                                        final StorageReference url_thumbfilePath = urlStorage.child("img_profile").child("thumbs").child(current_user_id + ".jpg");
                                        url_thumbfilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                thumb_downloadUrl = uri.toString();

                                                Map update_hashmMap=new HashMap();
                                                update_hashmMap.put("url_thumb_pic",thumb_downloadUrl);

                                                fdb.collection("Estudiantes").document(current_user_id)
                                                        .update(update_hashmMap)
                                                        .addOnSuccessListener(new OnSuccessListener() {
                                                            @Override
                                                            public void onSuccess(Object o) {
                                                                mProgressDialog.dismiss();
                                                                Toast.makeText(PerfilEstudiante.this,"Imagen Subida.",Toast.LENGTH_LONG).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(PerfilEstudiante.this,"Error al subir la imagen.",Toast.LENGTH_LONG).show();
                                                        mProgressDialog.dismiss();
                                                    }
                                                });

                                            }

                                        });


                                    }else{
                                        Toast.makeText(PerfilEstudiante.this,"Error al subir la imagen.",Toast.LENGTH_LONG).show();
                                        mProgressDialog.dismiss();
                                    }

                                }
                            });

                        }else{
                            Toast.makeText(PerfilEstudiante.this,"Error al subir la imagen.",Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });

            }
        }catch (Exception e){
            Toast.makeText(PerfilEstudiante.this,"No se cambio la imagen!",Toast.LENGTH_LONG).show();
        }
    }



    public static String random(){
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLegth=generator.nextInt(10);
        char tempchar;
        for(int i=0; i<randomLegth;i++){
            tempchar=(char)(generator.nextInt(96)+32);
            randomStringBuilder.append(tempchar);
        }
        return randomStringBuilder.toString();
    }



    private void sendToStart() {
        try {
            Intent startintent = new Intent(PerfilEstudiante.this, Login.class);
            startActivity(startintent);
            finish();
        }catch (Exception e){
            Toast.makeText(PerfilEstudiante.this,"Error al abrir la app", Toast.LENGTH_SHORT).show();
        }
    }

}




