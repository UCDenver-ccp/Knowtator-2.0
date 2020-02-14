(ns knowtator-clj.views
  (:require [knowtator-clj.subs-evts :as se]
            [knowtator-clj.util :as util :refer [<sub >evt]]
            [rid3.core :as rid3]
            [re-frame.core :as rf]))

(defn document-controls
  []
  (let [doc-ids (<sub [::se/doc-ids])]
    [:div
     [:select
      {:value     (or (<sub [::se/visible-doc-id]) (first doc-ids))
       :on-change #(>evt [::se/load-doc (util/target-value %)])
       :id        "available-docs"}
      (for [doc-id doc-ids]
        ^{:key (str (random-uuid))}
        [:option {:value doc-id}
         doc-id])]]))

(defn annotation-controls
  "UI for manipulating annotations"
  []
  [:div
   [:button {:on-click #(>evt [::se/add-ann])}
    "Add annotation"]
   [:button {:on-click #(>evt [::se/add-span])}
    "Add span"]
   [:button {:on-click #(>evt [::se/extend-span-start])}
    "<-Span"]
   [:button {:on-click #(>evt [::se/extend-span-end])}
    "Span->"]
   [:button {:on-click #(>evt [::se/shrink-span-start])}
    "->Span"]
   [:button {:on-click #(>evt [::se/shrink-span-end])}
    "Span<-"]])

(defn editor-foredrop
  []
  [:div#foredrop.foredrop {:style {:z-index 3
                                   :background-color :transparent}}
   [:div.highlights
    (doall
     (for [text (<sub [::se/selected-span-content])]
       (if (string? text)
         text
         (let [{:keys [id content selected]} text]
           ^{:key (str (random-uuid))}
           [:div.popup {:style {#_:border #_:solid
                                :color :black
                                :border-color :black}}
            (when selected
              [:span.popuptext.show {:style {:z-index 3}
                                     :id (str id "popup")}
               id])
            content]))))]])

(defn editor-text-area
  []
  [:textarea#textarea {:value (<sub [::se/visible-doc-content])
                       :on-select #(>evt [::se/record-selection
                                          (util/selection-start %)
                                          (util/selection-end %)
                                          (<sub [::se/visible-doc-id])])
                       :read-only true
                       :on-scroll #(util/unify-scroll "textarea" "backdrop" "foredrop")}])

(defn editor-backdrop
  []
  [:div#backdrop.backdrop
   [:div.highlights
    (doall
     (for [text (<sub [::se/highlighted-text])]
       (if (string? text)
         text
         (let [{:keys [content ann id]} text]
           ^{:key (str (random-uuid))}
           [:span {:style {:background-color (<sub [::se/ann-color ann])}}
            content]))))]])

(defn editor
  []
  [:div
   [document-controls]
   #_[annotation-controls]
   [:h2 (<sub [::se/visible-doc-id])]
   [:div#know.container
    [editor-foredrop]
    [editor-text-area]
    [editor-backdrop]]])

(defn graph-space
  "Displays the current graph space."
  []
  [:div
   [rid3/viz
    {:id     "graph-space"
     :ratom  (rf/subscribe [::se/graph-space])
     :svg    {:did-mount (fn [node ratom]
                           (rf/dispatch-sync [::se/perform-layout])
                           node)}
     :pieces [{:kind            :elem-with-data
               :tag             "text"
               :class           "node-text"
               :did-mount       (fn [node ratom]
                                  node
                                  (rid3/rid3-> node
                                               {:x           #(.-x %)
                                                :y           100}
                                               (.text #(.-text %))))
               :prepare-dataset #(clj->js (<sub [::se/graph-nodes]))}]}]])

(defn main-panel []
  [:div
   [:h1 "Knowtator"]
   #_[graph-space]
   [editor]
   [:span#ruler {:style {:visibility  :hidden
                         :white-space :nowrap}}]])
