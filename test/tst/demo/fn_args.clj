(ns tst.demo.fn-args
  (:use demo.core
        tupelo.core
        tupelo.test))

;---------------------------------------------------------------------------------------------------
; Full documentation:  https://clojure.org/guides/destructuring
;---------------------------------------------------------------------------------------------------

(defn f0 [] :dummy) ; 0-arg function
(defn f1 [a] (vals->map a)) ; 1-arg function
(defn f2 [a b] (vals->map a b)) ; 2-arg function
(defn f3 [a b c] (vals->map a b c)) ; 3-arg function

(verify
  (is= (f0) :dummy) ; just right args
  (throws? (f0 1))  ; too many args
  (throws? (f0 1 2)) ; too many args
  ;
  (throws? (f1))    ; too few args
  (is= (f1 1) {:a 1}) ; just right args
  (throws? (f1 1 2)) ; too many args

  (throws? (f2))    ; too few args
  (throws? (f2 1))  ; too few args
  (is= (f2 1 2) {:a 1 :b 2}) ; just right args
  (throws? (f2 1 2 3)) ; too many args

  (throws? (f3))    ; too few args
  (throws? (f3 1))  ; too few args
  (throws? (f3 1 2)) ; too few args
  (is= (f3 1 2 3) {:a 1 :b 2 :c 3}) ; just right args
  (throws? (f3 1 2 3 4)) ; too many args
  )

(defn f0r [& others] (vals->map others)) ; 0-arg function + rest args
(defn f1r [a & others] (vals->map a others)) ; 1-arg function + rest args
(defn f2r [a b & others] (vals->map a b others)) ; 2-arg function + rest args
(defn f3r [a b c & others] (vals->map a b c others)) ; 3-arg function + rest args

; NOTE that when there are no rest args, `others` is set to `nil`
; and is never the empty vector `[]`
(verify
  (is= (f0r) {:others nil}) ; min num args
  (is= (f0r 1) {:others [1]}) ; min num args
  (is= (f0r 1 2) {:others [1 2]}) ; more than min args
  (is= (f0r 1 2 3) {:others [1 2 3]}) ; more than min args

  (throws? (f1r))   ; too few args
  (is= (f1r 1) {:a 1 :others nil}) ; min num args
  (is= (f1r 1 2) {:a 1 :others [2]}) ; more than min args
  (is= (f1r 1 2 3) {:a 1 :others [2 3]}) ; more than min args

  (throws? (f2r))   ; too few args
  (throws? (f2r 1)) ; too few args
  (is= (f2r 1 2) {:a 1 :b 2 :others nil}) ; min num args
  (is= (f2r 1 2 3) {:a 1 :b 2 :others [3]}) ; more than min args
  (is= (f2r 1 2 3 4) {:a 1 :b 2 :others [3 4]}) ; more than min args

  (throws? (f3r))   ; too few args
  (throws? (f3r 1)) ; too few args
  (throws? (f3r 1 2)) ; too few args
  (is= (f3r 1 2 3) {:a 1 :b 2 :c 3 :others nil}) ; min num args
  (is= (f3r 1 2 3 4) {:a 1 :b 2 :c 3 :others [4]}) ; more than min args
  (is= (f3r 1 2 3 4 5) {:a 1 :b 2 :c 3 :others [4 5]}) ; more than min args

  ; Note: the type of the rest args value fits both "seq" and "sequential".
  ; It compares equal to any other "seq" with the same values
  (let [result (f2r 1 2 3 4)]
    (with-map-vals result [a b others]
      (is= a 1)
      (is= b 2)

      (is= others [3 4]) ; vector
      (is= others (list 3 4)) ; list
      (is= others '(3 4)) ; list using "reader macro" syntax

      (is= (type others) clojure.lang.ArraySeq) ; detailed type
      (is (seq? others)) ; it is a "seq"
      (is (sequential? others)) ; it is "seqential"

      ; Direct parent Class & Interfaces
      (is= (parents clojure.lang.ArraySeq)
        #{clojure.lang.ASeq
          clojure.lang.IReduce
          clojure.lang.IndexedSeq})

      ; All parent Classes & Interfaces
      (is= (ancestors clojure.lang.ArraySeq)
        #{clojure.lang.ASeq
          clojure.lang.Counted
          clojure.lang.IHashEq
          clojure.lang.IMeta
          clojure.lang.IObj
          clojure.lang.IPersistentCollection
          clojure.lang.IReduce
          clojure.lang.IReduceInit
          clojure.lang.ISeq
          clojure.lang.IndexedSeq
          clojure.lang.Obj
          clojure.lang.Seqable
          clojure.lang.Sequential
          java.io.Serializable
          java.lang.Iterable
          java.lang.Object
          java.util.Collection
          java.util.List
          java.util.SequencedCollection})))

  )

