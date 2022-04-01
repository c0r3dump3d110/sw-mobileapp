package com.fstm.coredumped.smartwalkabilty.android.deamon;

import com.fstm.coredumped.smartwalkabilty.android.ClientSocket;
import com.fstm.coredumped.smartwalkabilty.android.model.bo.UserInfos;

public class RoutingChecker extends Thread{
    @Override
    public void run() {
        try {
            Thread.sleep(180000);
            while(true){
                Thread.sleep(300000);
                if (!UserInfos.getInstance().isRouting()){
                    new ClientSocket().SendAnnoncesReq(UserInfos.getInstance().getCurrentLocation());
                }
            }
        } catch (InterruptedException e) {
            return;
        }
    }
}
