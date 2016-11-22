#include "MyClass.cpp"
#include "CMyClassWrapper.h"

//extern "C" 
EXTERNC
MyClass* MyClass_create()
{
   return new MyClass;
}

//extern "C"
EXTERNC
void MyClass_release(MyClass* myclass)
{
   delete static_cast<MyClass*>(myclass);
}

/*
extern "C" void MyClass_sendCommandToSerialDevice(yypvoid* myclass, int cmd, int params, int id)
*/
//extern "C"
EXTERNC
void MyClass_foo(MyClass* myclass)
{
   static_cast<MyClass*>(myclass)->foo();
}
