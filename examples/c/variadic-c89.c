#include <stdio.h>

/* In C89/90, you need multiple macros with "funny" names to do this.*/
/* Anyway, in this example, the arguments passed (those represented  */
/* by the ...) replace the __VA_ARGS__ when the substitution occurs. */
#define DoLogFile(...) fprintf(stderr, __VA_ARGS__)

/* Variadic macros also help out if you want to effectively */
/* pass one argument  that contains commas */
#define MakeName(...)  #__VA_ARGS__

/* Of course, the macro can also take other args, so long as */
/* the ... is last */
#define output(FILEptr, ...) fprintf(FILEptr, __VA_ARGS__)

int main(int argc, char** argv)
{
    int bound = 99;
    int current_index = 88;
    int linenumber = 77;
	char *p;

    DoLogFile("Detected array out of bounds\n");
    DoLogFile("Bound = %d, current index =%d\n", bound, current_index);
    DoLogFile("Missing semicolon on line %d\n", linenumber);
    output(stderr, "Detected array out of bounds\n");

    p = MakeName(Mary);
    p = MakeName(Doe, John);
    p = MakeName(Martin Luther King, Jr.);
    /* Combo two variadic macros */
    DoLogFile("MakeName=%s\n", MakeName(This is a test, yes it is));

	return 0;
}
