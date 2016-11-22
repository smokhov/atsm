
aaa.o: aaa.cpp aaa.h
	g++ -c aaa.cpp -o aaa.o

libaaa.a: aaa.o
	ar rvs libaaa.a aaa.o

libaaa.so: aaa.cpp aaa.h
	g++ -fpic -shared aaa.cpp -o libaaa.so

aaa_c_connector.o: aaa_c_connector.cpp aaa_c_connector.h
	g++ -c aaa_c_connector.cpp -o aaa_c_connector.o

libaaa_c_connector.a: aaa_c_connector.o aaa.o
	ar rvs libaaa_c_connector.a aaa_c_connector.o aaa.o

libaaa_c_connector.so: aaa_c_connector.cpp aaa_c_connector.h
	g++ -fpic -shared aaa_c_connector.cpp -L. -laaa -o libaaa_c_connector.so

c_aaa: main.c libaaa.so libaaa_c_connector.so
	gcc main.c -L. -laaa_c_connector -laaa -o c_aaa

#c_bbb: main.c aaa.o libaaa_c_connector.o
c_bbb: main.c libaaa_c_connector.a
	gcc main.c -L. libaaa_c_connector.a -lstdc++ -o c_bbb
