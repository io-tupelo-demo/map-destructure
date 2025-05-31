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
; few supplied args or too many. Result is either `nil` placeholder values,
; or dropped values,  respectively.
(verify
  (let [[a b c] x0] ; too few items => nil values
    (is= [nil nil nil] [a b c]))

  (let [[a b c] x1] ; too few items => nil values
    (is= [1 nil nil] [a b c]))

  (let [[a b c] x2] ; too few items => nil values
    (is= [1 2 nil] [a b c]))

  (let [[a b c] x3] ; just right number of items
    (is= [1 2 3] [a b c]))

  (let [[a b c] x4] ; too many items => extras dropped
    (is= [1 2 3] [a b c]))

  (let [[a b c] x5] ; too many items => extras dropped
    (is= [1 2 3] [a b c])))

; number of named args = 2
(verify
  (let [[a b & others] x0] ; less than num named args
    (is= [a b others]
      [nil nil nil]))

  (let [[a b & others] x1] ; less than num named args
    (is= [a b others]
      [1 nil nil]))

  (let [[a b & others] x2] ; all num named args
    (is= [a b others]
      [1 2 nil]))   ; NOTE: `others` is nil here, not empty vector

  (let [[a b & others] x3] ; more than num named args
    (is= [a b others]
      [1 2 [3]]))

  (let [[a b & others] x4] ; more than num named args
    (is= [a b others]
      [1 2 [3 4]]))

  (let [[a b & others] x5] ; more than num named args
    (is= [a b others]
      [1 2 [3 4 5]])
    ))

; alternate syntax  using (vals->map ...)
(verify
  (let [[a b c] x2]
    (is= (vals->map a b c)
      {:a 1 :b 2 :c nil}))

  (let [[a b & others] x2]
    (is= (vals->map a b others)
      {:a 1 :b 2 :others nil}))

  )

