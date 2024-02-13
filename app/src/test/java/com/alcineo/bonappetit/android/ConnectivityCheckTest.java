package com.alcineo.bonappetit.android;

import org.junit.Test;

public class ConnectivityCheckTest {

    @Test
    public void testCanReachDnsGoogle() {
        ConnectivityCheck connectivityCheck = new ConnectivityCheck();
        connectivityCheck.canReachDNS("google.com");
    }


}
