(ns examples.handlers
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [com.rpl.specter :as s :refer-macros [select transform]]))

(def nested-drop-targets-dummy
  {:root1 {:children [:a]}
   :a     {:children [:b]}
   :b     {:children [:c]}
   :c     {:children [:d]}
   :d     {:children [:e]}
   :e     {:children [:f]}
   :f     {:children [:g]}
   :g     {:children []}})

(def nested-drag-sources-dummy
  {:root {:color    :blue
          :children [:b :c]}
   :b    {:color    :yellow
          :children [:d :e]}
   :c    {:color    :blue
          :children [:f]}
   :d    {:color :yellow}
   :e    {:color :blue}
   :f    {:color :yellow}})

(def simple-sortable-cards
  [{:id 1 :text "Write a cool CLJS library"}
   {:id 2 :text "Make it generic enough"}
   {:id 3 :text "Write README"}
   {:id 4 :text "Create some examples"}
   {:id 5 :text "Spam in Twitter and IRC to promote it (note that this element is taller than the others)"}
   {:id 6 :text "???"}
   {:id 7 :text "PROFIT"}]
  )

(def dummy-db
  {:examples {:stress-test       {:drag-sources [:glass :banana :pepper]
                                  :drop-targets [:glass :banana :pepper]}
              :drag-around-naive {:position [80 80]}
              :nested-drag-sources nested-drag-sources-dummy
              :nested-drop-targets nested-drop-targets-dummy
              :simple-sortable-cards simple-sortable-cards}})

(re-frame/reg-event-db
 :initialize-db
 (fn [] dummy-db))

(defn set-viewing
  [db [_ panel]]
  (assoc db :active-panel panel))

(re-frame/reg-event-db
 :set-viewing
 set-viewing)

(defn set-viewing-examples
  [db [_ example]]
  (assoc db
         :active-panel :examples
         :example example))

(re-frame/reg-event-db
 :set-viewing-examples
 set-viewing-examples)

(re-frame/reg-event-db
 :rearrange-stress-test
 (fn [db _]
   (-> db
       (update-in [:examples :stress-test :drop-targets] shuffle)
       (update-in [:examples :stress-test :drag-sources] shuffle))))

(re-frame/reg-event-db
 :move-naive-handler
 (fn [db [_ [x y]]]
   (update-in db [:examples :drag-around-naive] assoc :position [x y])))

(re-frame/reg-event-db
 :toggle-forbid-drag-source
 (fn [db [_ id]]
   (update-in db [:examples :nested-drag-sources id :forbidden?] not)))

(re-frame/reg-event-db
 :nested-drag-source-dropped
 (fn [db [_ id]]
   db))

(re-frame/reg-event-db
 :toggle-greedy-drop-target
 (fn [db [_ id]]
   (update-in db [:examples :nested-drop-targets id :greedy?] not)))

(re-frame/reg-event-db
 :dropped-on-nested-drop-target
 (fn [db [_ id & {:keys [dropped?]}]]
   (let [greedy? (get-in db [:examples :nested-drop-targets id :greedy?])]
     (if (and dropped? (not greedy?)) ;; handled already
       db
       (update-in db [:examples :nested-drop-targets id]
                  assoc
                  :dropped? true
                  :dropped-on-child? dropped?)))))

(re-frame/reg-event-db
 :initialize-nested-drop-targets
 (fn [db]
   (assoc-in db [:examples :nested-drop-targets] nested-drop-targets-dummy)))



(re-frame/reg-event-db
 :move-sortable-card
 (fn [db [_ drag-index hover-index]]
   (s/transform [:examples :simple-sortable-cards]
                (fn [elem-list]
                  (let [drop-idx (if (> drag-index hover-index) hover-index (dec hover-index))
                        drag-card (s/select [drag-index] elem-list)]
                    (->> elem-list
                         (s/setval [drag-index] s/NONE)
                         (s/setval [(s/srange drop-idx drop-idx)] drag-card))))
                db)))
