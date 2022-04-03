package com.fstm.coredumped.smartwalkabilty.web.Model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fstm.coredumped.smartwalkabilty.web.Model.bo.Annonce;
import com.fstm.coredumped.smartwalkabilty.web.Model.bo.Image;
import com.fstm.coredumped.smartwalkabilty.web.Model.bo.Site;
import static com.fstm.coredumped.smartwalkabilty.web.Model.dao.AnnounceTable.*;
import static com.fstm.coredumped.smartwalkabilty.web.Model.dao.SiteTable.TableName;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DAOAnnonce implements IDAO<Annonce>{
    private static DAOAnnonce daoAnnonce=null;
    public static DAOAnnonce getDAOAnnonce(){
        if(daoAnnonce==null)daoAnnonce=new DAOAnnonce();
        return daoAnnonce;
    }
    private DAOAnnonce(){
    }
    @Override
    public boolean Create(Annonce obj)
    {
        if(checkExiste(obj.getId()))return false;
        SQLiteDatabase connexion= Connexion.getCon().getWritableDatabase();
        try {
            connexion.beginTransaction();
            ContentValues contentValues=new ContentValues();
            fillStatement(obj, contentValues);
            connexion.insertOrThrow(TableName,null,contentValues);
            for (Image img: obj.getSubImgs()) {
                DAOImage.getDAOImage().Create(img);
            }
            for (Site site :obj.getSites())
            {
                Create_Relation_Ann_Site(obj,site);
            }
            connexion.setTransactionSuccessful();
            connexion.endTransaction();
            return true;
        }catch (Exception e){
            connexion.endTransaction();
            System.err.println(e);
            return false;
        }

    }

    @Override
    public Collection<Annonce> Retrieve()
    {
        return null;
    }


    private void fillStatement(Annonce obj, ContentValues preparedStatement)  {
        preparedStatement.put(dateD, obj.getDateDebut().toString());
        preparedStatement.put(dateF, obj.getDateFin().toString());
        preparedStatement.put(titre,obj.getTitre());
        preparedStatement.put(urlPrincipalImage,obj.getUrlPrincipalImage());
        preparedStatement.put(description,obj.getDescription());
        preparedStatement.put(categorie,obj.getCategorie().getId());
    }

    @Override
    public boolean delete(Annonce obj)
    {
       return false;
    }
    public boolean Create_Relation_Ann_Site(Annonce annonce,Site site)
    {
        //DAOSite.getDaoSite().Create(site);
        SQLiteDatabase database=Connexion.getCon().getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values=new ContentValues();
            values.put(A_S_Table.Announce,annonce.getId());
            values.put(A_S_Table.Site,site.getId());
            database.insertOrThrow(A_S_Table.TableName,null,values);
            database.setTransactionSuccessful();
            database.endTransaction();
            return true;
        }catch (Throwable exception){
            database.endTransaction();
            System.err.println(exception);
            return false;
        }
    }
    private Annonce extractAnnonce(Cursor set) throws SQLException {
        Annonce annonce=new Annonce();
        annonce.setId(set.getInt(0));
        annonce.setDescription(set.getString(1));
        annonce.setUrlPrincipalImage(set.getString(2));
        annonce.setTitre(set.getString(3));
        String dated=set.getString(4);
        String datef=set.getString(5);
        SimpleDateFormat dateFormat=new SimpleDateFormat("dow mon dd hh:mm:ss zzz yyyy");
        try {
            annonce.setDateDebut(dateFormat.parse(dated));
            annonce.setDateFin(dateFormat.parse(datef));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DAOImage.getDAOImage().findImagesByAnnonce(annonce);
        return annonce;
    }
    public void extractSiteAnnonces(Site site)
    {
        SQLiteDatabase database=Connexion.getCon().getWritableDatabase();
        database.beginTransaction();
        try {
            Cursor set =database.rawQuery("SELECT a.* FROM "+TableName+" a JOIN "+A_S_Table.TableName+" acs on a."+id+" = acs."+A_S_Table.Announce+"  where id_site=?",new String[]{String.valueOf(site.getId())}) ;
            while (set.moveToNext()){
                site.AddAnnonce(extractAnnonce(set));
            }
            database.setTransactionSuccessful();
            database.endTransaction();
        }catch (Exception e){
            database.endTransaction();
            System.err.println(e);
        }
    }
    public boolean checkExiste(int id) {
        try {
            SQLiteDatabase database=Connexion.getCon().getReadableDatabase();
            Cursor set=database.query(TableName,new String[]{SiteTable.id},SiteTable.id+"=?",new String[]{String.valueOf(id)},null,null,null);
            if(set.moveToNext())return true;
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
