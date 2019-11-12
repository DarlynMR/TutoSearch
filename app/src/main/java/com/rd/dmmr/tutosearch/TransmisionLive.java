package com.rd.dmmr.tutosearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.os.Bundle;

import com.bambuser.broadcaster.BroadcastPlayer;
import com.bambuser.broadcaster.PlayerState;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.widget.MediaController;
import android.view.MotionEvent;
import android.widget.Toast;

public class TransmisionLive extends AppCompatActivity implements View.OnClickListener {

    private SurfaceView mVideoSurface;
    private TextView mPlayerStatusTextView, titulo;

    private static final String APPLICATION_ID = "gn3xWy0C556si80X9aPkTA";
    private static final String API_KEY = "6kcdkvvycv7tj7dsqnmxx15xm";

    private BroadcastPlayer mBroadcastPlayer;

    private Bundle datosTuto;

    final OkHttpClient mOkHttpClient = new OkHttpClient();

    MediaController mMediaController = null;

    private String idTuto,idBroadcast;

    private FirebaseFirestore fdb;

    private Button btnIniciar;

    private ImageView logoGif;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transmision_live);

        mVideoSurface = (SurfaceView) findViewById(R.id.VideoSurfaceView);
        mPlayerStatusTextView = (TextView) findViewById(R.id.PlayerStatusTextView);
        btnIniciar = (Button) findViewById(R.id.btnIniciar);
        titulo = (TextView) findViewById(R.id.txtTituloLive);
        logoGif = (ImageView) findViewById(R.id.liveGif);


        Intent intent= getIntent();

        datosTuto=intent.getExtras();
        if (datosTuto!=null) {
            idTuto = datosTuto.getString("idTuto");
            Log.i("Prueba", idTuto);

            titulo.setText(datosTuto.getString("Titulo"));
            Log.i("ProbandoAsistir",""+datosTuto.getString("TipoEs"));
        }



        fdb = FirebaseFirestore.getInstance();

        btnIniciar.setOnClickListener(this);

    }


    private void ObtenerBroadcastID (){


        DocumentReference dc = fdb.collection("Tutorias_institucionales").document(idTuto);
        dc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot docS = task.getResult();
                Log.i("LiveError request", idTuto);
                idBroadcast = docS.getString("broadcastId");

                if (!idBroadcast.equals("None")) {
                    getLatestResourceUri();
                } else{
                    Toast.makeText(TransmisionLive.this, "Esta transmisión aun no ha iniciado", Toast.LENGTH_SHORT).show();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TransmisionLive.this, "No se pudo obtener el ID de la transmisión", Toast.LENGTH_SHORT).show();
            }
        });


    }

    BroadcastPlayer.Observer mBroadcastPlayerObserver = new BroadcastPlayer.Observer() {
        @Override
        public void onStateChange(PlayerState playerState) {
            if (mPlayerStatusTextView != null)
                mPlayerStatusTextView.setText("Status: " + playerState);
            Log.i("BroadcastError Http", ""+playerState);
            if (playerState.toString().equals("COMPLETED")){
                logoGif.setVisibility(View.INVISIBLE);
            }


            if (playerState == PlayerState.PLAYING || playerState == PlayerState.PAUSED || playerState == PlayerState.COMPLETED) {
                if (mMediaController == null && mBroadcastPlayer != null && !mBroadcastPlayer.isTypeLive()) {
                    mMediaController = new MediaController(TransmisionLive.this);
                    mMediaController.setAnchorView(mVideoSurface);
                    mMediaController.setMediaPlayer(mBroadcastPlayer);
                }
                if (mMediaController != null) {
                    mMediaController.setEnabled(true);
                    mMediaController.show();
                }
            } else if (playerState == PlayerState.ERROR || playerState == PlayerState.CLOSED) {
                if (mMediaController != null) {
                    mMediaController.setEnabled(false);
                    mMediaController.hide();
                }
                mMediaController = null;
            }
        }
        @Override
        public void onBroadcastLoaded(boolean live, int width, int height) {
            Log.i("ProbandoLiveEstate", ""+live);
            if (live){
                Glide.with(TransmisionLive.this).load(R.raw.logo_live_no_recording).into(logoGif);
                logoGif.setVisibility(View.INVISIBLE);
            }
        }

    };
    // ...

    @Override
    protected void onPause() {
        super.onPause();
        mOkHttpClient.dispatcher().cancelAll();
        mVideoSurface = null;

        if (mBroadcastPlayer != null)
            mBroadcastPlayer.close();
        mBroadcastPlayer = null;

        if (mMediaController != null)
            mMediaController.hide();
        mMediaController = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoSurface = (SurfaceView) findViewById(R.id.VideoSurfaceView);
        mPlayerStatusTextView.setText("Loading latest broadcast");
    }

    void getLatestResourceUri() {


        Request request = new Request.Builder()
                .url("https://api.bambuser.com/broadcasts/"+idBroadcast)
                .addHeader("Accept", "application/vnd.bambuser.v1+json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .get()
                .build();

        Log.i("LiveError request", idBroadcast);

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                runOnUiThread(new Runnable() { @Override public void run() {
                    if (mPlayerStatusTextView != null)
                        mPlayerStatusTextView.setText("Http exception: " + e);
                    Log.i("BroadcastError Http", ""+e.getMessage());
                }});
            }
            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                String body = response.body().string();
                String resourceUri = null;
                Log.i("BroadcastError Call", ""+call);
                Log.i("BroadcastError Response", ""+response);
                Log.i("BroadcastError Body", body);

                try {
                    JSONObject json = new JSONObject(body);
                    resourceUri = json.optString("resourceUri");
                    Log.i("LiveError Uri", resourceUri);
                } catch (Exception ignored) {
                    Log.i("BroadcastError 2", ""+ignored.getMessage());
                }
                final String uri = resourceUri;
                runOnUiThread(new Runnable() { @Override public void run() {
                    initPlayer(uri);
                }});
            }
        });
    }

    void initPlayer(String resourceUri) {
        if (resourceUri == null) {
            if (mPlayerStatusTextView != null)
                mPlayerStatusTextView.setText("Could not get info about latest broadcast");

            return;
        }
        if (mVideoSurface == null) {
            // UI no longer active
            return;
        }

        if (mBroadcastPlayer != null)
            mBroadcastPlayer.close();
        mBroadcastPlayer = new BroadcastPlayer(this, resourceUri, APPLICATION_ID, mBroadcastPlayerObserver);
        mBroadcastPlayer.setSurfaceView(mVideoSurface);
        mBroadcastPlayer.load();
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_UP && mBroadcastPlayer != null && mMediaController != null) {
            PlayerState state = mBroadcastPlayer.getState();
            if (state == PlayerState.PLAYING ||
                    state == PlayerState.BUFFERING ||
                    state == PlayerState.PAUSED ||
                    state == PlayerState.COMPLETED) {
                if (mMediaController.isShowing())
                    mMediaController.hide();
                else
                    mMediaController.show();
            } else {
                mMediaController.hide();
            }
        }
        return false;
    }


    @Override
    public void onClick(View view) {
        if (view==btnIniciar){
            ObtenerBroadcastID();
        }
    }
}


