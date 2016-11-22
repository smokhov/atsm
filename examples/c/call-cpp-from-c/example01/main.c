#include "CMyClassWrapper.h"

int main(int argc, char** argv)
{
	MyClass* myclass = MyClass_create();
	/*MyClass_sendCommandToSerialDevice(myclass,1,2,3);*/
	MyClass_foo(myclass);
	MyClass_release(myclass);
	return 0;
}
