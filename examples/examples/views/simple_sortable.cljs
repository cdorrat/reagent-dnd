(ns examples.views.simple-sortable
  (:require-macros [devcards.core :as dc])
  (:require [re-frame.core :refer [subscribe dispatch]]
            [clojure.string :as str]
            [devcards.core :as dc]
            [devcards.util.edn-renderer]
            [reagent.core :as r]
            [reagent-dnd.core :as dnd]
            [reagent.ratom]
            [markdown.core :refer [md->html]]
            [medley.core :refer [map-vals]]
            [examples.utils :refer [make-title make-url document card]]
            ["react-dom" :refer [findDOMNode]]))


(defn on-hover [idx-from-id card-data monitor component]
  (let [hover-idx (idx-from-id (:id card-data))
        drag-index (-> monitor :item :id idx-from-id)] ;; need to find index from id
    (when (not= drag-index hover-idx)
      (let [hover-rect (-> component findDOMNode .getBoundingClientRect)
            hover-middle-y (/ (- (.-bottom hover-rect) (.-top hover-rect)) 2)
            hover-client-y (-> monitor :client-offset last (- (.-top hover-rect)))]
        (when (or
               (and (< drag-index hover-idx) (>= hover-client-y hover-middle-y))
               (and (> drag-index hover-idx) (<= hover-client-y hover-middle-y)))
          (dispatch [:move-sortable-card drag-index hover-idx]))))))

(defn box [{:keys [id text]} drag-state drop-state]
  [:div {:style {:opacity (if (:dragging? @drag-state) 0 1)
                 :background-color "white"
                 :font-size "larger"
                 :padding "10px"
                 :width "15em"
                 :border "1px solid black"}} text])

(defn draggable-card [idx-from-id card-data]
  (let [drag-state (r/atom {})
        drop-state (r/atom {})]
   [dnd/drop-target
    :types [:box]
    :state drop-state
    ;; :drop (fn [monitor]
    ;;         (println "dropped")
    ;;         true)
    :hover (partial on-hover idx-from-id card-data)
    :child [dnd/drag-source
            :state drag-state 
            :type :box
            :begin-drag (fn [] {:id (:id card-data)})
            :dragging? (fn [mon]
                         (and (not (:dropped? mon))
                              (= (:id card-data) (-> mon :item :id))))
            :child [box card-data drag-state ]]]))

(defn sorted-container []
  (let [data (subscribe [:simple-sorted-data])
        idx-from-id (into {} (map-indexed #(vector (:id %2) %1) @data))]
    [:div {:class "sorted-container"}
     (doall (map-indexed (fn [idx data]
                           ^{:key (str "simp-"idx)}
                           [draggable-card idx-from-id data])
                         @data))]))

(defn view []
  [:div
   [:div {:style {:display         :flex
                  :flex-wrap       :wrap
                  :justify-content :space-around}}
    [:div {:style {:flex "0 1 auto"}}     
     [sorted-container]]]])
