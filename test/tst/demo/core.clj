(ns tst.demo.core
  (:use demo.core
        tupelo.core
        tupelo.test)
  (:require
    [schema.core :as s]
    [tupelo.schema :as tsk]
    ))

;---------------------------------------------------------------------------------------------------
; Full documentation:  https://clojure.org/guides/destructuring
;---------------------------------------------------------------------------------------------------

(verify
  ; We want to extract values from a simple map
  (let [m {:a 1 :b 2}]
    ;-----------------------------------------------------------------------------
    ; ***** BEWARE of typos in keyword, or you get `nil` results! *****

    ; Use keyword as a function
    (let [a (:a m)
          b (:b m)
          c (:c m)]
      (is= a 1)
      (is= b 2)
      (is= c nil))  ; missing values default to `nil`

    ; use `get`
    (let [a (get m :a)
          b (get m :b)
          c (get m :c)]
      (is= a 1)
      (is= b 2)
      (is= c nil))  ; missing values default to `nil`

    ; associative destructure
    (let [{a :a
           b :b
           c :c} m]
      (is= a 1)
      (is= b 2)
      (is= c nil))  ; missing values default to `nil`

    ; `:keys` destructure
    (let [{:keys [a b c]} m]
      (is= a 1)
      (is= b 2)
      (is= c nil))  ; missing values default to `nil`

    ;-----------------------------------------------------------------------------
    ; Extract with default value for missing keywords
    ; ***** BEWARE of typos in keyword, or you get the default value! *****
    ;
    ; Use keyword as a function with default values
    (let [a (:a m 5)
          b (:b m 6)
          c (:c m 7)]
      (is= a 1)
      (is= b 2)
      (is= c 7))    ; use supplied default for missing value

    ; use `get` with default
    (let [a (get m :a 5)
          b (get m :b 6)
          c (get m :c 7)]
      (is= a 1)
      (is= b 2)
      (is= c 7))    ; use supplied default for missing value

    ; associative destructure with `:or` defaults
    (let [{a   :a
           b   :b
           c   :c
           ; vvv default values are from the `:or` part
           :or {a 5
                b 6
                c 7}} m]
      (is= a 1)
      (is= b 2)
      (is= c 7))    ; use supplied default for missing value

    ; `:keys` destructure with `:or` defaults
    (let [{:keys [a b c]
           :or   {a 5 ; default values from here
                  b 6
                  c 7}} m]
      (is= a 1)
      (is= b 2)
      (is= c 7))    ; missing values default to `nil`

    ;-----------------------------------------------------------------------------
    ; destructure with `:as` result => returns "input" map

    ;-------------------------------------------------------
    ; Without `:or` defaults
    (let [{:keys [a]
           :as   orig} m]
      (is= a 1)
      (is= orig m)) ; `:as` always yields "input" map

    (let [{:keys [a b]
           :as   orig} m]
      (is= a 1)
      (is= b 2)
      (is= orig m)) ; `:as` always yields "input" map

    (let [{:keys [a b c]
           :as   orig} m]
      (is= a 1)
      (is= b 2)
      (is= c nil)
      (is= orig m)) ; `:as` always yields "input" map

    ;-------------------------------------------------------
    ; With `:or` defaults
    (let [{:keys [a]
           :or   {a 5
                  b 6
                  c 7}
           :as   orig} m]
      (is= a 1)
      (is= orig m)) ; `:as` always yields "input" map

    (let [{:keys [a b]
           :or   {a 5
                  b 6
                  c 7}
           :as   orig} m]
      (is= a 1)
      (is= b 2)
      (is= orig m)) ; `:as` always yields "input" map

    (let [{:keys [a b c]
           :or   {a 5 ; default values from here
                  b 6
                  c 7}
           :as   orig} m]
      (is= a 1)
      (is= b 2)
      (is= c 7)
      (is= orig m)) ; `:as` always yields "input" map

    ;-----------------------------------------------------------------------------
    ; You COULD use `:or` defaults and `:as` result with simple associative destructuring
    ; if you wanted.
    (let [{a   :a
           b   :b
           c   :c
           :or {a 5 ; default values from here
                b 6
                c 7}
           :as orig} m]
      (is= a 1)
      (is= b 2)
      (is= c 7)
      (is= orig m))))

;---------------------------------------------------------------------------------------------------
; Desctucturing for function calls

(s/defn f
  "Destructure in `(let ...)` statement"
  [m :- tsk/KeyMap]
  (let [{:keys [a b c]
         :or   {a 5
                b 6
                c 7}
         :as   orig} m] ; `:as` result unneeded since have original function arg `m`
    (is= a 1)
    (is= b 2)
    (is= c 7)
    (is= orig m)))

(s/defn g
  "Destructure in arg list"
  [{:keys [a b c]
    :or   {a 5
           b 6
           c 7}
    :as   orig}]    ; <= true purpose of `:as` result is to name original function arg
  (is= a 1)
  (is= b 2)
  (is= c 7)
  (is= orig {:a 1 :b 2})) ; `m` arg does not exist, need literal value

(verify
  (f {:a 1 :b 2})
  (g {:a 1 :b 2}))

;---------------------------------------------------------------------------------------------------
; ***** The BEST way to construct/destructure maps *****

(verify
  (let [m {:a 1 :b 2}]
    (with-map-vals m [a b] ; simpler version of `:keys` destructuring
      (is= a 1)
      (is= b 2)

      (let [M (vals->map a b)] ; create a keyword map from variables
        (is= M m))) ; same as original

    (throws-not?    ; `:a` and `:b` exist in map => normal flow
      (with-map-vals m [a b]
        (is= a 1)
        (is= b 2)))
    (throws?        ; Safe against typos & missing values: throw Exception due to `c`
      (with-map-vals m [a c]))))

(s/defn h
  "Best way to destructure args, including default values"
  [m :- tsk/KeyMap]
  (let [defaults {:a 5
                  :b 6
                  :c 7}
        params   (glue defaults m)] ; like `(merge m1 m2)` but safer
    (is= nil (:c m)) ; missing value is nil
    (with-map-vals params [a b c]
      (is= a 1)     ; override default value
      (is= b nil)   ; explicit nil input overrides default value
      (is= c 7)     ; missing value replaced with default
      )))

(verify
  (h {:a 1 :b nil})
  )

; create a map with default values
(verify
  ; set up default values
  (let [defaults {:a 5
                  :b 6
                  :c 7}]

    ; use local & default values to create final result
    (let [a      1
          b      2
          result (glue defaults (vals->map a b))]

      (is= result {:a 1
                   :b 2
                   :c 7}))))