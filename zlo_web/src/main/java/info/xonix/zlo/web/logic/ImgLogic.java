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
}
