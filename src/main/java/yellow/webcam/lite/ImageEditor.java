/* Copyright 2017 avisec ag

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License. */

package yellow.webcam.lite;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component("imageEditor")
public class ImageEditor {

    private static final Logger LOG = LoggerFactory.getLogger(ImageEditor.class);
    private final Integer width;
    private final Integer height;
    private final Integer cropX;
    private final Integer cropY;
    private final Integer cropHeight;
    private final Integer cropWidth;
    private final float jpgQuality;

    public ImageEditor(@Value("${image.resize.width}") String imageWidth,
                       @Value("${image.resize.height}") String imageHeight,
                       @Value("${image.crop.x}") String cropX,
                       @Value("${image.crop.y}") String cropY,
                       @Value("${image.crop.height}") String cropHeight,
                       @Value("${image.crop.width}") String cropWidth,
                       @Value("${image.jpg.quality}") String jpgQuality) {
        this.width = parseInt(imageWidth);
        this.height = parseInt(imageHeight);
        this.cropX = parseInt(cropX);
        this.cropY = parseInt(cropY);
        this.cropHeight = parseInt(cropHeight);
        this.cropWidth = parseInt(cropWidth);
        this.jpgQuality = parsePercent(jpgQuality);
        if (resizeIsActive()) {
            LOG.info("Resizing all images to {}x{}", this.width, this.height);
        }
        if (cropIsActive()) {
            LOG.info("Cropping all images to {}x{}", this.cropWidth, this.cropHeight);
        }
    }

    float parsePercent(String jpgQuality) {
        return StringUtils.hasLength(jpgQuality) ? Float.parseFloat(jpgQuality) / 100 : 1f;
    }

    private int parseInt(String property) {
        return StringUtils.hasLength(property) ? Integer.parseInt(property) : 0;
    }

    private boolean resizeIsActive() {
        return this.width > 0 || this.height > 0;
    }

    private boolean cropIsActive() {
        return cropHeight > 0 && cropWidth > 0;
    }

    public InputStream resizeAndCrop(File originalImage) {
        try {
            InputStream result = new FileInputStream(originalImage);
            if (resizeIsActive() || cropIsActive()) {
                // Load from file
                BufferedImage processedImage = ImageIO.read(originalImage);
                // Calculate the target image size
                Dimensions dimensions = calculateDimensions(processedImage.getWidth(), processedImage.getHeight());
                // Resize Image
                if (resizeIsActive()) {
                    processedImage = Scalr.resize(processedImage, Scalr.Method.QUALITY,
                            dimensions.width, dimensions.height);
                }
                // Crop image
                if (cropIsActive()) {
                    processedImage = Scalr.crop(processedImage, cropX, cropY, cropWidth, cropHeight);
                }
                // BufferedImage to InputStream
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
                jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                jpegParams.setCompressionQuality(jpgQuality);
                final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
                writer.setOutput(new MemoryCacheImageOutputStream(os));
                writer.write(null, new IIOImage(processedImage, null, null), jpegParams);
                result = new ByteArrayInputStream(os.toByteArray());
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Dimensions calculateDimensions(Integer originalWidth, Integer originalHeight) {
        BigDecimal imageWidth = new BigDecimal(originalWidth);
        BigDecimal imageHeight = new BigDecimal(originalHeight);
        if (width > 0 && height > 0) {
            return new Dimensions(width, height);
        } else if (width > 0) { // calculate height
            Integer height = imageHeight.divide(imageWidth, 10, RoundingMode.HALF_UP).multiply(new BigDecimal(width))
                    .setScale(0, BigDecimal.ROUND_UP).intValueExact();
            return new Dimensions(width, height);
        } else { // calculate  width
            Integer width = imageWidth.divide(imageHeight, 10, RoundingMode.HALF_UP).multiply(new BigDecimal(height))
                    .setScale(0, BigDecimal.ROUND_UP).intValueExact();
            return new Dimensions(width, height);
        }
    }

    class Dimensions {
        Integer width;
        Integer height;

        Dimensions(Integer width, Integer height) {
            this.width = width;
            this.height = height;
        }
    }

    Integer getHeight() {
        return height;
    }

    Integer getWidth() {
        return width;
    }

}
