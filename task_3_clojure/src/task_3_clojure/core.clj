(ns task-3-clojure.core)

(defn future-activate
  [n coll]
  (lazy-seq
    (when-let [s (seq coll)]
      (let [
            heads (doall (take n s))
            tail  (drop n s)]
        (concat heads (future-activate n tail))))))

(defn pfilter
  [pred coll]
  (let [chunk-size  512
        buffer-size 4]
    (->> coll
         (partition-all chunk-size)
         (map (fn [chunk]
                (future
                  (doall (filter pred chunk)))))
         (future-activate buffer-size)
         (mapcat deref))))

(defn heavy-pred [n]
  (cond (< n 2) false (= n 2) true (even? n) false
        :else (let [s (Math/sqrt n)]
                (loop [i 3] (if (> i s) true (if (zero? (mod n i)) false (recur (+ i 2))))))))

(defn benchmark []
  (let [data (range 1 5000000)]
    (println "Benchmarking start")
    (println "Original from clojure:")
    (time (doall (filter heavy-pred data)))
    (println "My implementation:")
    (time (doall (pfilter heavy-pred data)))))

(benchmark)