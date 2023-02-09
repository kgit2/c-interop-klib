//
// Created by BppleMan on 2022/11/10.
//

#ifndef API_API_H
#define API_API_H

void hello(void);

typedef struct api_name {
    char *first;
    char *last;
} api_name;

typedef struct api_user {
    api_name *name;
    int age;
} api_user;

void get_strings(const char **strings, int length);

#endif //API_API_H
