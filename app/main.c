#include <stdio.h>
#include "libnative_api.h"
#include "api.h"

int main() {
    libnative_ExportedSymbols *lib = libnative_symbols();
    api_user *apiUser = (api_user *) lib->kotlin.root.createUser("Bpple", "Man", 90);
    printf("Hello, %s %s! You are %d years old.\n", apiUser->name->first, apiUser->name->last, apiUser->age);
    libnative_kref_User krefUser = lib->kotlin.root.createUserForKClass("Bpple", "Man", 91);
    apiUser = (api_user *) lib->kotlin.root.User.get_handler(krefUser);
    printf("Hello, %s %s! You are %d years old.\n", apiUser->name->first, apiUser->name->last, apiUser->age);
    lib->kotlin.root.User.free(krefUser);
    // will error (interrupted by signal 11: SIGSEGV)
    // printf("Hello, %s %s! You are %d years old.\n", apiUser->name->first, apiUser->name->last, apiUser->age);
    return 0;
}
