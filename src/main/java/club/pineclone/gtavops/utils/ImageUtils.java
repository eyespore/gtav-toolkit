package club.pineclone.gtavops.utils;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import javafx.scene.image.Image;

import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ImageUtils {

    private static final Map<String, Image> images = new HashMap<>();

    public static Image loadImage(String path) {
        if (images.containsKey(path)) {
            return images.get(path);
        }
        URL imgResource = ImageUtils.class.getResource(path);
        if (imgResource == null) {
            Logger.error(LogType.FILE_ERROR, "unable to load image on path " + path);
            return null;
        }

        Image image = new Image(imgResource.toExternalForm());
        images.put(path, image);
        return image;
    }

    public static Image getIconImage() {
        return loadImage("/img/favicon.png");
    }
}
