(ns task-5-clojure.core)

(defn dine
  [id left-fork right-fork think-time eat-time iterations restarts-atom]
  (dotimes [_ iterations]
    (Thread/sleep think-time)
    (dosync
      (swap! restarts-atom inc)
      (alter left-fork inc)
      (alter right-fork inc)
      (Thread/sleep eat-time))))

(defn run-experiment
  [num-philosophers think-time eat-time iterations]
  (let [forks    (vec (repeatedly num-philosophers #(ref 0)))
        restarts (atom 0)
        start    (System/currentTimeMillis)
        futures  (doall
                   (map (fn [i]
                          (let [left  (forks i)
                                right (forks (mod (inc i) num-philosophers))]
                            (future (dine i left right think-time eat-time iterations restarts))))
                        (range num-philosophers)))]
    (doseq [f futures] @f)

    (let [end          (System/currentTimeMillis)
          total-tx-run @restarts
          success-tx   (* num-philosophers iterations)
          retries      (- total-tx-run success-tx)]

      {:philosophers num-philosophers
       :iterations   iterations
       :think-time   think-time
       :eat-time     eat-time
       :total-time-ms (- end start)
       :restarts     retries
       :fork-usages  (mapv deref forks)})))

;(run-experiment 4 5 10 100)
;(run-experiment 5 5 10 100)
;(run-experiment 10 0 50 100)