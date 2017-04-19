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

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultMessage;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static yellow.webcam.lite.YellowLite.DF;
import static yellow.webcam.lite.YellowLite.HEADER_IMAGE_TIME_TAKEN;

public class YellowLiteTest {

    private YellowLite sut;

    @Before
    public void setup() {
        sut = new YellowLite();
    }

    @Test
    public void parseFilename() throws Exception {
        // Given
        Message m = createFileMessage("YellowLite-20170419T102200.jpg");
        // When
        sut.setImageDateHeader(m);
        // Then
        assertThat(m.getHeader(HEADER_IMAGE_TIME_TAKEN)).isEqualTo("20170419T102200");
    }

    @Test
    public void parseUnexpectedFilename() throws Exception {
        // Given
        Message m = createFileMessage("Foobar.jpg");
        String time = DF.format(new Date());
        // When
        sut.setImageDateHeader(m);
        // Then
        assertThat(m.getHeader(HEADER_IMAGE_TIME_TAKEN)).isEqualTo(time);
    }

    @Test
    public void panomaxFilename() {
        Message m = new DefaultMessage();
        m.setHeader(HEADER_IMAGE_TIME_TAKEN, "20170419T102201");
        sut.setPanomaxFilename(m);
        assertThat(m.getHeader(Exchange.FILE_NAME, String.class)).isEqualTo("2017_0419_102201.jpg");
    }

    private Message createFileMessage(String pathname) {
        Message m = new DefaultMessage();
        m.setBody(new File(pathname));
        return m;
    }

}