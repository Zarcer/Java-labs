(ns task-2-clojure.core-test
  (:require [clojure.test :refer :all]
            [task-2-clojure.core :refer :all]))

(deftest test-prime-sequence

  (testing "Basic Sieve Working"
    (is (= (take 10 primes)
           '(2 3 5 7 11 13 17 19 23 29))))

  (testing "Large prime"
    (is (= (nth primes 99) 541)))

  (testing "Check for even after 2"
    (let [sample (take 100 (drop 1 primes))]
      (is (every? odd? sample)))))

(run-tests)