(ns tf.ui
  (:require [net.cgrand.enlive-html :as h]
            [net.cgrand.jsoup :as jsoup]
            [schema.core :as s]))

(h/set-ns-parser! jsoup/parser)

(h/deftemplate index-page* "templates/server/index.html"
  [version component-name]
  [:#tf-js] (h/replace-vars {:version version})
  [:#init-component] (h/set-attr :value component-name))

(defn index-page
  [{:keys [version sys-config] :as res}]
  (apply str (index-page* "0.0.1" "main")))
