package com.fstm.coredumped.smartwalkabilty.web.Model.dao;

import com.fstm.coredumped.smartwalkabilty.web.Model.bo.Annonce;
import com.fstm.coredumped.smartwalkabilty.web.Model.bo.Image;
import static com.fstm.coredumped.smartwalkabilty.web.Model.dao.ImagesTable.*;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DAOImage implements IDAO<Image>{
    private static DAOImage daoImage=null;
    public static DAOImage getDAOImage(){
        if(daoImage==null)daoImage=new DAOImage();
        return daoImage;
    }
    private DAOImage(){
    }
    @Override
    public boolean Create(Image obj) {
        SQLiteDatabase database=Connexion.getCon().getReadableDatabase();
        database.beginTransaction();
        try
        {
            ContentValues contentValues=new ContentValues();
            contentValues.put(id,obj.getId());
            contentValues.put(Announce,obj.getAnnonce().getId());
            contentValues.put(lien,obj.getUrlImage());
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
    public Collection<Image> Retrieve() {
        return null;
    }
    @Override
    public boolean delete(Image obj) {
        return false;
    }
    public void findImagesByAnnonce(Annonce annonce) throws SQLException {
        SQLiteDatabase database=Connexion.getCon().getReadableDatabase();
        database.beginTransaction();
        try {
            Cursor set= database.query(TableName,new String[]{id,lien},Announce+"=?",new String[]{String.valueOf(annonce.getId())},null,null,null);
            while (set.moveToNext()){
                Image img= extractImage(set);
                img.setAnnonce(annonce);
                annonce.AddImage(img);
            }
            database.setTransactionSuccessful();
            database.endTransaction();
        }catch (Exception e){
            System.err.println(e);
            database.endTransaction();
        }
    }
    private Image extractImage(Cursor set) throws SQLException {
        Image image=new Image();
        image.setUrlImage(set.getString(1));
        image.setId(set.getInt(0));
        return image;
    }

}
