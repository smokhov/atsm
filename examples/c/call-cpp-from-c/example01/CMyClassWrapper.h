#ifdef __cplusplus
#define EXTERNC extern "C"
#else
#define EXTERNC
#endif

#ifdef __cplusplus
class MyClass;
extern "C" {
#else
struct MyClass;
typedef struct MyClass MyClass;
#endif

//extern "C"
MyClass* MyClass_create();
//extern "C"
void MyClass_foo(MyClass* instance);
//extern "C"
void MyClass_release(MyClass* myclass);

#ifdef __cplusplus
}
#endif
