package com.alcineo.bonappetit.ui.settings;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.alcineo.bonappetit.ui.settings.preference.DeviceInfoPreference;
import com.alcineo.softpos.payment.api.DeviceInfoAPI;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeviceInfoPreferenceTest {

    @Mock
    private        DeviceInfoPreference        deviceInfoPreference;
    private static MockedStatic<DeviceInfoAPI> deviceInfoAPIMockedStatic;

    @BeforeClass
    public static void beforeClass() throws Exception {
        deviceInfoAPIMockedStatic = mockStatic(DeviceInfoAPI.class);
    }

    @Before
    public void setUp() {
        when(deviceInfoPreference.getFormattedDuid()).thenCallRealMethod();
    }

    @Test
    public void testShouldDisplayEmptyStringIfDuidCouldNotBeFetched() {
        deviceInfoAPIMockedStatic.when(DeviceInfoAPI::getDeviceUid).then(invocation -> new byte[0]);
        final String formattedDuid = deviceInfoPreference.getFormattedDuid();
        assertEquals("", formattedDuid);
    }

    @Test
    public void testTwoDecimalSignedBytesShouldBeWellPrintedInHexadecimal() {
        deviceInfoAPIMockedStatic.when(DeviceInfoAPI::getDeviceUid).then(invocation -> new byte[]{(byte) 255, 27});
        final String formattedDuid = deviceInfoPreference.getFormattedDuid();
        assertEquals("FF1B", formattedDuid);
    }


}
