package com.fstm.coredumped.smartwalkabilty.android.deamon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import com.fstm.coredumped.android.R;
import com.fstm.coredumped.smartwalkabilty.android.GeoMethods;
import com.fstm.coredumped.smartwalkabilty.android.model.bo.UserInfos;
import com.fstm.coredumped.smartwalkabilty.web.Model.bo.Annonce;
import com.fstm.coredumped.smartwalkabilty.web.Model.bo.Site;
import com.fstm.coredumped.smartwalkabilty.web.Model.dao.DAOSite;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VisualiserDeamon extends Thread{
    private DAOSite daoSite;
    private MapView mapView;
    private Context context;
    // because each site may have multiple annonces at a time
    private Map<Site, Set<Marker>> sitesMarkers = new HashMap<>();
    Polygon polygon=new Polygon();

    // store the previous location and don't start another calculation
    // untill Dist(prev, curren) > Radius
    private com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint previousLocation;

    public VisualiserDeamon(MapView mapView, Context context){
        this.mapView = mapView;
        this.context = context;
        // dependecy injection problem but it's okay
        daoSite = new DAOSite();
    }

    public void drawNearestSites(){
        this.previousLocation = UserInfos.getInstance().getCurrentLocation();
        for(Site site: daoSite.RetrieveNear(UserInfos.getInstance().getCurrentLocation(), UserInfos.getInstance().getRadius())){
            Set<Marker> announcesMarkers = new HashSet<>();
            for(Annonce annonce: site.getAnnonces()){
                Marker annonceSiteMarker = new Marker(this.mapView);

                annonceSiteMarker.setPosition(new GeoPoint(GeoMethods.turnGEOOSM(site.getLocalisation())));
                annonceSiteMarker.setIcon(context.getDrawable(R.drawable.icons8advertisement));
                mapView.getOverlays().add(annonceSiteMarker);

                annonceSiteMarker.setTitle(annonce.getTitre());
                // add more feature to the marker
                announcesMarkers.add(annonceSiteMarker);
            }
            this.sitesMarkers.put(site, announcesMarkers);
        }
    }
    private void MakePolygon(){
        mapView.getOverlays().remove(polygon);
        polygon.setPoints(Polygon.pointsAsCircle(GeoMethods.turnGEOOSM(UserInfos.getInstance().getCurrentLocation()),UserInfos.getInstance().getRadius()));
        polygon.getOutlinePaint().setColor(Color.TRANSPARENT);
        polygon.getFillPaint().setColor(Color.argb((float) 0.3,30, 239, 100));
        mapView.getOverlays().add(polygon);
        mapView.invalidate();
    }
    private void clearAnnouncesMarkers(){
        for(Site site: this.sitesMarkers.keySet()){
            for(Marker marker: this.sitesMarkers.get(site)){
                mapView.getOverlays().remove(marker);
            }
        }
        mapView.invalidate();
        this.sitesMarkers.clear();
    }

    @Override
    public void run() {
        try {
            while (true){
                //clear the previous content
                this.clearAnnouncesMarkers();

                // then draw the sites
                this.MakePolygon();
                this.drawNearestSites();

                // test if previous
                Thread.sleep(1000);
            }
        }catch (Exception e) {
            System.out.println("Exception in Deamon 2: "+e);
        }
    }
}
