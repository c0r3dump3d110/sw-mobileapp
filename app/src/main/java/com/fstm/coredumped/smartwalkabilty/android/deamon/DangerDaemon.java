package com.fstm.coredumped.smartwalkabilty.android.deamon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

import com.fstm.coredumped.android.R;
import com.fstm.coredumped.smartwalkabilty.android.ClientSocket;
import com.fstm.coredumped.smartwalkabilty.core.danger.bo.Danger;
import com.fstm.coredumped.smartwalkabilty.core.danger.bo.Declaration;
import com.fstm.coredumped.smartwalkabilty.core.routing.model.bo.Vertex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.fstm.coredumped.smartwalkabilty.android.GeoMethods.*;
public class DangerDaemon extends Thread
{
    private static DangerDaemon dangerDaemon;
    private MapView mapView;
    private Context context;
    private Map<Vertex, Marker> declarationMarkerMap = new HashMap<>();
    ClientSocket socket=new ClientSocket();
    public static void CreateDangerDaemon(MapView mapView, Context context){
        dangerDaemon=new DangerDaemon(mapView,context);
        dangerDaemon.start();
    }
    public static DangerDaemon GetDangerDeamon(){
         return dangerDaemon;
    }

    private DangerDaemon(MapView mapView, Context context) {
        this.mapView = mapView;
        this.context = context;
    }

    public MapView getMapView() {
        return mapView;
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                ClearDangers();
                socket.SendDangerReq();
                Thread.sleep(5000);
            }catch (InterruptedException exception)
            {
                System.err.println(exception.toString());
            }
        }
    }

    public void showDeclarations(List<Declaration> declarations) {
        for (Declaration d: declarations) {
            Marker marker=new Marker(mapView);
            marker.setIcon(getDrawableByDanger(d.getDanger()));
            marker.setPosition(turnGEOOSM(CenterOfVertex(d.getVertex())));
            marker.setVisible(true);
            marker.setTitle(d.getDanger().toString()+"\n Degree : "+GetDegree(d.getDanger().getDegree()));
            declarationMarkerMap.put(d.getVertex(),marker);
        }
    }
    @SuppressLint("NewApi")
    private void ClearDangers(){
        declarationMarkerMap.keySet().stream().forEach(vertex -> { mapView.getOverlays().remove(declarationMarkerMap.get(vertex)); });
        declarationMarkerMap.clear();
    }
    private Drawable getDrawableByDanger(Danger danger)
    {
        switch (danger.toString()){
                case "Accident":
                    if(danger.getDegree()<3)return AppCompatResources.getDrawable(context,R.drawable.accident1);
                    return AppCompatResources.getDrawable(context,R.drawable.accident2);
                case "Vol":
                    if(danger.getDegree()<3)return AppCompatResources.getDrawable(context, R.drawable.vol1);
                    return AppCompatResources.getDrawable(context,R.drawable.vol2);
                case "Traveaux":
                    if(danger.getDegree()<3)return AppCompatResources.getDrawable(context,R.drawable.traveaux1);
                    return AppCompatResources.getDrawable(context,R.drawable.traveaux2);
            default:
                return AppCompatResources.getDrawable(context,R.drawable.warning);
        }
    }
    private String GetDegree(int d){
        switch (d){
            case 1 : return "Very Low";
            case 2 : return "Low";
            case 3 : return "Medium";
            case 4 : return "High";
            case 5 : return "Very High";
            default: return "Unknown";
        }
    }
}
