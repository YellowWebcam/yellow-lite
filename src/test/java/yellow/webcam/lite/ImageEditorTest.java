package yellow.webcam.lite;

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