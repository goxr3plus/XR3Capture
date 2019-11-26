---

<h3 align="center" > XR3Capture ( <a href="https://www.youtube.com/watch?v=s4TGWYBdv0E" target="_blank">Demo</a> )</h3>

| DJ UI | Chromium Web Browser 
|:-:|:-:|
| ![1](https://user-images.githubusercontent.com/20374208/69632235-75a26e80-1057-11ea-9969-942197f188da.jpeg) | ![1 (1)](https://user-images.githubusercontent.com/20374208/69632236-75a26e80-1057-11ea-96af-f0ba038fb392.jpeg) |

<p align="center">
<sup>
<b>Is a JavaFX application which allows you to take screen shots of your computer and with the help of another app XR3ImageViewer you can also view the result image. </b>
</sup>
</p>

---

[![Latest Version](https://img.shields.io/github/release/goxr3plus/XR3Capture.svg?style=flat-square)](https://github.com/goxr3plus/XR3Capture/releases)
[![GitHub contributors][contributors-image]][contributors-url]
[![HitCount](http://hits.dwyl.io/goxr3plus/XR3Capture.svg)](http://hits.dwyl.io/goxr3plus/XR3Capture)
[![Total Downloads](https://img.shields.io/github/downloads/goxr3plus/XR3Capture/total.svg)](https://github.com/goxr3plus/XR3Capture/releases)

[contributors-url]: https://github.com/goxr3plus/XR3Capture/graphs/contributors
[contributors-image]: https://img.shields.io/github/contributors/goxr3plus/XR3Capture.svg

[jitpack-url]: https://jitpack.io/#goxr3plus/XR3Capture

> Release V101 and above are under work for Java 10 and Java 11 modularization 


### Add it to your project using JitPack :

[Link][jitpack-url]

### Step 1. Add the JitPack repository to your build file
``` XML
<repositories>
	<repository>
	   <id>jitpack.io</id>
	   <url>https://jitpack.io</url>
        </repository>
</repositories>
```

### Step 1. Add it as a dependency

* JavaFX-Web-Browser for Java 9 - (9.x)

``` XML
<dependency>
	 <groupId>com.github.goxr3plus</groupId>
	 <artifactId>JavaFX-Web-Browser</artifactId>
	 <version>10.0.2</version>
</dependency>
```

* JavaFX-Web-Browser for Java 8 - (3.x)

``` XML
<dependency>
	 <groupId>com.github.goxr3plus</groupId>
	 <artifactId>JavaFX-Web-Browser</artifactId>
	 <version>V3.12</version>
</dependency>
```


# From version V3.11 it is completely embeddable!!!

What that means ? Well you can download the jar file with Maven , Gradle etc ( the depencities will come along ) and use it inside your application . 

--> You don't believe me ?? ( I am already doing this with [XR3Player](https://github.com/goxr3plus/XR3Player) ) 

--> How you can embed it inside your application ? ( Use [JitPack.io](https://jitpack.io/#goxr3plus/XR3Capture/V3.12) )

### Add it to your project using JitPack :

https://jitpack.io/#goxr3plus/XR3Capture

### Step 1. Add the JitPack repository to your build file
``` XML
<repositories>
	<repository>
	   <id>jitpack.io</id>
	   <url>https://jitpack.io</url>
        </repository>
</repositories>
```

###  Step 2. Add the dependency
``` XML
dependency>
	  <groupId>com.github.goxr3plus</groupId>
	  <artifactId>xr3capture</artifactId>
	  <version>V3.12</version>
</dependency>
```

Then from inside your code you can create an intance or multiple instances of Browser like this:

``` JAVA
public XR3Capture xr3Capture = new XR3Capture();
```

and very simply open the  window :

```JAVA
xr3Capture.stage.show();
```

Happy :) ?  Cause i am ...

---

## On Sourceforge
 SourceForge:(https://sourceforge.net/projects/xr3capture/)

## Youtube Demostration
[![Demostration of XR3Capture](http://img.youtube.com/vi/s4TGWYBdv0E/0.jpg)](https://www.youtube.com/watch?v=s4TGWYBdv0E)
