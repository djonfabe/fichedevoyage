package td.gov.fichedevoyage.infrastructure.qr;

import td.gov.fichedevoyage.application.service.QrCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Component
public class ZXingQrCodeAdapter implements QrCodeService {

    private final QRCodeWriter writer = new QRCodeWriter();

    @Override
    public byte[] generatePng(String content, int width, int height) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            writePng(content, width, height, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur génération QR PNG", e);
        }
    }

    @Override
    public void writePng(String content, int width, int height, OutputStream out) {
        try {
            Map<EncodeHintType, Object> hints = Map.of(
                    EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H,
                    EncodeHintType.MARGIN, 2
            );
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Erreur génération QR Code", e);
        }
    }
}
