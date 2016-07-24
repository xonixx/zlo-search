package info.xonix.zlo.web.logic;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.imgscalr.AsyncScalr;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

public class ImgLogic {
    private static final Logger log = Logger.getLogger(ImgLogic.class);

    private static final String salt = UUID.randomUUID().toString();

    public static String hash(String val) {
        return DigestUtils.shaHex(val + salt);
    }

    public static boolean checkHash(String val, String hash) {
        return hash.equals(hash(val));
    }

    public static BufferedImage preview(String url, int maxHeight) throws Exception {
        try {
            BufferedImage image = ImageIO.read(new URL(url));
            if (image == null) {
                log.warn("null loading url=" + url);
                throw new Exception("null");
            }
            if (image.getHeight() <= maxHeight)
                return image;
            return AsyncScalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_HEIGHT, 500, maxHeight).get();
        } catch (Exception e) {
            log.error("Error resizing img " + url + ": " + e);
            throw e;
        }
    }

    private static final char[] EMPTY_GIF = {
            0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00, 0x01, 0x00, 0xf0, 0x01,
            0x00, 0xff, 0xff, 0xff, 0x00, 0x00, 0x00, 0x21, 0xf9, 0x04, 0x01, 0x0a,
            0x00, 0x00, 0x00, 0x2c, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00,
            0x00, 0x02, 0x02, 0x44, 0x01, 0x00, 0x3b
    };

    public static void emptyGif(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Content-Type", "image/gif");
        response.getWriter().write(EMPTY_GIF);
        response.flushBuffer();
    }
}
