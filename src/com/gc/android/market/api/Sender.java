package com.gc.android.market.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;

import com.gc.android.market.api.model.Market.App;
import com.gc.android.market.api.model.Market.AppsResponse;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class Sender {
    /**
     * A <code>HttpPut</code> object for making HTTP Put requests
     */
    private HttpPut httpPut;

    /**
     * A <code>HttpPost</code> object for making HTTP Post requests
     */
    private HttpPost httpPost;

    /**
     * A <code>HttpClient</code> object
     */
    private HttpClient httpClient;

    /**
     * Constructs a <code>Sender</code>
     */
    public Sender() {
        this.httpPut = new HttpPut();
        this.httpPost = new HttpPost();

        this.httpClient = new DefaultHttpClient();
        this.httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpConnectionParams.setConnectionTimeout(this.httpClient.getParams(), 20000);

    }

    /**
     * Check if an entry for app by the package name passed as the parameter
     * already exists in the database.
     * 
     * @param packageName
     *            the existence of packageName to check
     * @return <code>ExistResponse</code> containing the id of app in the
     *         database if it exists.
     * 
     * @throws ConnectivityException
     *             thrown if there was a problem connecting with the database
     */
    public ExistResponse appExists(String packageName) {
        ExistResponse existResponse = null;

        AppQuery query = new AppQuery();
        query.packageName = packageName;

        byte[] response = doBasicHttpPost(query.toString(), Constants.appExistUrl);

        InputStream is = new ByteArrayInputStream(response);
        Reader reader = new InputStreamReader(is);

        try {
            existResponse = new Gson().fromJson(reader, ExistResponse.class);
        } catch (JsonSyntaxException jse) {
            jse.printStackTrace();
        } catch (JsonIOException jio) {
            jio.printStackTrace();
        }

        return existResponse;
    }

    /**
     * Save android app details to a Rails app.
     * 
     * @param data
     *            the app data in jSon formate
     * @return an internal id assigned by Rails to the app.
     * @throws ConnectivityException
     *             thrown if there was a problem connecting with the database
     */
    public AppSaveResponse saveApp(String data) {
        AppSaveResponse appResponse = null;
        byte[] response = doBasicHttpPost(data, Constants.appsUrlJson);

        InputStream is = new ByteArrayInputStream(response);
        Reader reader = new InputStreamReader(is);

        try {
            appResponse = new Gson().fromJson(reader, AppSaveResponse.class);
        } catch (JsonSyntaxException jse) {
            jse.printStackTrace();
        } catch (JsonIOException jio) {
            jio.printStackTrace();
        }

        return appResponse;
    }

    public void saveApk(String app_id) {
        doBasicHttpPut("app_id=" + app_id, Constants.appDownloadedUrl + "/" + app_id + Constants.jsonExtension);
        /*
        DownloadedResponse downloadedResponse = null;
        byte[] response = doBasicHttpPut("app_id=" + app_id, Constants.appDownloadedUrl + app_id + Constants.jsonExtension);

        InputStream is = new ByteArrayInputStream(response);
        Reader reader = new InputStreamReader(is);

        try {
            downloadedResponse = new Gson().fromJson(reader, DownloadedResponse.class);
        } catch (JsonSyntaxException jse) {
            jse.printStackTrace();
        } catch (JsonIOException jio) {
            jio.printStackTrace();
        }

        return downloadedResponse;
        */
    }

    public CategoriesResponse getCategories() {
        CategoriesResponse categoriesResponse = null;
        byte[] response = doBasicHttpPost("", Constants.url + "/api/categories" + Constants.jsonExtension);

        InputStream is = new ByteArrayInputStream(response);
        Reader reader = new InputStreamReader(is);

        try {
            categoriesResponse = new Gson().fromJson(reader, CategoriesResponse.class);
        } catch (JsonSyntaxException jse) {
            jse.printStackTrace();
        } catch (JsonIOException jio) {
            jio.printStackTrace();
        }
        
        return categoriesResponse;
    }
    

    public ApkResponse getApkFileInfos() {
        ApkResponse apkResponse = null;
        byte[] response = doBasicHttpPost("", Constants.appDownloadingUrl);

        InputStream is = new ByteArrayInputStream(response);
        Reader reader = new InputStreamReader(is);

        try {
            apkResponse = new Gson().fromJson(reader, ApkResponse.class);
        } catch (JsonSyntaxException jse) {
            jse.printStackTrace();
        } catch (JsonIOException jio) {
            jio.printStackTrace();
        }
        
        return apkResponse;
    }
    
    /**
     * Saves app details, the android sdk it targets and the required permission
     * 
     * @param appsResponse
     *            the <code>AppResponse</code> object to save
     * @param sdkVersion
     *            the target sdk by app
     * @throws ConnectivityException
     *             thrown if there was a problem connecting with the database
     */
    public void addAppToCollection(AppsResponse appsResponse, int sdkVersion) {
        for (int appIndex = 0; appIndex < appsResponse.getAppCount(); appIndex++) {

            int appId;

            // Check if an entry already exists
            ExistResponse existResponse = appExists(appsResponse.getApp(appIndex).getPackageName());
            String data = convertAppToJsonString(appsResponse.getApp(appIndex));

            if (existResponse.exists) {
                appId = existResponse.id;
                doBasicHttpPut(data, Constants.appsUrl + "/" + appId + Constants.jsonExtension);

            } else {
                AppSaveResponse appResponse = saveApp(data);
                appId = appResponse.app.id;
                String assetId = appResponse.app.appId;
                
                System.out.println("save:" + assetId);
            }

            System.out.println("Adding app " + appsResponse.getApp(appIndex).getTitle() + " to database");
            
            /*
            AppTargetExistResponse appTarget = appTargetExists(appId, sdkVersion);

            if (!appTarget.exists) {
                Target target = new Target();
                target.app_target.app_id = appId;
                target.app_target.target_id = sdkVersion;
                doBasicHttpPost(target.toString(), Constants.appTargetUrl);
            }

            Permissions permissions = new Permissions();
            permissions.app_id = appId;
            permissions.permissions = Utils.permissionToInt(appsResponse.getApp(appIndex).getExtendedInfo().getPermissionIdList());

            doBasicHttpPost(permissions.toString(), Constants.appPermissionUrl);
            */
        }
    }
    
    /**
     * <code>AppQuery</code> is used to make packageName query against the
     * database
     * 
     * @author raunak
     * @version 1.0
     */
    class AppQuery {

        /**
         * The name of package (for app) to package into a query.
         */
        public String packageName;

        @Override
        public String toString() {
            return Utils.gson.toJson(this);
        }
    }

    /**
     * Perform a simple HTTP Put request
     * 
     * @param data
     *            information that needs to be sent
     * @param url
     *            the location to send the information to
     * @throws ConnectivityException
     *             thrown if there was a problem connecting with the database
     */
    public void doBasicHttpPut(String data, String url) {
        HttpResponse httpResponse = null;
        StringEntity stringEntity = null;

        try {
            httpPut = new HttpPut();
            httpPut.setURI(new URI(url));
            httpPut.setHeader("Content-Type", "application/json");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            stringEntity = new StringEntity(data, "UTF-8");

            if (stringEntity != null) {
                httpPut.setEntity(stringEntity);
                httpResponse = httpClient.execute(httpPut);
                HttpEntity entity = httpResponse.getEntity();

                if (entity != null) {
                    entity.getContent().close();
                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Perform a simple HTTP Post request
     * 
     * @param data
     *            information that needs to be sent
     * @param url
     *            the location to send the information to
     * @return the response returned by Rails app
     * @throws ConnectivityException
     *             thrown if there was a problem connecting with the database
     */
    public byte[] doBasicHttpPost(String data, String url) {
        byte[] response = null;
        HttpResponse httpResponse = null;
        StringEntity stringEntity = null;

        try {
            httpPost = new HttpPost();
            httpPost.setURI(new URI(url));
            httpPost.addHeader("Content-Type", "application/json");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            stringEntity = new StringEntity(data, "UTF-8");

            if (stringEntity != null) {
                httpPost.setEntity(stringEntity);
                httpResponse = httpClient.execute(httpPost);

                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    InputStream contentStream = entity.getContent();
                    response = IOUtils.toByteArray(contentStream);
                    contentStream.close();
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Perform a complex HTTP Put (send files over the network)
     * 
     * @param data
     *            file that needs to be sent
     * @param mimeType
     *            the content type of file
     * @param filename
     *            the name of file
     * @param url
     *            the location to send the file to
     * @throws ConnectivityException
     *             thrown if there was a problem connecting with the database
     */
    public void doComplexHttpPut(byte[] data, String mimeType, String filename, String url) {
        HttpResponse httpResponse = null;

        try {
            httpPut = new HttpPut();
            httpPut.setURI(new URI(url));
            httpPut.addHeader("content_type", "image/jpeg");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ContentBody contentBody = new ByteArrayBody(data, mimeType, filename);

        MultipartEntity multipartEntity = new MultipartEntity();
        multipartEntity.addPart("image", contentBody);

        httpPut.setEntity(multipartEntity);

        try {
            httpResponse = httpClient.execute(httpPut);
            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {
                entity.getContent().close();
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Perform a complex HTTP Post (send files over the network)
     * 
     * @param data
     *            file that needs to be sent
     * @param mimeType
     *            the content type of file
     * @param filename
     *            the name of file
     * @param url
     *            the location to send the file to
     * @throws ConnectivityException
     *             thrown if there was a problem connecting with the database
     */
    public void doComplexHttpPost(byte[] data, String mimeType, String filename, String url) {
        HttpResponse httpResponse = null;

        try {
            httpPost = new HttpPost();
            httpPost.setURI(new URI(url));
            httpPost.addHeader("content_type", "image/jpeg");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ContentBody contentBody = new ByteArrayBody(data, mimeType, filename);

        MultipartEntity multipartEntity = new MultipartEntity();
        multipartEntity.addPart("image", contentBody);

        httpPost.setEntity(multipartEntity);

        try {
            httpResponse = httpClient.execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {
                entity.getContent().close();
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Converts a Market.App object to json string
     * 
     * @param app
     *            the <code>Market.App</code> to convert
     * @return jSon representation of <code>Market.App</code>
     */
    public String convertAppToJsonString(App app) {
        AppInfo appInfo = new AppInfo();
        
        appInfo.appId = app.getId();
        appInfo.title = app.getTitle();
        appInfo.appType = app.getAppType().toString();
        appInfo.creator = app.getCreator();
        appInfo.creatorId = app.getCreatorId();
        appInfo.packageName = app.getPackageName();
        appInfo.version = app.getVersion();
        appInfo.versionCode = app.getVersionCode();
        appInfo.rating = app.getRating();
        appInfo.promoText = app.getExtendedInfo().getPromoText();
        appInfo.ratingsCount = app.getRatingsCount();
        appInfo.installSize = app.getExtendedInfo().getInstallSize();
        appInfo.downloadsCountText = app.getExtendedInfo().getDownloadsCountText();
        appInfo.permissionIdList = app.getExtendedInfo().getPermissionIdList().toString();
        appInfo.permissionNum = app.getExtendedInfo().getPermissionIdCount();
        appInfo.category = app.getExtendedInfo().getCategory();
        appInfo.contactEmail = app.getExtendedInfo().getContactEmail();
        appInfo.contactWebsite = app.getExtendedInfo().getContactWebsite();
        appInfo.recentChanges = app.getExtendedInfo().getRecentChanges();
        appInfo.description = app.getExtendedInfo().getDescription();
        appInfo.priceCurrency = app.getPriceCurrency();
        
        return new Gson().toJson(appInfo);
    }

    /**
     * The response returned from a rails app after saving an app. "{app:{id:1}"
     * <code>AppSaveResponse</code> is used to convert the reponse from json to
     * java object.
     * 
     * @author raunak
     * @version 1.0
     */
    static class AppSaveResponse {
        public App app;

        public AppSaveResponse() {
            app = new App();
        }

        public static class App {
            public int id;
            public String appId;
        }
    }

    static class CategoriesResponse {
        public List<CategoryState> categories;
    }
    
    static class CategoryState {
        public String real_name;
        public String name;
        public Boolean state;
    }
    
    static class ApkResponse {
        public Integer error;
        public List<ApkFileInfo> apks;
    }
    
    static class ApkFileInfo {
        public String app_id;
    }
    
    static class DownloadedResponse {
        public String app_id;
        public boolean done;
    }
    
    /**
     * <code>ExistResponse</code> essentially holds the id of app in the
     * database if the app exists.
     * 
     * @author raunak
     * @version 1.0
     */
    static class ExistResponse {

        /**
         * The app id in the database. </br> <Strong>Note:</Strong> Only gets
         * assigned if the apps exists in database.
         */
        public int id;

        /**
         * a boolean flag. true if app exists in database; false otherwise
         */
        public boolean exists;
    }

    /**
     * <code>AppTargetExistResponse</code> holds a boolean flag. The flag is set
     * to true if the target exists for an app, false otherwise.
     * 
     * @author raunak
     * @version 1.0
     */
    static class AppTargetExistResponse {

        /**
         * a boolean flag; true if target exists, false otherwise.
         */
        public boolean exists;
    }

    /**
     * Close HTTP connection
     */
    public void closeConnection(){
        this.httpClient.getConnectionManager().shutdown();
    }

}

class AppInfo {
    public String appId;
    public String title;
    public String appType;
    public String creator;
    public String creatorId;
    public String packageName;
    public String version;
    public int versionCode;
    public String rating;
    public String promoText;
    public int ratingsCount;
    public int installSize;
    public String downloadsCountText;
    public String permissionIdList;
    public int permissionNum;
    public String category;
    public String contactEmail;
    public String contactWebsite;
    public String recentChanges;
    public String description;
    public String priceCurrency;
}
