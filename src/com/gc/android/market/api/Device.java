package com.gc.android.market.api;

public class Device {

    /**
     * Identifier used by Google Android Marketplace to verify if a device is
     * genuine
     */
    private String marketId;

    /**
     * The name of device
     */
    private String deviceName;

    /**
     * The version of Android the device is running.
     */
    private int deviceVersion;

    /**
     * Constructs a <code>Device</code> using the passed parameters.
     * 
     * @param deviceVersion
     *            The version of Android the device is running.
     * @param deviceName
     *            The device name.
     */
    public Device(int deviceVersion, String deviceName) {
        this.deviceName = deviceName;
        this.deviceVersion = deviceVersion;
        this.marketId = String.valueOf(Utils.nextLong());
    }

    /**
     * Constructs a <code>Device</code> using the passed parameters.
     * 
     * @param deviceVersion
     *            The version of Android the device is running.
     * @param deviceName
     *            The device name.
     * @param marketId
     *            Identifier used by Google Android Marketplace to verify if a
     *            device is genuine
     */
    public Device(int deviceVersion, String deviceName, String marketId) {
        this.deviceName = deviceName;
        this.deviceVersion = deviceVersion;
        this.marketId = marketId;
    }

    /**
     * Gets the name of Android device.
     * 
     * @return deviceName
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Gets the version of android running on device.
     * 
     * @return deviceVersion
     */
    public int getDeviceVersion() {
        return deviceVersion;
    }

    /**
     * Gets the market id for device.
     * 
     * @return marketId
     */
    public String getMarketId() {
        return marketId;
    }

    /**
     * Set the market id for device
     * 
     * @param marketId
     *            market id
     */
    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    @Override
    public String toString() {
        return deviceName + ":" + deviceVersion;
    }
}