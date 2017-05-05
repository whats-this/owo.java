# owo.java

## OwO What's This?
This is an API wrapper for <https://whats-th.is/> written in Java. 
This wrapper requires Java 7 or above (currently separate versions for Java 7 and 8 are available) to function,
it has also been confirmed working with Android (Minimum version is Marshmallow, because of Java 7).

## Usage
To start, create a new OwO object
```java
//Create a new OwO object, these objects can be reused.
OwO owo = new OwO("YOUR-TOKEN-HERE");
```

With this OwO object, you can shorten links and uploading files, simply by doing
```java
owo.shorten("http://my_domain.com");
owo.upload(new File("my_image.png"));
```
These methods return an `OwOAction<T>`. This means that the action has been prepared, but has not been send yet.
There are multiple ways to send this action, they can be send sync, async and with or without error handling.
```java
//Sync (These methods *can* throw errors, so you'll need to surround them with a try catch block)
String url = owo.shorten("http://my_domain.com").executeSync();
OwOFile file = owo.upload(new File("my_image.png")).executeSync();

//Async (no error handling)
owo.shorten("http://my_domain.com").execute(url -> System.out.println("Shortened link: " + url));
owo.upload(new File("my_image.png")).execute(file -> System.out.println("Image URL: " + file.getUrl()));

//Async (with error handling)
owo.shorten("http://my_domain.com").execute(url -> System.out.println("Shortened link: " + url), throwable -> /* handle error */);
owo.upload(new File("my_image.png")).execute(file -> System.out.println("Image URL: " + file.getUrl()), throwable -> /* handle error */);
```

## How to build
##### With dependencies
1. Run `gradlew shadowJar` in project's root.
2. The files is located in `jre7/build/lib` and `jre8/build/lib`
##### Without dependencies
1. Run `gradlew build` in project's root
2. The files is located in `jre7/build/lib` and `jre8/build/lib` (Note that you will need to supply the dependencies yourself!)

## Which version to use
The jre7 version is a port of the jre8 version, this means that it *might* not have all features the jre8 version has, 
but that is not likely to happen. I do recommend to use the jre8 version, unless you **have** to use Java 7.


## TODO
- Maven repo
- Javadoc