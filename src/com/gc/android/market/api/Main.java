package com.gc.android.market.api;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gc.android.market.api.model.Market.AppsRequest;
import com.gc.android.market.api.model.Market.AppsResponse;
import com.gc.android.market.api.model.Market.GetAssetResponse.InstallAsset;

public class Main {

    private static void usage() {
        System.out.println("Usage :\n" +
                "market <testId> \n" +
                "\ttestId : 1 to app info" +
                "\ttestId : 3 to apk download" +
                "\ttestId : 4 to crawl apk" +
                "\ttestId : 5 to crawl category");
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            if(args.length < 1) {
                usage();
                return;
            }

            User[] users = new Secure().getUsers();
            Device device = DeviceInventory.GINGERBREAD_UPDATE1;
            String login = users[0].getUsername();
            String password = users[0].getPassword();
            String androidId = "00000000000000000000"; //real id //device.getMarketId();

            Integer testId = new Integer(args[0]);
            String assetId = args[1];
            switch (testId) {
            case 1:
                login = users[1].getUsername();
                password = users[1].getPassword();
                showAppInfo(login, password, androidId, assetId);
                break;
            case 3:
                login = users[1].getUsername();
                password = users[1].getPassword();
                apkDownload(login, password, androidId, assetId);
                break;
            case 4:
                login = users[1].getUsername();
                password = users[1].getPassword();
                crawlApk(login, password, androidId, assetId);
                break;
            case 5:
                crawlCategory(login, password, androidId, assetId, "");
                break;
            default :
                usage();
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void showAppInfo(String login, String password, 
            String androidId, String assetId)
    {
        try {
            MarketSession session = new MarketSession(false);
            List<Object> rg = new ArrayList<Object>();
            session.login(login,password, androidId);

            AppsRequest appsRequest = AppsRequest.newBuilder()
                    .setAppId(assetId)
                    .setStartIndex(1)
                    .setEntriesCount(10)
                    .setWithExtendedInfo(true)
                    .build();

            rg.addAll(session.queryApp(appsRequest));
            for(int j = 0; j < rg.size(); ++j) {
                AppsResponse apps = (AppsResponse)rg.get(j);
                if (apps.getAppCount() > 0) {
                    System.out.println("id: " + apps.getApp(0).getId());
                    System.out.println("packagename: " + apps.getApp(0).getPackageName());
                }
            }  
        } catch(Exception ex) {
            ex.printStackTrace();
        }       
    }

    private static void crawlCategory(String login, String password, 
            String androidId, String assetId, String query)
    {
        try {
            MarketSession session = new MarketSession(false);
            session.login(login, password, androidId);
            session.setLocale(Locale.JAPAN);
            session.setOperator("NTT DOCOMO", "44010");
            session.getContext().setDeviceAndSdkVersion("nexuss:10");

            Sender sender = new Sender();
            Sender.CategoriesResponse categoriesResponse = sender.getCategories();
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            for (int i = 0; i < categoriesResponse.categories.size(); i++) {
                if (categoriesResponse.categories.get(i).state == true) {
                    System.out.println("response:" + categoriesResponse.categories.get(i).real_name);
                    executorService.submit(new AppThread(session, categoriesResponse.categories.get(i).real_name, new Fetcher()));
                }
            }
            
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
        }       
    }

    private static void crawlApk(String login, String password, 
            String androidId, String assetId)
    {
        try {
            MarketSession session = new MarketSession(true);
            session.login(login, password, androidId);
            session.setLocale(Locale.JAPAN);
            session.setOperator("NTT DOCOMO", "44010");
            session.getContext().setDeviceAndSdkVersion("nexuss:10");

            Sender sender = new Sender();
            Sender.ApkResponse apkResponse = sender.getApkFileInfos();
            
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            executorService.submit(new ApkThread(session, apkResponse, new Sender()));
            
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
        }       
    }

    private static void apkDownload(String login, String password, 
            String androidId, String assetId)
    {
        try {
            MarketSession session = new MarketSession(true);
            session.login(login, password, androidId);
            session.setLocale(Locale.JAPAN);
            session.setOperator("NTT DOCOMO", "44010");
            session.getContext().setDeviceAndSdkVersion("nexuss:10");

            InstallAsset ia = session.queryGetAssetRequest(assetId).getInstallAsset(0);
            String cookieName = ia.getDownloadAuthCookieName();
            String cookieValue = ia.getDownloadAuthCookieValue();
            URL url = new URL(ia.getBlobUrl());

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Android-Market/2 (sapphire PLAT-RC33); gzip");
            conn.setRequestProperty("Cookie", cookieName + "=" + cookieValue);

            InputStream inputstream =  (InputStream) conn.getInputStream();
            String fileToSave = assetId + ".apk";
            System.out.println("Downloading " + fileToSave);
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(fileToSave));
            byte buf[] = new byte[1024];
            int k = 0;
            for(long l = 0L; (k = inputstream.read(buf)) != -1; l += k )
                stream.write(buf, 0, k);
            inputstream.close();
            stream.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }       
    }
}
