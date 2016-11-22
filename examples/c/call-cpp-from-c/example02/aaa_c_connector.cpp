#include <cstdlib>

#include "aaa_c_connector.h"
#include "aaa.h"

#ifdef __cplusplus
extern "C" {
#endif

// Inside this "extern C" block, I can define C functions that are able to call C++ code

static AAA *AAA_instance = NULL;

void lazyAAA() {
    if (AAA_instance == NULL) {
        AAA_instance = new AAA();
    }
}

void AAA_sayHi(const char *name) {
    lazyAAA();
    AAA_instance->sayHi(name);
}

#ifdef __cplusplus
}
#endif

