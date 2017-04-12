package yellow.webcam.lite;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ImageEditorTest {

    @Test
    public void initEmpty() {
        ImageEditor sut = new ImageEditor("", "", "", "", "", "");
        assertThat(sut.getWidth()).isEqualTo(0);
        assertThat(sut.getHeight()).isEqualTo(0);
    }

    @Test
    public void initHeightAndWidth() {
        ImageEditor sut = new ImageEditor("500", "","", "", "", "");
        assertThat(sut.getWidth()).isEqualTo(500);
        assertThat(sut.getHeight()).isEqualTo(0);
    }

    @Test
    public void calculateDimensionsNoCalculation() {
        ImageEditor sut = new ImageEditor("400", "300", "", "", "", "");
        ImageEditor.Dimensions dimensions = sut.calculateDimensions(4000, 3000);
        assertThat(dimensions.width).isEqualTo(400);
        assertThat(dimensions.height).isEqualTo(300);
    }

    @Test
    public void calculateDimensionsCalculateHeight() {
        ImageEditor sut = new ImageEditor("400", "", "", "", "", "");
        ImageEditor.Dimensions dimensions = sut.calculateDimensions(4000, 3000);
        assertThat(dimensions.width).isEqualTo(400);
        assertThat(dimensions.height).isEqualTo(300);
    }

    @Test
    public void calculateDimensionsCalculateWidth() {
        ImageEditor sut = new ImageEditor("", "300", "", "", "", "");
        ImageEditor.Dimensions dimensions = sut.calculateDimensions(4000, 3000);
        assertThat(dimensions.width).isEqualTo(400);
        assertThat(dimensions.height).isEqualTo(300);
    }

}