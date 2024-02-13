package com.alcineo.bonappetit.android;

import java.io.IOException;
import java.net.InetAddress;

public class ConnectivityCheck {

    public ConnectivityCheck() {
        this.canReachDNS("alcineo.com");
    }

    void canReachDNS(String dns) {
        try {
            InetAddress.getByName(dns);
        } catch (IOException e) {
            throw new RuntimeException("Network error, failed to reach DNS. " + e);
        }
    }

}
