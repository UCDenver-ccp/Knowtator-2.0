(ns knowtator.html-util
  (:require [goog.object :as gobj]
            [re-frame.core :as rf :refer
             [->interceptor assoc-coeffect assoc-effect get-coeffect
              get-effect]]
            ["rangy/lib/rangy-textrange" :as rangy-txt]
            [reagent.core :as r]))

(defn win-inner-w [] js/window.innerWidth)

(defn win-inner-h [] js/window.innerHeight)

(def key-map
  {:enter 13
   :ctrl  17})

(defn get-element-by-id [id] (js/document.getElementById id))

(defn key? [e key] (= (.-charCode e) (key key-map)))

(defn get-label [d] (gobj/get d "label"))

(defn get-value [d] (gobj/get d "value"))

(defn get-id [d] (gobj/get d "id"))

(defn target-value
  "Returns value found in text field"
  [d]
  (-> d
      (.-target)
      (.-value)))

(defn selection-start
  "Returns selection start."
  [d]
  (-> d
      (.-target)
      (.-selectionStart)))

(defn selection-end
  "Returns selection start."
  [d]
  (-> d
      (.-target)
      (.-selectionEnd)))

(defn set-ons
  "Utility function for adding a bunch of ons."
  [node ons]
  (reduce (fn [node [on on-fn]] (.on node (name on) on-fn)) node ons))

(def <sub (comp deref rf/subscribe))
(def >evt rf/dispatch)

(defn remove-nth
  [v n]
  (-> v
      (subvec 0 n)
      (concat (subvec v (inc n)))
      (vec)))

(defn path-nth
  ([] (path-nth 0))
  ([i]
   (let [db-store-key         :re-frame-path/db-store
         first-path-store-key (keyword *ns*
                                       (str (name ::first-path-store)
                                            (random-uuid)))]
     (->interceptor
      :id     :viz-id-path
      :before (fn [context]
                (let [original-db (get-coeffect context :db)
                      viz-id      (get-in context [:coeffects :event i])
                      new-db      (get original-db viz-id)]
                  (-> context
                      (update-in [:coeffects :event] remove-nth i)
                      (update db-store-key conj original-db)
                      (assoc first-path-store-key viz-id)
                      (assoc-coeffect :db new-db))))
      :after  (fn [context]
                (let [db-store     (get context db-store-key)
                      original-db  (peek db-store)
                      new-db-store (pop db-store)
                      viz-id       (get context first-path-store-key)
                      context'     (-> context
                                       (assoc db-store-key new-db-store)
                                       (assoc-coeffect :db original-db)) ;; put
                                                                         ;; the
                                                                         ;; original
                                                                         ;; db
                                                                         ;; back
                                                                         ;; so
                                                                         ;; that
                                                                         ;; things
                                                                         ;; like
                                                                         ;; debug
                                                                         ;; work
                                                                         ;; later
                                                                         ;; on
                      db           (get-effect context :db ::not-found)]
                  (if (= db ::not-found)
                    context'
                    (->> db
                         (assoc original-db viz-id)
                         (assoc-effect context' :db)))))))))

(defn len-text
  "Calculates the length of a span of text."
  [text]
  (let [ruler (get-element-by-id "ruler")]
    (set! (.-innerHTML ruler) text)
    (.-offsetWidth ruler)))

(defn unify-scroll
  "Unifies the scrolling of two elements"
  [master-id & follower-ids]
  (let [follower-elems (map get-element-by-id follower-ids)
        master-elem    (get-element-by-id master-id)]
    (doseq [e follower-elems]
      (set! (.-scrollTop e) (.-scrollTop master-elem)))))

(defn class?
  [e c]
  (-> e
      .-classList
      (.contains c)))

(defn get-parent-element-of-class
  [e c]
  (if (class? e c) e (recur (.-parentElement e) c)))

(defn text-selection
  [e c]
  (let [e (get-parent-element-of-class e c)]
    (-> rangy-txt
        .getSelection
        (.saveCharacterRanges e)
        (js->clj :keywordize-keys true)
        first
        :characterRange)))

(defonce window-size
         (let [a (r/atom {:width  (.-innerWidth js/window)
                          :height (.-innerHeight js/window)})]
           (.addEventListener js/window
                              "resize"
                              (fn []
                                (reset! a
                                        {:width  (.-innerWidth js/window)
                                         :height (.-innerHeight js/window)})))
           a))
