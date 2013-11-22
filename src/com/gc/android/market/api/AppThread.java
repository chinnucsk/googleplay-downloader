package com.gc.android.market.api;

import java.util.Random;

import com.gc.android.market.api.model.Market.AppsResponse;

public class AppThread extends Thread {

    /**
     * Starting index for marketplace query.
     */
    protected int startIndex;

    /**
     * Maximum number the start index can reach
     */
    protected int maxAppIndex;

    /**
     * the fetcher object used to fetch app(s)
     */
    protected Fetcher fetcher;

    /**
     * the sender object used to save app(s) to database
     */
    protected Sender sender;

    /**
     * the session object used to fetch app(s)
     */
    protected MarketSession session;

    protected String category;
    
    /**
     * Number of attempts the current query has made so far
     */
    protected int attempts;

    /**
     * Maximum number of attempts a query can make
     */
    protected final int maxAttempts = 3;

    /**
     * Sleep time between each marketplace query.
     */
    protected final int sleepTime = 600000;

    /**
     * Constructs <code>AppThread</code>using the passed parameters
     * 
     * @param session
     *            the session object to use for fetching the app(s)
     */
    public AppThread(MarketSession session) {
        this(session, "", new Fetcher(), new Sender());
        init();
    }

    public AppThread(MarketSession session, String category, Fetcher fetcher) {
        this(session, category, fetcher, new Sender());
        init();
    }

    /**
     * Constructs <code>AppThread</code>using the passed parameters
     * 
     * @param session
     *            the session object to use for fetching the app(s)
     * @param fetcher
     *            the fetcher object to use for fetching the app(s)
     * @param sender
     *            the sender object to use for saving the app(s) to database
     */
    public AppThread(MarketSession session, String category, Fetcher fetcher, Sender sender) {
        this.session = session;
        this.category = category;
        this.fetcher = fetcher;
        this.sender = sender;
        init();
    }
    
    private void init() {
        this.maxAppIndex = Constants.maxAppIndex;
    }

    @Override
    public void run() {
        try {
            System.out.println("start:" + startIndex);
            System.out.println("maxAppIndex:" + maxAppIndex);
   
            while (startIndex < maxAppIndex) {

                AppsResponse appsResponse = fetcher.getAppByCategory(session, category, startIndex);

                if ((appsResponse != null) && (attempts < maxAttempts)) {
                    try {
                        sender.addAppToCollection(appsResponse, DeviceInventory.GINGERBREAD_UPDATE1.getDeviceVersion());
                        startIndex += appsResponse.getAppCount();
                        attempts = 0;
                        System.out.println("startIndex:" + startIndex);
                    } catch (Exception ce) {
                        ce.printStackTrace();
                    }

                } else if (attempts >= maxAttempts) {
                    startIndex += 10;
                } else {
                    attempts++;
                }
                // Sleep for 10 ~ 30 Minutes
                sleep(sleepTime + new Random().nextInt(1200000));
            }

            //sender.closeConnection();
            System.out.println("Thread execution completed");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
