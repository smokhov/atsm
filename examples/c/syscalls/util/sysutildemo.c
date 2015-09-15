/*
 * sysutildemo.c
 *
 * Mini-demo of the utility functions readc() and cls()
 *
 * Serguei Mokhov, mokhov@cs.concordia.ca
 */

#include <stdio.h>

#include "sysutil.h"

int main(int argc, char* argv[])
{
	char c;

	cls();

 	printf("We just cleared the screen and waiting for you\n");
 	printf("to press any :) key to clear it again and terminate.\n");

 	c = readc();
 	cls();

 	printf("Character you just pressed: '%c'\n", c);
	printf("Terminating...\n\n");

 	return 0;
}

/* EOF */
