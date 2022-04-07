package com.fstm.coredumped.smartwalkabilty.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fstm.coredumped.android.R;
import com.fstm.coredumped.smartwalkabilty.android.deamon.AnnonceDeamon_noRouting;
import com.fstm.coredumped.smartwalkabilty.android.deamon.DangerDaemon;
import com.fstm.coredumped.smartwalkabilty.android.deamon.VisualiserDeamon;
import com.fstm.coredumped.smartwalkabilty.android.model.bo.UserInfos;
import com.fstm.coredumped.smartwalkabilty.web.Model.dao.Connexion;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private RoutingOverlay routingOverlay;
    private ProgressBar progressBar;


    public void startSpinner() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.animate();
    }
    public void stopSpinner() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.app_bar_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        requestPermissionsIfNecessary(new String[] {
                 Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });
        initALL();
        map.setMultiTouchControls(true);
        map.getOverlayManager().add(routingOverlay);
        goToMyLocation();
        findViewById(R.id.Buttlocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routingOverlay.setMethod(RoutingOverlay.METHOD_ONE_POINTS);
                goToMyLocation();
                Toast.makeText(MainActivity.this,"the start is by default your location now choose the end point",Toast.LENGTH_LONG).show();
            }
        });
        findViewById(R.id.Butt_TwoMethod).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routingOverlay.setMethod(RoutingOverlay.METHOD_TWO_POINTS);
                Toast.makeText(MainActivity.this,"You Choose the start and end Points now",Toast.LENGTH_LONG).show();
            }
        });
        progressBar=findViewById(R.id.progressBar);
        progressBar.animate();
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                //Toast.makeText(MainActivity.this,"Omar this is where you set your page",Toast.LENGTH_LONG).show();
                Intent intentsettings = new Intent(this, SettingsActivity.class);
                startActivity(intentsettings);
                return true;
            case R.id.action_declareDanger:
                Intent intent=new Intent(this,DeclareDangerActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    requestCode);
        }else{
            if(requestCode==50)UserInfos.getInstance().DemandLocationOnGPS();
        }
    }
    public void requestPermissionsIfNecessary(String[] permissions) {
       requestPermissionsIfNecessary(permissions,REQUEST_PERMISSIONS_REQUEST_CODE);
    }
    public void requestPermissionsIfNecessary(String[] permissions,int code) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    code);
        }
    }
    private void initALL(){
        routingOverlay= new RoutingOverlay(this);
        Connexion.ConstructDb(getApplicationContext());
        UserInfos.initUserInfosObject(this);
        new AnnonceDeamon_noRouting().start();
        new VisualiserDeamon(this.map, getApplicationContext()).start();
        DangerDaemon.CreateDangerDaemon(map,getApplicationContext());
    }
    private void goToMyLocation()
    {
        IMapController mapController = map.getController();
        mapController.setZoom(15.8);
        GeoPoint startPoint= GeoMethods.turnGEOOSM( UserInfos.getInstance().getCurrentLocation());
        if(startPoint.getLatitude()==0 && startPoint.getLongitude()==0)startPoint = new GeoPoint(33.5821209, -7.6038164);
        mapController.setCenter(startPoint);
    }
}