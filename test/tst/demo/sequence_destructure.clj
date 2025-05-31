(ns tst.demo.sequence-destructure
  (:use demo.core
        tupelo.core
        tupelo.test))

;---------------------------------------------------------------------------------------------------
; Full documentation:  https://clojure.org/guides/destructuring
;---------------------------------------------------------------------------------------------------

(def x0 [])
(def x1 [1])
(def x2 [1 2])
(def x3 [1 2 3])
(def x4 [1 2 3 4])
(def x5 [1 2 3 4 5])

; Sequential destructuring in a `let` form does not throw an exception for
; insufficient number of supplied args.
(verify
  (let [[a b c d e] x0]
    (is= [nil nil nil nil nil] [a b c d e]))
  (let [[a b c d e] x1]
    (is= [1 nil nil nil nil] [a b c d e]))
  (let [[a b c d e] x2]
    (is= [1 2 nil nil nil] [a b c d e]))
  (let [[a b c d e] x3]
    (is= [1 2 3 nil nil] [a b c d e]))
  (let [[a b c d e] x4]
    (is= [1 2 3 4 nil] [a b c d e]))
  (let [[a b c d e] x5]
    (is= [1 2 3 4 5] [a b c d e])))

; number of named args = 2
(verify
  (let [[a b & others] x0]
    (is= [nil nil nil] [a b others])) ; less than num named args
  (let [[a b & others] x1]
    (is= [1 nil nil] [a b others])) ; less than num named args
  (let [[a b & others] x2]
    (is= [1 2 nil] [a b others])) ; all num named args
  (let [[a b & others] x3]
    (is= [1 2 [3]] [a b others])) ; more than num named args args
  (let [[a b & others] x4]
    (is= [1 2 [3 4]] [a b others])) ; more than num named args args
  (let [[a b & others] x5]
    (is= [1 2 [3 4 5]] [a b others]) ; more than num named args args
    ))
