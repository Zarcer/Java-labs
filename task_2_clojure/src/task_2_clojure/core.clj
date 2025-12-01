(ns task-2-clojure.core)

(defn sieve
  [s]
  (cons (first s)
        (lazy-seq
          (sieve
            (remove #(zero? (mod % (first s))) (rest s))))))

(def primes
  (sieve (iterate inc 2)))