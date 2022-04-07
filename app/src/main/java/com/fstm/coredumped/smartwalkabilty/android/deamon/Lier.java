package com.fstm.coredumped.smartwalkabilty.android.deamon;

import com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint;

import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class Lier extends Thread{
    int i=0;
    private List<GeoPoint> geoPoints=new ArrayList<>();

    public Lier(){
        geoPoints.add(new GeoPoint(33.7053414,-7.3545817));
        geoPoints.add(new GeoPoint(33.7062118,-7.3546988));
        geoPoints.add(new GeoPoint(33.7065837,-7.3542007));
        geoPoints.add(new GeoPoint(33.7066533,-7.3542899));
        geoPoints.add(new GeoPoint(33.7069026,-7.3537616));
        geoPoints.add(new GeoPoint( 33.7079981,-7.3519716));
        geoPoints.add(new GeoPoint(33.7080375,-7.3517114));
        geoPoints.add(new GeoPoint(33.708969,-7.34974));
        geoPoints.add(new GeoPoint(33.7088543,-7.3496113));
        geoPoints.add(new GeoPoint( 33.7079304,-7.3484108));
        geoPoints.add(new GeoPoint(33.7079987,-7.3481644));
        geoPoints.add(new GeoPoint(33.7089649,-7.3470968));
    }
    public void init(){
        i=0;
    }
    public GeoPoint GetLocation()
    {
        return geoPoints.get(i);
    }
    @Override
    public void run() {
        try {
        for (i=0;i<geoPoints.size()-1;i++ )
        {
            Thread.sleep(3000);
        }
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
