#include <jni.h>
#include <stdio.h>

JNIEnv* create_vm(JavaVM ** jvm)
{
    JNIEnv *env;
    JavaVMInitArgs vm_args;
    JavaVMOption options[2];

    options[0].optionString = (char *)"-Djava.class.path=.";
    options[1].optionString = (char *)"-DXcheck:jni:pedantic";  

    vm_args.version = JNI_VERSION_1_6;
    vm_args.nOptions = 2;
    vm_args.options = options;
    vm_args.ignoreUnrecognized = JNI_TRUE; // remove unrecognized options

    int ret = JNI_CreateJavaVM(jvm, (void**) &env, &vm_args);
    if (ret < 0) printf("\n<<<<< Unable to Launch JVM >>>>>\n");
    return env;
}

int main(int argc, char* argv[])
{
    JNIEnv* env;
    JavaVM* jvm;

    env = create_vm(&jvm);

    if (env == NULL) return 1;

    jclass myClass = NULL;
    jmethodID main = NULL;


    myClass = env->FindClass("HelloWorld");


    if (myClass != NULL)
        main = env->GetStaticMethodID(myClass, "main", "([Ljava/lang/String;)V");
    else
        printf("Unable to find the requested class\n");


    if (main != NULL)
    {
       env->CallStaticVoidMethod( myClass, main, " ");

    }else printf("main method not found") ;


    jvm->DestroyJavaVM();
    return 0;
}
