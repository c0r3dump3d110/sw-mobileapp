package com.fstm.coredumped.smartwalkabilty.android;

import android.os.AsyncTask;

import com.fstm.coredumped.smartwalkabilty.android.deamn.RoutingHelper;
import com.fstm.coredumped.smartwalkabilty.common.controller.ShortestPathReq;
import com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint;
import com.fstm.coredumped.smartwalkabilty.core.routing.model.bo.Chemin;

import org.osmdroid.views.overlay.Overlay;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class ClientSocket
{
    Overlay overlay;

    public void SendRoutingReq(RoutingOverlay overlay , GeoPoint depart, GeoPoint arrive)
    {
        this.overlay=overlay;
        ShortestPathReq shortestPathReq=new ShortestPathReq(15,depart,arrive);
        new Routing().execute(shortestPathReq);
    }

    class Routing extends AsyncTask<ShortestPathReq,Void, List<Chemin>>{
        public String server="192.168.1.100";
        public int port=1337;
        public Socket ConnectToServer(){
            try {
                InetAddress address=InetAddress.getByName(server);
                Socket socket= new Socket(address,port);
                return socket;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected List<Chemin> doInBackground(ShortestPathReq... shortestPathReqs) {
            try {
                Socket socket=ConnectToServer();
                ObjectOutputStream outputStream=new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream=new ObjectInputStream(socket.getInputStream());
                outputStream.writeObject(shortestPathReqs[0]);
                outputStream.flush();
                List<Chemin> chemins= (List<Chemin>) objectInputStream.readObject();
                socket.close();
                outputStream.close();
                objectInputStream.close();
                return chemins;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(List<Chemin> chemins) {
            if(chemins!=null)
            {
                if(((RoutingOverlay)overlay).getMethod()==RoutingOverlay.METHOD_ONE_POINTS) new RoutingHelper(chemins,(RoutingOverlay)overlay).start();
                else if(((RoutingOverlay)overlay).getMethod()==RoutingOverlay.METHOD_TWO_POINTS) new RoutingHelper(chemins,(RoutingOverlay) overlay).RoutingProcess();
            }
        }
    }


}
