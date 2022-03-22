package com.fstm.coredumped.smartwalkabilty.web.Model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import static com.fstm.coredumped.smartwalkabilty.web.Model.dao.SiteTable.*;
import com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint;
import com.fstm.coredumped.smartwalkabilty.web.Model.bo.*;

import java.sql.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DAOSite implements IDAO<Site>{
    private static DAOSite daoSite=null;

    public DAOSite() {
    }

    public static DAOSite getDaoSite() {
        if(daoSite == null) daoSite= new DAOSite();
        return daoSite;
    }

    @Override
    public boolean Create(Site obj)
    {
        if(existance(obj.getId())) return false;
        SQLiteDatabase db=Connexion.getCon().getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        try
        {
            contentValues.put(id,obj.getId());
            contentValues.put(Name,obj.getName());
            contentValues.put(Organisation,obj.getOrganisation().getNom());
            contentValues.put(localisationY,obj.getLocalisation().getLaltittude());
            contentValues.put(localisationX,obj.getLocalisation().getLongtitude());
            contentValues.put(DateInserted,new Date().toString());
            db.beginTransaction();
            db.insertOrThrow(TableName,null,contentValues);
            db.setTransactionSuccessful();
            db.endTransaction();
        }catch (Exception e)
        {
            db.endTransaction();
            System.err.println(e);
            return false;
        }
        return true;
    }
    private Site extractSite(Cursor set){
        Site site=new Site();
        site.setId(set.getInt(1));
        site.setName(set.getString(2));
        GeoPoint geoPoint = new GeoPoint();
        geoPoint.setLaltittude(set.getDouble(5));
        geoPoint.setLongtitude(set.getDouble(4));
        site.setLocalisation(geoPoint);
        site.setOrganisation(new Organisation());
        site.getOrganisation().setNom(set.getString(3));
        return site;
    }
    @Override
    public Collection<Site> Retrieve() {
        return null;
    }
    public Set<Site> RetrieveNear(GeoPoint loc, double meters)
    {
        Set<Site> sites=new HashSet<>();
        SQLiteDatabase database=Connexion.getCon().getReadableDatabase();
        Cursor set=database.query(TableName,new String[]{id,Name,Organisation,localisationX,localisationY},null,null,null,null,null);
        while (set.moveToNext())
        {
            Site site=extractSite(set);
            double distance=loc.distanceToInMeters(site.getLocalisation());
            if(distance<=meters){
                DAOAnnonce.getDAOAnnonce().extractSiteAnnonces(site);
                sites.add(site);
            }
        }
        return sites;
    }

    @Override
    public boolean delete(Site obj) {
        SQLiteDatabase db= Connexion.getCon().getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(TableName,id+"?",new String[]{String.valueOf(obj.getId())});
            db.setTransactionSuccessful();
            db.endTransaction();
            return true;
        }catch (Exception e){
            db.endTransaction();
            return false;
        }
    }
    public boolean existance(int id) {
        SQLiteDatabase database=Connexion.getCon().getReadableDatabase();
        Cursor set=database.query(TableName,new String[]{SiteTable.id},SiteTable.id+"=?",new String[]{String.valueOf(id)},null,null,null);
        if(set.moveToNext())return true;
        return false;
    }

}
