(ns cmr.search.services.parameters.converters.temporal
  "Contains functions for parsing, validating and converting temporal query parameters to query conditions"
  (:require [clojure.string :as s]
            [cmr.common.services.errors :as err]
            [cmr.common.date-time-parser :as parser]
            [cmr.search.models.query :as qm]
            [cmr.search.models.group-query-conditions :as gc]
            [cmr.search.services.parameters.conversion :as p]))

(defn string->int-value
  "Return int value of the string value if it is not blank or nil if it is"
  [value]
  (when-not (s/blank? value) (Integer/parseInt value)))

;; Converts temporal parameter and values into query condition, returns the converted condition
(defmethod p/parameter->condition :temporal
  [concept-type param value options]
  (if (sequential? value)
    (if (= "true" (get-in options [:temporal :and]))
      (gc/and-conds
        (map #(p/parameter->condition concept-type param % options) value))
      (gc/or-conds
        (map #(p/parameter->condition concept-type param % options) value)))
    (let [[start-date end-date start-day end-day] (map s/trim (s/split value #","))]
      (qm/map->TemporalCondition {:start-date (when-not (s/blank? start-date) (parser/parse-datetime start-date))
                                  :end-date (when-not (s/blank? end-date) (parser/parse-datetime end-date))
                                  :start-day (string->int-value start-day)
                                  :end-day (string->int-value end-day)}))))

