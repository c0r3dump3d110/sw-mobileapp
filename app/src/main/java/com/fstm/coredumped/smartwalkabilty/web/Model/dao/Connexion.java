package com.fstm.coredumped.smartwalkabilty.web.Model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Connexion extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String databaseName = "SMARTWALK_DB";
    private static  Connexion conn=null;
    public static Connexion getCon()
    {
        return conn;
    }
    public static void ConstructDb(Context context)
    {
        conn=new Connexion(context);
    }
    Connexion(Context context) {
        super(context,databaseName , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String DATABASE_TABLE_Announce_CREATE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY,%s TEXT,%s TEXT,%s TEXT,%s TEXT,%s TEXT,%s INTEGER,%s TEXTTIME);", AnnounceTable.TableName, AnnounceTable.id, AnnounceTable.dateD, AnnounceTable.dateF, AnnounceTable.titre, AnnounceTable.description, AnnounceTable.urlPrincipalImage, AnnounceTable.categorie, AnnounceTable.dateInserted);
        String DATABASE_TABLE_Site_CREATE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY,%s TEXT,%s REAL,%s REAL,%s TEXT,%s TEXT);",SiteTable.TableName,SiteTable.id,SiteTable.Name,SiteTable.localisationX,SiteTable.localisationY,SiteTable.Organisation,SiteTable.dateInserted);
        String DATABASE_TABLE_Images_CREATE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY,%s TEXT,%s INTEGER);",ImagesTable.TableName,ImagesTable.id,ImagesTable.lien,ImagesTable.Announce);
        String DATABASE_TABLE_AS_CREATE = String.format("CREATE TABLE %s ( %s INTEGER,%s INTEGER );",A_S_Table.TableName,A_S_Table.Announce,A_S_Table.Site);
        sqLiteDatabase.execSQL(DATABASE_TABLE_Announce_CREATE);
        sqLiteDatabase.execSQL(DATABASE_TABLE_Site_CREATE);
        sqLiteDatabase.execSQL(DATABASE_TABLE_Images_CREATE);
        sqLiteDatabase.execSQL(DATABASE_TABLE_AS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    { }
    public void ClearDB(){
       SQLiteDatabase database=  conn.getWritableDatabase();
       try {
           database.beginTransaction();
           database.delete(AnnounceTable.TableName,null,null);
           database.delete(A_S_Table.TableName,null,null);
           database.delete(SiteTable.TableName,null,null);
           database.delete(ImagesTable.TableName,null,null);
           database.setTransactionSuccessful();
           database.endTransaction();
       }catch (Exception e){
           database.endTransaction();
           System.err.println(e);
       }
    }
}
interface AnnounceTable
{
    String TableName="announces";
    String id="id";
    String dateD="TEXTDebut";
    String dateF="TEXTFin";
    String titre="titre";
    String description="description";
    String urlPrincipalImage="urlPrincipalImage";
    String categorie="id_cat";
    String dateInserted ="dateinser";
}
interface SiteTable
{
    String TableName="sites";
    String id="id";
    String Name="name";
    String Organisation="organisation";
    String localisationX="Longitude";
    String localisationY="Latitude";
    String dateInserted ="dateinser";
}
interface ImagesTable
{
    String TableName="images";
    String id="id";
    String lien="link";
    String Announce="id_announce";
}

interface A_S_Table
{
    String TableName="announces_con_site";
    String Announce="id_announce";
    String Site="id_site";
}