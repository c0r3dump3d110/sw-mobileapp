package com.fstm.coredumped.smartwalkabilty.android;

import com.fstm.coredumped.smartwalkabilty.core.routing.model.bo.Vertex;

import org.osmdroid.util.GeoPoint;

import java.util.StringTokenizer;

public interface GeoMethods {

    static GeoPoint turnGEOOSM(com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint geoPoint) {
        GeoPoint point = new GeoPoint(geoPoint.getLaltittude(), geoPoint.getLongtitude());
        return point;
    }

    static double tronque(double x, int nbElem) {
        String str, prefix, suffix;
        StringTokenizer strs = new StringTokenizer(Double.toString(x), ".");
        prefix = strs.nextToken();
        if (!strs.hasMoreTokens() || nbElem == 0) {
            return Double.parseDouble(prefix);
        }
        str = prefix;
        suffix = strs.nextToken();
        if (suffix.length() > nbElem) suffix = suffix.substring(0, nbElem);
        str += "." + suffix;
        return Double.parseDouble(str);
    }
    static double distanceToCenterOfVertex(Vertex v, com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint loc){
        double Ycenter = (v.getDepart().getLaltittude() + v.getArrive().getLaltittude())/2.0;
        double Xcenter = (v.getDepart().getLongtitude() + v.getArrive().getLongtitude())/2.0;
        return loc.distanceToInMeters(new com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint(Ycenter,Xcenter));
    }
}
