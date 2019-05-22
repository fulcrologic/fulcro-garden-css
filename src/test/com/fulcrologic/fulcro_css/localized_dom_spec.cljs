(ns com.fulcrologic.fulcro-css.localized-dom-spec
  (:require-macros com.fulcrologic.fulcro-css.localized-dom-spec)
  (:require
    [fulcro-spec.core :refer [specification assertions behavior provided when-mocking]]
    [com.fulcrologic.fulcro.dom :as adom]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro-css.localized-dom :as ldom :refer [div p span]]
    [cljsjs.react.dom.server]
    [clojure.string :as str]))

(defsc NoPropsComponent [this props] (ldom/div :.a#y "Hello"))
(defsc NilPropsComponent [this props] (ldom/div :.a#y nil "Hello"))
(defsc EmptyPropsComponent [this props] (ldom/div :.a#y {} "Hello"))
(defsc EmptyJSPropsComponent [this props] (ldom/div :#y.a #js {} "Hello"))
(defsc CLJPropsComponent [this props] (ldom/div :.a#y {:className "x"} "Hello"))
(defsc CLJPropsWithIDComponent [this props] (ldom/div :.a#y {:id 1 :className "x"} "Hello"))
(defsc JSPropsWithIDComponent [this props] (ldom/div :.a#y #js {:id 1 :className "x"} "Hello"))
(defsc SymbolicClassPropComponent [this props] (let [x "x"] (ldom/div :.a#y #js {:id 1 :className x} "Hello")))
(defsc ExtendedCSSComponent [this props] (ldom/div :.a$b#y {:className "x"} "Hello"))
(defsc NoKWComponent [this props] (ldom/div #js {:id "y" :className "x"} "Hello"))
(defsc NoKWCLJComponent [this props] (ldom/div {:id "y" :className "x"} "Hello"))
(defsc DynamicClassesComponent [this props] (ldom/div {:id "y" :classes [:.a :$b]} "Hello"))
(defsc DynamicSymClassesComponent [this props] (let [classes [:.a :$b]] (ldom/div {:id "y" :classes classes} "Hello")))
(defsc DynamicSymPropsComponent [this props] (let [props {:id "y" :classes [:.a :$b]}] (ldom/div props "Hello")))
(defsc DynamicSymPropsWithNilEntryComponent [this props] (let [props {:id "y" :classes [:$b nil]}] (ldom/div props "Hello")))
(defsc DynamicSymPropsWithKWComponent [this props] (let [props {:id "y" :classes [:.a :$b]}] (ldom/div :$x.z props "Hello")))
(defsc SymbolicClassPropsComponent [this props] (let [props {:className "x"}] (ldom/div :.a#y props "Hello")))
(defsc SymbolicClassJSPropsComponent [this props] (let [props #js {:className "x"}] (ldom/div :.a#y props "Hello")))
(defsc SymbolicClassNilPropsComponent [this props] (let [props nil] (ldom/div :.a#y props "Hello")))

;; NOTE: There are some pathological cases that I'm just not bothering to support. E.g. a #js {:fulcro-css.css/classes #js [:.a]} as props

(specification "Contextual rendering with localized CSS"
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed a style kw and no props:" NoPropsComponent "fulcro_client_localized-dom-spec_NoPropsComponent__a")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed a style kw and nil props:" NilPropsComponent "fulcro_client_localized-dom-spec_NilPropsComponent__a")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed a style kw and empty cljs props:" EmptyPropsComponent "fulcro_client_localized-dom-spec_EmptyPropsComponent__a")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed a style kw and empty js props:" EmptyJSPropsComponent "fulcro_client_localized-dom-spec_EmptyJSPropsComponent__a")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed a style kw and cljs props with class:" CLJPropsComponent "fulcro_client_localized-dom-spec_CLJPropsComponent__a x")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed a style kw and cljs props with class and ID:" CLJPropsWithIDComponent "fulcro_client_localized-dom-spec_CLJPropsWithIDComponent__a x")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed a style kw and js props with class and ID:" JSPropsWithIDComponent "fulcro_client_localized-dom-spec_JSPropsWithIDComponent__a x")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed a style kw and cljs props with symbolic class and ID:" SymbolicClassPropComponent "fulcro_client_localized-dom-spec_SymbolicClassPropComponent__a x")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed a style kw and cljs binding for props" SymbolicClassPropsComponent "fulcro_client_localized-dom-spec_SymbolicClassPropsComponent__a x")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed a style kw and js binding for props" SymbolicClassJSPropsComponent "fulcro_client_localized-dom-spec_SymbolicClassJSPropsComponent__a x")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed a style kw and a nil binding for props" SymbolicClassNilPropsComponent "fulcro_client_localized-dom-spec_SymbolicClassNilPropsComponent__a")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed a style kw with global marker:" ExtendedCSSComponent "fulcro_client_localized-dom-spec_ExtendedCSSComponent__a b x")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed js props with class and ID:" NoKWComponent "x")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed cljs props with class and ID:" NoKWCLJComponent "x")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed props with css/classes:" DynamicClassesComponent "fulcro_client_localized-dom-spec_DynamicClassesComponent__a b")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed props with symbolic css/classes:" DynamicSymClassesComponent "fulcro_client_localized-dom-spec_DynamicSymClassesComponent__a b")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed symbolic props that have css/classes:" DynamicSymPropsComponent "fulcro_client_localized-dom-spec_DynamicSymPropsComponent__a b")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed symbolic props that have css/classes:" DynamicSymPropsWithNilEntryComponent "b ")
  (com.fulcrologic.fulcro-css.localized-dom-spec/check-kw-processing "It is passed symbolic props with css/classes and kw:" DynamicSymPropsWithKWComponent "fulcro_client_localized-dom-spec_DynamicSymPropsWithKWComponent__z x fulcro_client_localized-dom-spec_DynamicSymPropsWithKWComponent__a b"))
