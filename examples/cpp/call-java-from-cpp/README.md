## Calling Java methods from C++ ##

These examples are ported from:

https://stackoverflow.com/questions/992836/how-to-access-the-java-method-in-a-c-application

They were tested with g++ and Java 8 on OS X. Makefile was created; should be adjustable to Linux.
Use `make compile-mac` for `jnitest` and `make hello-mac` for `hellow` examples.
`compile-linux` is included as well. Paths are hardcoded and require adjustments and refactoring.

The purpose of these is to enable to call the MARF library from [OpenISS](https://github.com/OpenISS/OpenISS).
