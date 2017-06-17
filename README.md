# owo.java

[ ![Download](https://api.bintray.com/packages/bramhaag/maven/owo.java/images/download.svg) ](https://bintray.com/bramhaag/maven/owo.java/_latestVersion) [![CircleCI](https://circleci.com/gh/bramhaag/owo.java/tree/master.svg?style=svg)](https://circleci.com/gh/bramhaag/owo.java/tree/master)

This is an API wrapper for https://whats-th.is/ written in Java. This wrapper 
requires Java 7 or above to function, it has also been confirmed working 
with Android (Minimum version is Marshmallow, because of Java 7).


### Download
> Replace `VERSION` with a specific version. The latest version can be found at
> the top of the readme
Maven:
```xml
<dependencies>
    <dependency>
      <groupId>me.bramhaag</groupId>
      <artifactId>owo-java</artifactId>
      <version>VERSION</version>
    </dependency>
</dependencies>
```
Gradle:
```groovy
repositories {
    jcenter()
}

dependencies {
    compile 'me.bramhaag:owo-java:VERSION'
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
> async using the `execute` method, but can also be executed sync using the
> `executeSync` method
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