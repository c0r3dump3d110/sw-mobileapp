package com.fstm.coredumped.smartwalkabilty.android;

import android.os.AsyncTask;

import com.fstm.coredumped.smartwalkabilty.android.deamon.RoutingHelper;
import com.fstm.coredumped.smartwalkabilty.android.model.bo.UserInfos;
import com.fstm.coredumped.smartwalkabilty.common.controller.PerimetreReq;
import com.fstm.coredumped.smartwalkabilty.common.controller.ShortestPathReq;
import com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint;
import com.fstm.coredumped.smartwalkabilty.core.routing.model.bo.Chemin;
import com.fstm.coredumped.smartwalkabilty.web.Model.bo.Site;
import com.fstm.coredumped.smartwalkabilty.web.Model.dao.Connexion;
import com.fstm.coredumped.smartwalkabilty.web.Model.dao.DAOSite;

import org.osmdroid.views.overlay.Overlay;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class ClientSocket
{

    public static String server="192.168.1.100";
    public static int port=1337;
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
    public void SendRoutingReq(RoutingOverlay overlay , GeoPoint depart, GeoPoint arrive)
    {
        ShortestPathReq shortestPathReq=new ShortestPathReq(UserInfos.getInstance().getRadius(),depart,arrive);
        new Routing(overlay).execute(shortestPathReq);
    }
    public void SendAnnoncesReq( GeoPoint point)
    {
        PerimetreReq perimetreReq=new PerimetreReq(UserInfos.getInstance().getRadius(),point);
        perimetreReq.getCategorie().addAll(UserInfos.getInstance().getCats());
        new Perimetre().execute(perimetreReq);
    }

    class Routing extends AsyncTask<ShortestPathReq,Void, List<Chemin>>{
        Overlay overlay;

        public Routing(Overlay overlay) {
            this.overlay = overlay;
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
    class Perimetre extends AsyncTask<PerimetreReq,Void, List<Site>>{


        @Override
        protected List<Site> doInBackground(PerimetreReq... perimetreReqs) {
            try {
                Socket socket=ConnectToServer();
                ObjectOutputStream outputStream=new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream=new ObjectInputStream(socket.getInputStream());
                outputStream.writeObject(perimetreReqs[0]);
                outputStream.flush();
                List<Site> sites= (List<Site>) objectInputStream.readObject();
                socket.close();
                outputStream.close();
                objectInputStream.close();
                return sites;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(List<Site> sites) {
            Connexion.getCon().ClearDB();
            if(sites!=null)
            {
                for (Site site : sites) {
                    DAOSite.getDaoSite().Create(site);
                }
            }
        }
    }

}
