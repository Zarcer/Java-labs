(ns task-3-clojure.core-test
  (:require [clojure.test :refer :all]
            [task-3-clojure.core :refer :all]))

(deftest pfilter-test
  (testing "Basic functionality"
    (is (= (filter even? (range 20))
           (pfilter even? (range 20)))))

  (testing "Infinite Sequence"
    (let [inf-seq (range)
          result  (take 5 (pfilter (fn [x] (zero? (mod x 100))) inf-seq))]
      (is (= '(0 100 200 300 400) result))))

  (testing "Order saved"
    (let [data (shuffle (range 100))]
      (is (= (filter odd? data)
             (pfilter odd? data))))))

(run-tests)