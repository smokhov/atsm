
lib_dir=/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/jre/lib/server 

compile-mac:
	g++ -I/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/include \
	    -I/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/include/darwin \
	    -L/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/jre/lib/server \
            -Wl,-rpath,${lib_dir} \
            -ljvm jnitest.cc -o jnitest


compile-linux:
	g++ -I/usr/lib/jvm/java-6-sun/include \
	    -I/usr/lib/jvm/java-6-sun/include/linux \
	    -L/usr/lib/jvm/java-6-sun/jre/lib/i386/server/ -ljvm jnitest.cc

hello-mac: HelloWorld.class
	g++ \
	    -D_JNI_IMPLEMENTATION_ \
	    -I/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/include \
	    -I/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/include/darwin \
	    -L/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/jre/lib/server \
            -Wl,-rpath,${lib_dir} \
	    hello.cc \
	    -ljvm -o hello

HelloWorld.class: HelloWorld.java
	javac HelloWorld.java
