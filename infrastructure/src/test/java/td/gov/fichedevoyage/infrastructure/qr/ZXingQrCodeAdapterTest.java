package td.gov.fichedevoyage.infrastructure.qr;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class ZXingQrCodeAdapterTest {

    private final ZXingQrCodeAdapter qrCodeAdapter = new ZXingQrCodeAdapter();

    @Test
    void testGeneratePng() {
        byte[] bytes = qrCodeAdapter.generatePng("https://fichedevoyage.gov.td/v/test-token", 200, 200);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test
    void testWritePng() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        qrCodeAdapter.writePng("https://fichedevoyage.gov.td/v/test-token", 200, 200, out);
        byte[] bytes = out.toByteArray();
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }
}
