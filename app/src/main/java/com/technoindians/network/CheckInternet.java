package com.technoindians.network;

/**
 * Created by root on 12/12/16.
 */

public class CheckInternet {

    public static boolean check() {
        return ConnectivityReceiver.isConnected();
    }
}
