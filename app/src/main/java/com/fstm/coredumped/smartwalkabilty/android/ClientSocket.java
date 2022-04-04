package com.fstm.coredumped.smartwalkabilty.android;

import android.os.AsyncTask;

import com.fstm.coredumped.smartwalkabilty.android.deamon.DangerDaemon;
import com.fstm.coredumped.smartwalkabilty.android.deamon.RoutingHelper;
import com.fstm.coredumped.smartwalkabilty.android.model.bo.UserInfos;
import com.fstm.coredumped.smartwalkabilty.common.controller.DangerReq;
import com.fstm.coredumped.smartwalkabilty.common.controller.DeclareDangerReq;
import com.fstm.coredumped.smartwalkabilty.common.controller.RequestPerimetreAnnonce;
import com.fstm.coredumped.smartwalkabilty.common.controller.ShortestPathReq;
import com.fstm.coredumped.smartwalkabilty.common.controller.ShortestPathWithAnnounces;
import com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint;
import com.fstm.coredumped.smartwalkabilty.core.danger.bo.Danger;
import com.fstm.coredumped.smartwalkabilty.core.danger.bo.Declaration;
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

    public static String server="192.168.43.37";
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
        if(overlay.getMethod()==RoutingOverlay.METHOD_TWO_POINTS){
            ShortestPathReq shortestPathReq=new ShortestPathReq(depart,arrive);
            new RoutingTask(overlay).execute(shortestPathReq);
        }else if(overlay.getMethod()==RoutingOverlay.METHOD_ONE_POINTS) {
            ShortestPathWithAnnounces shortestPathReq1=new ShortestPathWithAnnounces(UserInfos.getInstance().getRadius(),depart,UserInfos.getInstance().getCats(),arrive);
            new RoutingAnnouncesTask(overlay).execute(shortestPathReq1);
        }
    }

    public void SendAnnoncesReq( GeoPoint point)
    {
        RequestPerimetreAnnonce perimetreReq=new RequestPerimetreAnnonce(UserInfos.getInstance().getRadius(),point,UserInfos.getInstance().getCats());
        new PerimetreTask().execute(perimetreReq);
    }
    public void SendDangerReq()
    {
        DangerReq dangerReq=new DangerReq(UserInfos.getInstance().getRadiusDanger(),UserInfos.getInstance().getCurrentLocation());
        new DangerTask().execute(dangerReq);
    }
    public void SendDeclareDangerReq(DeclareDangerActivity context, Danger danger)
    {
        DeclareDangerReq declareDangerReq=new DeclareDangerReq(danger,UserInfos.getInstance().getCurrentLocation());
        new DeclareDangerTask(context).execute(declareDangerReq);
    }

    class RoutingTask extends AsyncTask<ShortestPathReq,Void, List<Chemin>>{
        Overlay overlay;

        public RoutingTask(Overlay overlay) {
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
               new RoutingHelper(chemins,(RoutingOverlay) overlay,false).RoutingProcess();
            }
        }
    }
    class RoutingAnnouncesTask extends AsyncTask<ShortestPathWithAnnounces,Void, List<Chemin>>{
        Overlay overlay;

        public RoutingAnnouncesTask(Overlay overlay) {
            this.overlay = overlay;
        }
        @Override
        protected List<Chemin> doInBackground(ShortestPathWithAnnounces... shortestPathReqs) {
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
                new RoutingHelper(chemins,(RoutingOverlay)overlay).start();
            }
        }
    }
    class PerimetreTask extends AsyncTask<RequestPerimetreAnnonce,Void, List<Site>>{


        @Override
        protected List<Site> doInBackground(RequestPerimetreAnnonce... perimetreReqs) {
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
    class DangerTask extends AsyncTask<DangerReq,Void, List<Declaration>>{
        @Override
        protected List<Declaration> doInBackground(DangerReq... perimetreReqs) {
            try {
                Socket socket=ConnectToServer();
                ObjectOutputStream outputStream=new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream=new ObjectInputStream(socket.getInputStream());
                outputStream.writeObject(perimetreReqs[0]);
                outputStream.flush();
                List<Declaration> declarations= (List<Declaration>) objectInputStream.readObject();
                socket.close();
                outputStream.close();
                objectInputStream.close();
                return declarations;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(List<Declaration> declarations) {
            if(declarations!=null)
            {
                DangerDaemon.GetDangerDeamon().showDeclarations(declarations);
            }
        }
    }
    class DeclareDangerTask extends AsyncTask<DeclareDangerReq,Void, Boolean>{
        DeclareDangerActivity context;

        public DeclareDangerTask(DeclareDangerActivity context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(DeclareDangerReq... declareDangerReqs) {
            try {
                Socket socket=ConnectToServer();
                ObjectOutputStream outputStream=new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream=new ObjectInputStream(socket.getInputStream());
                outputStream.writeObject(declareDangerReqs[0]);
                outputStream.flush();
                Boolean b = (Boolean) objectInputStream.readObject();
                socket.close();
                outputStream.close();
                objectInputStream.close();
                return b;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(Boolean bol) {
            if(bol!=null)
            {
                context.Done(bol);
            }
            else context.Done(false);
        }
    }

}
