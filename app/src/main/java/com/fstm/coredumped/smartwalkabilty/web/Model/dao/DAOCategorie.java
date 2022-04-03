package com.fstm.coredumped.smartwalkabilty.web.Model.dao;

import static com.fstm.coredumped.smartwalkabilty.web.Model.dao.CategorieTable.*;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fstm.coredumped.smartwalkabilty.web.Model.bo.Categorie;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DAOCategorie implements IDAO<Categorie>{
    private static DAOCategorie daoCategorie=null;
    public static DAOCategorie getDaoCategorie(){
        if(daoCategorie==null)daoCategorie=new DAOCategorie();
        return daoCategorie;
    }
    private DAOCategorie(){
    }
    @Override
    public boolean Create(Categorie obj) {
        SQLiteDatabase database=Connexion.getCon().getReadableDatabase();
        database.beginTransaction();
        try
        {
            ContentValues contentValues=new ContentValues();
            contentValues.put(id,obj.getId());
            contentValues.put(cat,obj.getCategorie());
            database.insertOrThrow(TableName,null,contentValues);
            database.setTransactionSuccessful();
            database.endTransaction();
            return true;
        } catch (Exception e) {
            System.err.println(e);
            database.endTransaction();
            return  false;
        }
    }
    public boolean Create(SQLiteDatabase database,Categorie obj) {
        database.beginTransaction();
        try
        {
            ContentValues contentValues=new ContentValues();
            contentValues.put(id,obj.getId());
            contentValues.put(cat,obj.getCategorie());
            database.insertOrThrow(TableName,null,contentValues);
            database.setTransactionSuccessful();
            database.endTransaction();
            return true;
        } catch (Exception e) {
            System.err.println(e);
            database.endTransaction();
            return  false;
        }
    }
    @Override
    public Collection<Categorie> Retrieve() {
        Set<Categorie> categories=new HashSet<>();
        SQLiteDatabase database=Connexion.getCon().getReadableDatabase();
        Cursor set=database.query(TableName,new String[]{id,cat},null,null,null,null,null);
        while (set.moveToNext())
        {
            Categorie categorie=extractCategorie(set);
            categories.add(categorie);
        }
        return categories;
    }
    @Override
    public boolean delete(Categorie obj) {
        return false;
    }
    private Categorie extractCategorie(Cursor set) {
        Categorie categorie=new Categorie();
        categorie.setCategorie(set.getString(1));
        categorie.setId(set.getInt(0));
        return categorie;
    }

}
