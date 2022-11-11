#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include "libnative_api.h"
#include "api.h"

int main() {
    libnative_ExportedSymbols *lib = libnative_symbols();

    void *arena = NULL;
    api_user *apiUser = (api_user *) lib->kotlin.root.createUser("Foo", "Bar", 90, &arena);
    assert(apiUser != NULL);
    assert(apiUser->age == 90);
    printf("Hello, %s %s! You are %d years old.\n", apiUser->name->first, apiUser->name->last, apiUser->age);
    // warning: free this pointer will get error, because it is allocated by Kotlin/Native
    // warning: this approach will cause hidden dangers of memory leaks
    // free(apiUser);
    lib->kotlin.root.freeUser(&arena);
    // warning: will error (interrupted by signal 11: SIGSEGV)
    // warning: it means that the pointer has been freed
    // printf("Hello, %s %s! You are %d years old.\n", apiUser->name->first, apiUser->name->last, apiUser->age);

    libnative_kref_User krefUser = lib->kotlin.root.createUserForKClass("Foo", "Bar", 91);
    apiUser = (api_user *) lib->kotlin.root.User.handler(krefUser);
    assert(apiUser->age == 91);
    printf("Hello, %s %s! You are %d years old.\n", apiUser->name->first, apiUser->name->last, apiUser->age);
    lib->kotlin.root.User.free(krefUser);
    // warning: will error (interrupted by signal 11: SIGSEGV)
    // warning: it means that the pointer has been freed
    // printf("Hello, %s %s! You are %d years old.\n", apiUser->name->first, apiUser->name->last, apiUser->age);

    lib->kotlin.root.createUserForKClass("Foo", "Bar", 92);
    lib->kotlin.root.createUserForKClass("Foo", "Bar", 93);

    void *arenaPointer = NULL;
    int size = 0;
    libnative_kref_User *krefUsers = (libnative_kref_User *)lib->kotlin.root.getUserCArray(&size, &arenaPointer);
    assert(size == 4);
    for (int i = 0; i < size; ++i) {
        apiUser = (api_user *) lib->kotlin.root.User.handler(krefUsers[i]);
        assert(apiUser->age == 90 + i);
        printf("Hello, %s %s! You are %d years old.\n", apiUser->name->first, apiUser->name->last, apiUser->age);
    }

    lib->kotlin.root.freeUserCArray(krefUsers, &arenaPointer, size);
    return 0;
}
