{{#
    fun fib(n) {
        if (n <= 1) {
            return n;
        }

        return fib(n - 1) + fib(n - 2);
    }
-}}

Expected 8, Actual: {{ fib(6) }}