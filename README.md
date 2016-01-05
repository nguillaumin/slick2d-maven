# Slick 2D Maven Distribution

The fastest and simplest way to start a new Slick 2D based game.

[![Build Status](https://travis-ci.org/nguillaumin/slick2d-maven.svg?branch=travis-ci)](https://travis-ci.org/nguillaumin/slick2d-maven)

### Quickstart

**You don't need to clone this repository to get started. The project archetype as well as the JARs are published in the official Maven repositories, so you can just follow the steps below from scratch.** Clone this project only if you want to contribute to Slick2D or to the game archetype.

Pre-requisites:
* Git, Java and Maven working
* Slick 2D depends on `javaws.jar` which ships with the Oracle JDK (It's not available in the public Maven repositories). The `pom.xml` file references a *local filesystem path* to `javaws.jar` for that reason.
* It won't work with OpenJDK for the reason above (Can be solved by providing `javaws.jar` separately). On some Linux distributions you can install [Netx](http://jnlp.sourceforge.net/netx/) and change the system path to point to `netx.jar`. For example on Ubuntu the package to install is `icedtea-netx-common` and the jar is in `/usr/share/icedtea-web/netx.jar`.

#### Create a Slick 2D game

The command below is using `archetypeVersion=1.0.0`. Make sure you use the [latest available version from Maven central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22slick2d-basic-game-archetype%22)

```
cd /home/<user>/projects/   -or-   cd C:\Users\<user>\Projects\

# You can omit the last 4 parameters for interactive mode
mvn archetype:generate -DarchetypeGroupId=org.slick2d -DarchetypeArtifactId=slick2d-basic-game-archetype -DarchetypeVersion=1.0.0 -DgroupId=com.me.game -DartifactId=game -Dversion=0.0.1-SNAPSHOT -Dpackage=com.me.game

cd game
mvn clean package
```

You'll end up with a packaged game in `target/game-0.0.1-SNAPSHOT-release.zip`. Just unzip and run `game.sh` (Linux) or `game.bat` (Windows). Alternatively, files are also available unzipped in `target/game-0.0.1-SNAPSHOT-release/`.

#### Run/Debug from Eclipse

Configuration-free with the awesome [Maven Natives](https://code.google.com/p/mavennatives/#Eclipse_Plugin) plugin ! Just hit the "Run" button ! 

**Manual steps:**

* You must have the Maven Eclipse integration plugin installed (m2e)
* Import the Maven project
* Right-click on `Game`, Debug as, Java application
* This will fail with `java.lang.UnsatifsiedLinkError`
* Run `mvn package` once, the native libraries will get copied in `target/natives`
* Edit your debug configuration (menu Run, Debug configurations...), on the "Arguments" tab, "VM Arguments" field, enter `-Djava.library.path=target/natives`
* Click on "Debug" and you're all set !

### Why ?

I believe making Slick 2D available through Maven will make life easier for users:

* Clear versionning scheme
* Very easy quickstart using Maven archetypes (see above)
* Simplified dependency management, especially for the native libraries
* Simplified game packaging

### Upstream

It seems that the upstream [Slick2D Mercurial repository](http://bitbucket.org/kevglass/slick/) is not updated any more, despite numerous bug reports and pull requests. Because of that, I'm now accepting patches that I'll integrate and release through Maven. I guess you could consider this project a fork of the official sources.
