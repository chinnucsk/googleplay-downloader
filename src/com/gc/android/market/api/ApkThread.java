package com.gc.android.market.api;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import com.gc.android.market.api.Sender.ApkResponse;
import com.gc.android.market.api.model.Market.GetAssetResponse.InstallAsset;

public class ApkThread extends Thread {

    protected MarketSession session;
    protected Sender sender;
    protected ApkResponse apkResponse;
    protected final int sleepTime = 600000;

    public ApkThread(MarketSession session, ApkResponse apkResponse, Sender sender) {
        this.session = session;
        this.apkResponse = apkResponse;
        this.sender = sender;
    }
    
    @Override
    public void run() {
        try {
            System.out.println("size:" + apkResponse.apks.size());
   
            for (int i = 0; i < apkResponse.apks.size(); i++) {
                System.out.println("apk:" + apkResponse.apks.get(i).app_id);
                
                String assetId = apkResponse.apks.get(i).app_id;
                String fileToSave = Constants.apk_dir + "/" + assetId + ".apk";

                try {
                    InstallAsset ia = session.queryGetAssetRequest(assetId).getInstallAsset(0);
                    String cookieName = ia.getDownloadAuthCookieName();
                    String cookieValue = ia.getDownloadAuthCookieValue();
                    URL url;
                    url = new URL(ia.getBlobUrl());

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", "Android-Market/2 (sapphire PLAT-RC33); gzip");
                    conn.setRequestProperty("Cookie", cookieName + "=" + cookieValue);

                    InputStream inputstream =  (InputStream) conn.getInputStream();
                    System.out.println("Downloading " + fileToSave);
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(fileToSave));
                    byte buf[] = new byte[1024];
                    int k = 0;
                    for(long l = 0L; (k = inputstream.read(buf)) != -1; l += k )
                        stream.write(buf, 0, k);
                    inputstream.close();
                    stream.close();

                    sender.saveApk(assetId);
                    
                    // Sleep for 10 ~ 30 Minutes
                    sleep(sleepTime + new Random().nextInt(1200000));
                    
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            //sender.closeConnection();
            System.out.println("Thread execution completed");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
