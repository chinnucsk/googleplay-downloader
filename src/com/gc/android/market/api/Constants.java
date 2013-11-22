package com.gc.android.market.api;

public class Constants {
    public static int maxAppIndex = 10;
   
    public static String apk_dir = "/tmp/apks";  // apk archive directory
            
    public static String url = "http://127.0.0.1:5000";

    public static String jsonExtension = ".json";

    public static String visuals = "/visuals";

    public static final String appsUrl = url + "/api/apps";

    public static final String appsUrlJson = appsUrl + jsonExtension;

    public static final String appDownloadingUrl = appsUrl + "/downloading" + jsonExtension;
    
    public static final String appDownloadedUrl = appsUrl + "/downloaded";
    
    public static final String appExistUrl = appsUrl + "/exists" + jsonExtension;

    public static final String appNextIdUrl = appsUrl + "/ids" + jsonExtension; 

    public static final String appPermissionUrl = url + "/app_permissions/update_permissions" + jsonExtension;

    public static final String appTargetUrl = url + "/app_targets" + jsonExtension;

    public static final String appTargetExistUrl = url + "/app_targets/exists" + jsonExtension;

    public static final String appCommentUrl = url + "/comments";

    public static final String appCommentUrlJson = url + "/comments" + jsonExtension;

    public static final String appCommentExistUrl = appCommentUrl + "/exists" + jsonExtension;

    public static final String partVisualExistUrl =  visuals + "/exists" + jsonExtension;

    public static final String newPermissionUrlJson = url + "/permissions" + jsonExtension;
}
