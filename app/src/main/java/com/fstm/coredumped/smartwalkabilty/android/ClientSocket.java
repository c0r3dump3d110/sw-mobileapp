package com.fstm.coredumped.smartwalkabilty.android;

import android.os.AsyncTask;

import com.fstm.coredumped.smartwalkabilty.common.controller.ShortestPathReq;
import com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint;
import com.fstm.coredumped.smartwalkabilty.routing.model.bo.Chemin;

import org.osmdroid.views.overlay.Overlay;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClientSocket
{
    Overlay overlay;
    public static String server="192.168.1.100";
    public static int port=1337;
    public void SendRoutingReq(MyTouchOverlay overlay ,GeoPoint depart,GeoPoint arrive)
    {
        this.overlay=overlay;
        ShortestPathReq shortestPathReq=new ShortestPathReq(15,depart,arrive);
        new Routing().doInBackground(shortestPathReq);
    }


    public Socket ConnectToServer(){
        try {
            InetAddress address=InetAddress.getByAddress(server.getBytes(StandardCharsets.UTF_8));
            Socket socket= new Socket(address,port);
            return socket;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    class Routing extends AsyncTask<ShortestPathReq,Void, List<Chemin>>{
        @Override
        protected List<Chemin> doInBackground(ShortestPathReq... shortestPathReqs) {
            try {
                Socket socket=ConnectToServer();
                ObjectOutputStream outputStream=new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream=new ObjectInputStream(socket.getInputStream());
                outputStream.writeObject(shortestPathReqs[0]);
                outputStream.flush();
                return (List<Chemin>) objectInputStream.readObject();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Chemin> chemins) {
            MyTouchOverlay touchOverlay=(MyTouchOverlay) overlay;
            touchOverlay.
        }
    }


}
