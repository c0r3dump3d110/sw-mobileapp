package com.fstm.coredumped.smartwalkabilty.android.deamon;

import com.fstm.coredumped.smartwalkabilty.android.ClientSocket;
import com.fstm.coredumped.smartwalkabilty.android.model.bo.UserInfos;

public class AnnonceDeamon_noRouting extends Thread{
    @Override
    public void run() {
        try {
            Thread.sleep(60000);
            while(true){
                if (!UserInfos.getInstance().isRouting()){
                    new ClientSocket().SendAnnoncesReq(UserInfos.getInstance().getCurrentLocation());
                }
                Thread.sleep(120000);
            }
        } catch (InterruptedException e) {
            return;
        }
    }
}
