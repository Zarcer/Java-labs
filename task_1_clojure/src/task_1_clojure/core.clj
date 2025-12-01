(ns task-1-clojure.core)

(defn all-forms [alphabet n]
  (if (<= n 1)
    alphabet
    (reduce
      (fn [current-strings _index]
        (apply concat
               (map (fn [s]
                      (map (fn [char] (str s char))
                           (filter (fn [c] (not= c (str (last s)))) alphabet)))
                    current-strings)))
      alphabet
      (range 1 n))))