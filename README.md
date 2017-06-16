# owo.java

Badges go here in one line for the master branch ONLY. Badges can also go in the
header line.

This is an API wrapper for https://whats-th.is/ written in Java. This wrapper 
requires Java 7 or above to function, it has also been confirmed working 
with Android (Minimum version is Marshmallow, because of Java 7).


### Download
Maven:
```xml
<repositories>
    <repository>
        <id>bramhaag</id>
        <url>http://bramhagens.me:8081/repository/maven-public/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>me.bramhaag</groupId>
        <artifactId>owo-java</artifactId>
        <version>2.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```
Gradle:
```groovy
repositories {
    maven { url "http://bramhagens.me:8081/repository/maven-public/" }
}

dependencies {
    //Note that you can also use owo-java-jre7 if you're running on Java 7
    compile 'me.bramhaag:owo-java:2.0-SNAPSHOT'
}
```

Or you can build the jar yourself (see below)

### Usage

To get started, create an `OwO` object using the Builder class.

> `REQUIRED` `TOKEN` should be replaced with your own unique OwO API key  
> `(OPTIONAL)` You can set `endpoint` to use a custom endpoint, default
> endpoint is `https://api.awau.moe/`  
> `(OPTIONAL)` You can set `uploadUrl` to use a custom upload url, default
> upload URL is `https://owo.whats-th.is/`  
> `(OPTIONAL)` You can set `shortenUrl` to use a custom shorten url, default
> upload URL is `https://awau.moe/`

```java
OwO owo = new OwO.Builder()
                .setKey("TOKEN")
                .setUploadUrl("https://owo.whats-th.is/")
                .setShortenUrl("https://thats-a.link/")
                .build();
```

Next, we can use our newly created `owo` object to upload files and shorten urls
> `OwO#upload` and `OwO#shorten` both return `OwoAction`s, these can be executed
> async using the `execute` method, but can also be executed sync using the `executeSync` method
```java
owo.upload(new UploadBuilder().setFile(myFile)).execute(file -> System.out.println("Image URL: " + file.getUrl()));
owo.shorten("http://my-domain.com").execute(url -> System.out.println("Shortened link: " + url));
```

This code can throw an exception when something goes wrong, to handle this 
exception we can add an extra `throwable` argument to the `execute` method
```java
owo.upload(new UploadBuilder().setFile(myFile)).execute(file -> System.out.println("Image URL: " + file.getUrl(), throwable -> throwable.printStackTrace()));
owo.shorten("http://my-domain.com").execute(url -> System.out.println("Shortened link: " + url), throwable -> throwable.printStackTrace());
```

## How to build
##### With dependencies
1. Run `gradlew shadowJar` in project's root.
2. The file is located in `build/lib`.
##### Without dependencies
1. Run `gradlew build` in project's root.
2. The file is located in `build/lib`.

### Contributing

Pull requests are accepted. Make sure you add test suites for new features and
make sure the code passes the spec (so the build does not break). Tests are
automatically run when commits are made in a pull request.

### License

The contents of this repository are licensed under the MIT license. A
copy of the MIT license can be found in [LICENSE](https://github.com/bramhaag/owo.java/blob/master/LICENSE).