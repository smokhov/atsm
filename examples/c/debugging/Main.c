  /* prime-number finding program

     will (after bugs are fixed) report a list of all primes which are
     less than or equal to the user-supplied upper bound

     riddled with errors! */

#include <stdio.h>

#include "Defs.h"

int Prime[MaxPrimes],  /* Prime[I] will be 1 if I is prime, 0 otherwise */
    UpperBound;        /* we will check all number up through this one for
                          primeness */

int main(int argc, char**  argv)
{
	int N;

     printf("enter upper bound\n");
     scanf("%d", &UpperBound);

     Prime[2] = 1;

     for (N = 3; N <= UpperBound; N += 2) {
        CheckPrime(N);
        if (Prime[N]) printf("%d is a prime\n", N);
     }
}

