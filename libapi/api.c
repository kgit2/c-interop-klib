//
// Created by BppleMan on 2022/11/10.
//

#include <stdlib.h>
#include "api.h"

#include <stdio.h>

void hello(void) {
    printf("Hello, World!\n");
}

void get_strings(const char **strings, int length) {
    // *strings = (char **) malloc(sizeof(char **) * length);
    for (int i = 0; i < length; ++i) {
        strings[i] = (char *) malloc(sizeof(char) * 10);
        sprintf(strings[i], "Hello, %d", i);
    }
}
