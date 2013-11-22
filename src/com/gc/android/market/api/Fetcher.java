package com.gc.android.market.api;

import com.gc.android.market.api.MarketSession.Callback;
import com.gc.android.market.api.model.Market;
import com.gc.android.market.api.model.Market.AppsRequest;
import com.gc.android.market.api.model.Market.AppsResponse;
import com.gc.android.market.api.model.Market.ResponseContext;
import com.gc.android.market.api.model.Market.AppsRequest.OrderType;
import com.gc.android.market.api.model.Market.AppsRequest.ViewType;

public class Fetcher {
    private volatile AppsResponse appResponse;

    synchronized public AppsResponse getAppByCategory(MarketSession marketSession, String categoryName, int startIndex) {
        this.appResponse = null;

        AppsRequest appsRequest = AppsRequest.newBuilder()
                .setCategoryId(categoryName)
                .setStartIndex(startIndex)
                .setEntriesCount(10)
                .setWithExtendedInfo(true)
                .setOrderType(OrderType.POPULAR)
                .setViewType(ViewType.FREE)
                .build();

        marketSession.append(appsRequest, new Callback<Market.AppsResponse>() {
            @Override
            public void onResult(ResponseContext context, AppsResponse response) {
                setAppsResponse(response);
            }

        });

        marketSession.flush();
        
        return getAppsResponse();
    }

    private void setAppsResponse(AppsResponse response) {
        AppsResponse ar = (AppsResponse) response;
        if (ar.getAppCount() < 1) {
            System.out.println("{\"\" : 1}");
            return;
        }
        System.out.println(""+ar.getAppCount());

        System.out.println("##");
        
        this.appResponse = response;
    }
    
    private AppsResponse getAppsResponse() {
        return appResponse;
    }
}
