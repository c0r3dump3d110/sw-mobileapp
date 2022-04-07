package com.fstm.coredumped.smartwalkabilty.android.deamon;

import android.graphics.Color;

import com.fstm.coredumped.smartwalkabilty.android.GeoMethods;
import com.fstm.coredumped.smartwalkabilty.android.RoutingOverlay;
import com.fstm.coredumped.smartwalkabilty.android.model.bo.UserInfos;
import com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint;
import com.fstm.coredumped.smartwalkabilty.core.routing.model.bo.Chemin;
import com.fstm.coredumped.smartwalkabilty.core.routing.model.bo.Vertex;
import com.fstm.coredumped.smartwalkabilty.web.Model.bo.Site;
import com.fstm.coredumped.smartwalkabilty.web.Model.dao.Connexion;
import com.fstm.coredumped.smartwalkabilty.web.Model.dao.DAOSite;

import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoutingHelper extends Thread{
   private GeoPoint pointD,pointA;
   private boolean running=true;
   List<Chemin> chemins ;
   RoutingOverlay overlay;
   Vertex currentVertex=null;
   Map<Vertex,Set<Polyline>> polylineMap=new HashMap<>();

    public RoutingHelper( List<Chemin> chemins, RoutingOverlay ovel) {
        this.pointD = ovel.getDepart();
        this.pointA = ovel.getArrive();
        this.chemins = chemins;
        this.overlay=ovel;
    }
    public RoutingHelper( List<Chemin> chemins, RoutingOverlay ovel,boolean running) {
        this.pointD = ovel.getDepart();
        this.pointA = ovel.getArrive();
        this.chemins = chemins;
        this.overlay=ovel;
        this.running=false;
    }
    private Vertex calculateVertexCurrent(){
        Set<Vertex> vertices=polylineMap.keySet();
        double minDistance=Double.MAX_VALUE;
        Vertex closest=null;
        for(Vertex v : vertices){
            double dist=GeoMethods.distanceToCenterOfVertex(v,pointD);
            if(closest==null || dist<minDistance){
                minDistance=dist;
                closest=v;
            }
        }
        return closest;
    }
    public void VisualiseChemin(Chemin chemin)
    {
        if(UserInfos.getInstance().getPathsToShow().contains(chemin.getPriority()))
            for (Vertex v: chemin.getVertices()) {
                List<org.osmdroid.util.GeoPoint> list=new ArrayList<>();
                list.add(GeoMethods.turnGEOOSM(v.getArrive()));
                list.add(GeoMethods.turnGEOOSM(v.getDepart()));
                if(v.getDepart().equals(pointD))currentVertex=v;
                Polyline line = new Polyline();
                line.setPoints(list);
                line.getOutlinePaint().setColor(UserInfos.getInstance().getPathsColors().get(chemin.getPriority()));
                line.setGeodesic(true);
                line.setVisible(true);
                if(!polylineMap.containsKey(v)) polylineMap.put(v,new HashSet<>());
                polylineMap.get(v).add(line);
                overlay.getMapView().getOverlays().add(line);
                overlay.getMapView().invalidate();
            }
    }
    public void RoutingProcess()
    {
        overlay.setHelper(this);
        if(chemins!=null){
            Connexion.getCon().ClearDB();
            Collections.sort(chemins, new cmp());
            for (Chemin c : chemins) {
                VisualiseChemin(c);
                for (Site a: c.getSites()) {
                    DAOSite.getDaoSite().Create(a);
                }
            }
        }
        overlay.getpContext().stopSpinner();
    }
    static class cmp implements Comparator<Chemin>{

        @Override
        public int compare(Chemin chemin, Chemin t1) {
            return Integer.compare(t1.getPriority(), chemin.getPriority());
        }
    }
    public void stopMe()
    {
        justClear();
        running=false;
        UserInfos.getInstance().setRouting(false);
        this.interrupt();
    }
    private void removePolyline(Vertex v){
        for (Polyline poly: polylineMap.get(v)) {
            overlay.getMapView().getOverlays().remove(poly);
        }
        overlay.getMapView().invalidate();
    }
    @Override
    public void run()
    {
        if(running){
        System.out.println("I ran");
        UserInfos.getInstance().setRouting(true);
        RoutingProcess();
        while (running){
            try {
                Thread.sleep(1000);
                pointD=UserInfos.getInstance().getCurrentLocation();
                overlay.getDepartMark().setVisible(false);
                overlay.ReshowMarkerDep(GeoMethods.turnGEOOSM(pointD));
                if(pointD.distanceToInMeters(pointA) <= 10){
                    stopMe();
                }else{
                    Vertex v=calculateVertexCurrent();
                    if(v!=null){
                    if(v.equals(currentVertex))continue;
                    removePolyline(currentVertex);
                    currentVertex=v;
                    }
                }
            } catch (InterruptedException e) {
               return;
            }
        }
        }
    }

    public void justClear() {
        for (Set<Polyline> v: polylineMap.values()) {
            for (Polyline po : v) {
                overlay.getMapView().getOverlays().remove(po);
            }
        }
        UserInfos.getInstance().setRouting(false);
        overlay.getMapView().invalidate();
    }
}
