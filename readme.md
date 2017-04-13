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

## Installation

TODO: Link to article describing the pi setup.