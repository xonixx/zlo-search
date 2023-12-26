package info.xonix.zlo.web.logic;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.imgscalr.AsyncScalr;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;

public class ImgLogic {
    private static final Logger log = Logger.getLogger(ImgLogic.class);

    private final static Config config = AppSpringContext.get(Config.class);

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

    public static void renderPreviewAndCache(String url, HttpServletResponse resp, ServletContext servletContext) {
        File previewFile = new File(config.getPicPreviewDir(), safeFilename(url));
        boolean err = false;
        OutputStream outputStream = null;
        try {
            outputStream = resp.getOutputStream();
            if (previewFile.exists()) {
                if (previewFile.length() == 0) {
//                    log.info("renderPreviewAndCache " + url + ": cached is empty");
                    err = true;
                } else {
//                    log.info("renderPreviewAndCache " + url + ": cached");
                    resp.setContentType("image/png");
                    FileUtils.copyFile(previewFile, outputStream);
                }
            } else {
                BufferedImage previewImg = null;
                try {
//                    log.info("renderPreviewAndCache " + url + ": building preview");
                    try {
                        previewImg = preview(url, 70);
                    } catch (Exception e) {
                        previewImg = preview(url, 70);
                    }
                } catch (Exception e) {
                    if (!previewFile.createNewFile()) {
                        log.error("Can't create empty preview file!");
                    }
                    err = true;
                }
                if (previewImg != null) {
                    resp.setContentType("image/png");
                    ImageIO.write(previewImg, "png", previewFile);
                    ImageIO.write(previewImg, "png", outputStream);
                }
            }
        } catch (Exception e) {
            err = true;
        }
        if (outputStream != null) {
            try {
                if (err) {
                    resp.setContentType("image/png");
                    IOUtils.copy(servletContext.getResourceAsStream("/pic/FFFFFF-0.png"), outputStream);
                }
                outputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String safeFilename(String name) {
        String[] parts = name.split("\\.");

        for (int i = 0; i < parts.length; i++) {
            // replaces any character that isn't a number, letter or underscore
            parts[i] = parts[i].replaceAll("\\W+", "_");
        }

        String fname = StringUtils.join(parts, '.');
        if (fname.length() > 200)
            fname = fname.substring(0, 200);
        return fname + '_' + DigestUtils.shaHex(name);
    }
}
