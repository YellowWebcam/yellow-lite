# Yellow Webcam Lite

Yellow Webcam Lite turns your Raspberry PI and your DSLR into a 
powerful webcam solution. Yellow Lite is released as Open Source software
and therefore available free of charge for any usage.

[avisec ag](https://www.avisec.ch/) runs the [Yellow Webcam solution](https://yellow.webcam/) 
offering an end to end offering that includes consulting, weather-proof 
camera enclosings, privacy controls and anonymization of images, as well as 
time lapse videos.

Yellow Lite contains a limited set of functionality of the Yellow Webcam and 
allows you to resize, crop, and publish your images to one of the following 
destinations.

- Your FTP or SFTP server
- [Amazon S3](https://aws.amazon.com/s3/)
- [Teleport](https://www.teleport.nu)
- [Panomax](https://www.panomax.com/)

avisec runs a demo installation of Yellow Lite. 
TODO: Link to blog post?
- https://www.teleport.nu/feed/mankzyu4lfoaw2d4rqjk
- https://avisec.panomax.com/test1

## Releases

The first public release will follow shortly!

## Development

Yellow Lite built with [Spring Boot](https://projects.spring.io/spring-boot/) 
and [Apache Camel](http://camel.apache.org/). Both project
provide a detailed documentation on how to work with such
project.

To run Yellow Lite using only [Maven](http://maven.apache.org): 

    mvn spring-boot:run

To package and start the application:

    mvn package
    java -jar target/yellow-lite-1.0-SNAPSHOT.jar

We're welcoming you to create pull requests or bug reports to
improve Yellow Lite.

## Configuration

Yellow Lite uses Spring Boot's [Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) 
approach. You can therefore use a configuration file called 
`application.properties` located in the same folder as your jar 
file. To adjust development configuration from Eclipse/InteliJ
we recommed a file called `application-default.properties` in 
the project root folder.

### Reference

capture images

    # see http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger#format
    capture.cron=0 */5 6-21 * * ?
    # possible values: gphoto, webcam
    capture.source=gphoto

To use [gphoto](http://www.gphoto.org/) as the image source, you need to install
it first: `sudo apt-get install gphoto2`.

We strongly recommend using ghoto to take images. While development you may
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
the resized (or original) image. X/Y define the coordinated of the
top left corner of the bounding box used for cropping.

    image.crop.x=
    image.crop.y=
    image.crop.height=
    image.crop.width=
    
If activated, the `archive` folder keeps all unedited images once
uploaded to the publishing channel. By default the archive is
disabled.

    image.archive=true

The following blocks outline the required configuration
values to publish the images. Set the active flag on
one of the blocks to true and fill all parameters below.

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

## Installation

TODO: Link to the article describing the pi setup.