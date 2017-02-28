(ns tf.main
  "UI entry point into the application."
  (:require [tf.common.data :as data]
            [tf.views :as v]
            [schema.core :as s]
            [reagent.core :as reagent]
            [day8.re-frame.http-fx] ; loads and makes http-xhrio handler available
            [e85th.ui.rf.fx] ; loads and makes re-frame fx handlers available
            [e85th.ui.edn-io] ; loads support for application/edn content type
            [e85th.ui.util :as u]))


(defn init
  []
  (s/set-fn-validation! true)
  (data/set-api-host! js/window.location.origin)
  (reagent/render [v/main-panel] (u/element-by-id "app")))

(set! (.-onload js/window) init)
