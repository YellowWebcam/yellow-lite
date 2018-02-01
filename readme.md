# Yellow Webcam Lite

Yellow Webcam Lite turns your Raspberry PI and your DSLR camera into a powerful 
webcam solution. Yellow Lite is released as Open Source software 
(Apache 2.0 License) and therefore available free of charge for any usage.

[avisec ag](https://www.avisec.ch/) runs the [Yellow Webcam solution](https://yellow.webcam/) 
offering an end to end service that includes consulting, weather-proof 
camera casings, privacy control and anonymization of images, as well as 
timelapse videos.

Yellow Lite contains a limited set of functionality of the Yellow Webcam and 
allows you to resize, crop, and publish your images to the following 
destinations.

- Your FTP or SFTP server
- [Amazon S3](https://aws.amazon.com/s3/)
- [Teleport](https://www.teleport.io)
- [Panomax](https://www.panomax.com/)

We've compiled a series of tutorials to install and configure Yellow 
Lite as a webcam controller on a Raspberry PI.

- Part I: Install Raspbian on a Raspberry PI
- Part II: Install Yellow Lite on a Raspberry PI
- Part III: Configure Yellow Lite
- Part IV: Automatically resize and crop images

All tutorials are available in the [avisec wiki](https://avisec.atlassian.net/wiki/spaces/YD/pages/10977379/yellow+lite).

The hardware can be purchased in the [photo-webcam.shop](https://photo-webcam.shop/).

## Releases

Binary releases are available on [photo-webcam.shop/en/yellow-lite](https://photo-webcam.shop/yellow-lite/).

## Development

Yellow Lite is built using [Spring Boot](https://projects.spring.io/spring-boot/) 
and [Apache Camel](http://camel.apache.org/). Both projects
provide extensive documentation on how to use or enhance
such programs.

You will have to create a configuration file to successfuly start yellow lite (see "configuration" below).

To run Yellow Lite using only [Maven](http://maven.apache.org): 

    mvn spring-boot:run

To package and start the application:

    mvn package
    java -jar target/yellow-lite-1.0-SNAPSHOT.jar

We appreciate pull requests or bug reports to
improve Yellow Lite.

## Configuration

Yellow Lite uses Spring Boot's [Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) 
approach. You can therefore use a configuration file called 
`application.properties` located in the same folder as your jar 
file. To adjust development configuration from Eclipse/InteliJ
we recommed a file called `application-default.properties` in 
the project root folder.

### Reference

Images are triggered by a Cron expression. Cron allows
flexible time/date based triggers.

    # see http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger#format
    capture.cron=0 */5 6-21 * * ?
    # possible values: gphoto, webcam
    capture.source=gphoto

To use [gphoto](http://www.gphoto.org/) as the image source, you need to install
it first: `sudo apt-get install gphoto2`.

We strongly recommend using ghoto2 to take images. While developing you may
want to use your webcam to take test images. This requires streamer: 
`sudo apt-get install streamer`.

    capture.source=webcam
    webcam.source=/dev/video0

Resize an image. Provide your desired number of pixels in height and width.
When you provide only one value, the other figure will be calculated
to retain the image proportions.

    image.resize.width=
    image.resize.height=

Crop an image to a new height/width. Height/width must be smaller than
the resized (or original) image. X/Y define the coordinates of the
upper left corner of the bounding box used for cropping.

    image.crop.x=
    image.crop.y=
    image.crop.height=
    image.crop.width=

As soon as you crop or resize the image, it will be loaded into memory 
and converted back into the jpg format. By default a compression of 90%
will be used. A smaller value will result in smaller files. A value of 
100 will not compress the image. 

    # set quality to 90% after cropping/resizing the image
    image.jpg.quality=90

If activated, the `archive` folder keeps all unedited images once
uploaded to the publishing channel. By default the archive is
disabled.

    image.archive=true

The following blocks outline the required configuration
values to publish the images. Set the active flag on
the blocks you want to use for publishing to true and 
fill all parameters of the section.

    # SFTP
    sftp.active=false
    sftp.folder=
    sftp.user=
    sftp.password=
    sftp.host=
    
    # FTP
    ftp.active=false
    ftp.folder=
    ftp.user=
    ftp.password=
    ftp.host=
    
    # S3
    aws.s3.active=false
    aws.s3.accessKey=
    aws.s3.secretKey=
    aws.s3.bucket=
    
    # Panomax (FTP)
    panomax.active=false
    panomax.user=
    panomax.camera=
    panomax.password=
    panomax.sftp=false
    
    # Teleport (FTP)
    teleport.active=false
    teleport.user=
    teleport.password=
