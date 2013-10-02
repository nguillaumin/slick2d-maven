# Slick 2D Maven Distribution

The fastest and simplest way to start a new [Slick 2D](http://www.slick2d.org/) based game.

### Quickstart

Pre-requisites:
* Git, Java and Maven working
* Slick 2D depends on `javaws.jar` which ships with the Oracle JDK (It's not available in the public Maven repositories). The `pom.xml` file references a *local filesystem path* to `javaws.jar` for that reason.
* It won't work with OpenJDK for the reason above (Can be solved by providing `javaws.jar` separately). On some Linux distributions you can install [Netx](http://jnlp.sourceforge.net/netx/) and change the system path to point to `netx.jar`. For example on Ubuntu the package to install is `icedtea-netx-common` and the jar is in `/usr/share/icedtea-web/netx.jar`.

#### Clone and build the project (once)

```
git clone git://github.com/nguillaumin/slick2d-maven.git
cd slick2d-maven
mvn clean install
```

#### Create a Slick 2D game

```
cd /home/<user>/projects/   -or-   cd C:\Users\<user>\Projects\

# You can omit the last 4 parameters for interactive mode
mvn archetype:generate -DarchetypeGroupId=org.slick2d -DarchetypeArtifactId=slick2d-basic-game-archetype -DarchetypeVersion=2013.10-SNAPSHOT -DgroupId=com.me.game -DartifactId=game -Dversion=0.0.1-SNAPSHOT -Dpackage=com.me.game

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

### Why not submit a patch to switch Slick 2D to Maven ?

Slick 2D is currently using Ant for building and unfortunately there doesn't seem to be much interest in switching to Maven, despite numerous posts in the forums as well as some pull request on the Slick 2D repo to implement that switch.

I've already tried, and I'll try again :) I hope that this project will convince the Slick 2D maintainers that switching to Maven is worth it.

### How ?

A [Jenkins](http://www.jenkins-ci.org/) instance polls the upstream [Slick2D Mercurial repository](http://bitbucket.org/kevglass/slick/). As soon as a change is committed it merges it to this project using the custom script `src/main/scripts/sync-upstream.sh`, then commit the changes and pushes them to GitHub.

### What's next ?

* Update the archetype to produce WebStart artifacts (JNLP)
* Define a release scheme
* ...and the most important end-goal: Get those artifacts into Maven Central to avoid requiring people to clone the project.
