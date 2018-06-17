(defproject cdorrat/reagent-dnd "0.2.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [reagent "0.8.0"
                  :exclusions [cljsjs/create-react-class
                               cljsjs/react-dom-server
                               cljsjs/react-dom
                               cljsjs/react]]]

  :source-paths ["src/clj" "src/cljs"]

  ;; ==========================================================================================
  ;; project.clj is only used for packaging & deploying to clojars
  ;; for the examples look at the shadow-cljs build & README
  
  )
