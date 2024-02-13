package com.alcineo.bonappetit.ui.settings;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.alcineo.bonappetit.ui.settings.preference.ProductVersionPreference;
import com.alcineo.softpos.payment.api.ProductionVersionAPI;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProductVersionPreferenceTest {

    @Mock
    private        ProductVersionPreference           productVersionPreference;
    private static MockedStatic<ProductionVersionAPI> deviceInfoAPIMockedStatic;

    @BeforeClass
    public static void beforeClass() throws Exception {
        deviceInfoAPIMockedStatic = mockStatic(ProductionVersionAPI.class);
    }

    @Before
    public void setUp() {
        when(productVersionPreference.getFormattedProductVersion()).thenCallRealMethod();
    }

    @Test
    public void testShouldDisplayEmptyStringIfDuidCouldNotBeFetched() {
        deviceInfoAPIMockedStatic.when(() -> ProductionVersionAPI.getProductionVersion()).then(invocation -> new byte[0]);
        final String formattedProductVersion = productVersionPreference.getFormattedProductVersion();
        assertEquals("", formattedProductVersion);
    }

    @Test
    public void testTwoDecimalSignedBytesShouldBeWellPrintedInHexadecimal() {
        deviceInfoAPIMockedStatic.when(() -> ProductionVersionAPI.getProductionVersion()).then(invocation -> new byte[]{16, 18, (byte) 255, 26});
        final String formattedProductVersion = productVersionPreference.getFormattedProductVersion();
        assertEquals("1012FF1A", formattedProductVersion);
    }

}
