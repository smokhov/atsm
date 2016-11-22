
all: maincwrapper

maincwrapper: main.o CMyClassWrapper.o MyClass.o
	gcc *.o -lstdc++ -o maincwrapper

main.o: main.c CMyClassWrapper.h Makefile
	gcc -c main.c -o main.o

CMyClassWrapper.o: CMyClassWrapper.cpp CMyClassWrapper.h
	g++ -c CMyClassWrapper.cpp

MyClass.o: MyClass.cpp
	g++ -c MyClass.cpp
