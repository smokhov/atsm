#include "Externs.h"

void CheckPrime(K)
     int K;

  {  int J;
     /* the plan:  see if J divides K, for all values J which are
           (a) themselves prime (no need to try J if it is nonprime), and
           (b) less than or equal to sqrt(K) (if K has a divisor larger
              than this square root, it must also have a smaller one,
               so no need to check for larger ones) */

     for (J = 2; J*J <= K; J++)
        if (Prime[J] == 1)
           if (K % J == 0) {
              Prime[K] = 0;
              return;
           }

     /* if we get here, then there were no divisors of K, so it is
        prime */
     Prime[K] = 1;
}

