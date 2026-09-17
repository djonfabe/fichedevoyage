package td.gov.fichedevoyage.application.service;

import java.io.OutputStream;

public interface QrCodeService {
    byte[] generatePng(String content, int width, int height);
    void writePng(String content, int width, int height, OutputStream out);
}
