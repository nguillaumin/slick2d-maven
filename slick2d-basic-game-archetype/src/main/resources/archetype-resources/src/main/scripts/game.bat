@echo off
java -jar -Djava.library.path="lib/" "${project.build.finalName}.${project.packaging}"
