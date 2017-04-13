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

import org.apache.camel.Message;
import org.apache.camel.Predicate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@SpringBootApplication
public class YellowLite {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(YellowLite.class, args);
        CamelSpringBootApplicationController applicationController = context.getBean(CamelSpringBootApplicationController.class);
        applicationController.run();
    }

    private static final DateFormat DF = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    private static final DateFormat PANOMAX_DATE_FORMAT = new SimpleDateFormat("yyyy_MMdd_HHmmss");
    private static final String IMAGE_NAME = "YellowLite-${bean:yellow.webcam.lite.YellowLite?method=createCurrentTimestamp}";
    private static final Logger LOG = LoggerFactory.getLogger(YellowLite.class);
    private static final String FTP_OPTIONS = "&binary=true&passiveMode=true";
    private static final String WEBCAM = "webcam";
    private static final String GPHOTO = "gphoto";
    private static final String[] SOURCES = {WEBCAM, GPHOTO};

    @Value("${capture.source}")
    private String captureSource = "";
    @Value("${image.archive}")
    private boolean imageArchive = false;
    @Value("${aws.s3.active}")
    private boolean s3Active = false;
    @Value("${ftp.active}")
    private boolean ftpActive = false;
    @Value("${sftp.active}")
    private boolean sftpActive = false;
    @Value("${panomax.active}")
    private boolean panomaxActive = false;
    @Value("${panomax.sftp}")
    private boolean panomaxSftp = false;
    @Value("${teleport.active}")
    private boolean teleportActive = false;

    public String createCurrentTimestamp() {
        return DF.format(new Date());
    }

    @Bean
    RoutesBuilder myRouter() {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                if (WEBCAM.equals(captureSource)) {
                    from("quartz2:webcam-trigger?cron={{capture.cron}}")
                            .log("taking a webcam image")
                            .toD("exec:streamer?args=-c {{webcam.source}} -o {{capture.folder}}/" + IMAGE_NAME + ".jpeg");
                } else if (GPHOTO.equals(captureSource)) {
                    from("quartz2:dslr-trigger?cron={{capture.cron}}")
                            .log("taking a new slr image")
                            .toD("exec:gphoto2?args=--capture-image-and-download --filename {{capture.folder}}/" + IMAGE_NAME + ".jpg")
                            .log("image captured ${body}");
                } else {
                    LOG.error("No or unknown image source configured! Allowed values: " + Arrays.toString(SOURCES));
                }
                String fileAction = "delete=true";
                if (imageArchive) {
                    fileAction = "move=../archive";
                }

                from("file://{{capture.folder}}?include=.*\\.(jpeg|jpg)&" + fileAction)
                        .log("found new image: ${header.CamelFileName}")
                        .bean("imageEditor")
                        // TODO change to multicast http://camel.apache.org/multicast.html
                        .choice()
                        .when(is(s3Active)).to("direct:upload-s3")
                        .when(is(ftpActive)).to("direct:upload-ftp")
                        .when(is(sftpActive)).to("direct:upload-sftp")
                        .when(is(panomaxActive)).to("direct:upload-panomax")
                        .when(is(teleportActive)).to("direct:upload-teleport")
                        .otherwise().log("No image destination found! Please check your configuration.");

                if (s3Active) {
                    from("direct:upload-s3")
                            .process().message(this::createS3Key)
                            .to("aws-s3://{{aws.s3.bucket}}?accessKey={{aws.s3.accessKey}}&secretKey={{aws.s3.secretKey}}")
                            .log("Upload to S3 finished");
                }

                if (ftpActive) {
                    from("direct:upload-ftp")
                            .setHeader("CamelFileName", constant("LatestImage.jpg"))
                            .to("ftp://{{ftp.user}}@{{ftp.host}}/{{ftp.folder}}?password={{ftp.password}}" + FTP_OPTIONS)
                            .log("FTP upload to {{ftp.host}} finished");
                }
                if (sftpActive) {
                    from("direct:upload-sftp")
                            .setHeader("CamelFileName", constant("LatestImage.jpg"))
                            .to("sftp://{{sftp.user}}@{{sftp.host}}/{{sftp.folder}}?password={{sftp.password}}")
                            .log("SFTP upload to {{sftp.host}} finished");
                }
                if (panomaxActive) {
                    String protocol = panomaxSftp ? "sftp" : "ftp";
                    from("direct:upload-panomax")
                            .process().message(this::setPanomaxFilename)
                            .to(protocol + "://{{panomax.user}}@admin.panomax.com/{{panomax.camera}}?password={{panomax.password}}" + FTP_OPTIONS)
                            .log("Upload to " + protocol + "://{{panomax.user}}@admin.panomax.com/{{panomax.camera}}/${header.CamelFileName} finished");
                }
                if (teleportActive) {
                    from("direct:upload-teleport")
                            .setHeader("CamelFileName", constant("image.jpg"))
                            .to("ftp://{{teleport.user}}@ftp.teleport.nu?password={{teleport.password}}" + FTP_OPTIONS)
                            .log("FTP upload to ftp.teleport.nu finished");
                }
            }

            private Predicate is(boolean config) {
                return constant(Boolean.TRUE).isEqualTo(config);
            }

            private void setPanomaxFilename(Message message) {
                message.setHeader("CamelFileName", PANOMAX_DATE_FORMAT.format(new Date()) + ".jpg");
            }

            void createS3Key(Message e) {
                // TODO: init date at the beginning!
                File file = e.getBody(File.class);
                long lastModified = file != null ? file.lastModified() : System.currentTimeMillis();
                String s3id = DF.format(lastModified);
                e.setHeader(S3Constants.KEY, "image-" + s3id + ".jpg");
                e.setHeader(S3Constants.CONTENT_TYPE, "image/jpeg");
            }
        };
    }

}
