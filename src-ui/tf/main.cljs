(ns tf.main
  (:require [taoensso.timbre :as log]
            [tf.common.data :as data]
            [re-frame.core :as rf]
                                        ;[re-frisk.core :as re-frisk]
            [schema.core :as s]
            [reagent.core :as reagent]
            [tf.net.api :as api]
            [day8.re-frame.http-fx]
            [hodgepodge.core :as hp]
            [tf.events :as e]
            [tf.views :as v]
            [tf.routes :as routes]
            [e85th.ui.rf.fx]
            [e85th.ui.util :as u]
            [e85th.ui.edn-io]))


(def name->component
  {"main" [v/main-panel]})

(defn mount-root!
  [nm]
  (reagent/render (get name->component nm [:h2 "TF: Unknown component"])
                  (u/element-by-id "app")))

(defn init
  []
  ;; FIXME: need a configurable way to set this
  (s/set-fn-validation! true)
  (data/set-api-host! js/window.location.origin)
  (routes/init!)
  (let [nm (u/element-value "init-component")]
    (mount-root! nm)))

(set! (.-onload js/window) init)
