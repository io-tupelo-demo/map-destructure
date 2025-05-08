(ns demo.core
  (:use tupelo.core)
  (:require
    [schema.core :as s]
    ))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (spy :main--enter)
  (spyx args)
  (spy :main--leave))
