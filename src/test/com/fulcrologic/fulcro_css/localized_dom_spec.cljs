(ns com.fulcrologic.fulcro-css.localized-dom-spec
  (:require-macros [com.fulcrologic.fulcro-css.localized-dom-spec :refer [check-kw-processing]])
  (:require
    [fulcro-spec.core :refer [specification assertions behavior provided when-mocking]]
    [com.fulcrologic.fulcro.dom :as adom]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.application :as app]
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

(comment
  (js/console.log (comp/registry-key NoKWComponent))
  (adom/render-to-str (binding [comp/*app* (app/fulcro-app)] ((comp/factory NoPropsComponent) {})))
  (adom/render-to-str ((comp/factory NoPropsComponment) {}))
  )

(specification "Contextual rendering with localized CSS"
  (check-kw-processing "It is passed a style kw and no props:" NoPropsComponent "com_fulcrologic_fulcro-css_localized-dom-spec_NoPropsComponent__a")
  (check-kw-processing "It is passed a style kw and nil props:" NilPropsComponent "com_fulcrologic_fulcro-css_localized-dom-spec_NilPropsComponent__a")
  (check-kw-processing "It is passed a style kw and empty cljs props:" EmptyPropsComponent "com_fulcrologic_fulcro-css_localized-dom-spec_EmptyPropsComponent__a")
  (check-kw-processing "It is passed a style kw and empty js props:" EmptyJSPropsComponent "com_fulcrologic_fulcro-css_localized-dom-spec_EmptyJSPropsComponent__a")
  (check-kw-processing "It is passed a style kw and cljs props with class:" CLJPropsComponent "com_fulcrologic_fulcro-css_localized-dom-spec_CLJPropsComponent__a x")
  (check-kw-processing "It is passed a style kw and cljs props with class and ID:" CLJPropsWithIDComponent "com_fulcrologic_fulcro-css_localized-dom-spec_CLJPropsWithIDComponent__a x")
  (check-kw-processing "It is passed a style kw and js props with class and ID:" JSPropsWithIDComponent "com_fulcrologic_fulcro-css_localized-dom-spec_JSPropsWithIDComponent__a x")
  (check-kw-processing "It is passed a style kw and cljs props with symbolic class and ID:" SymbolicClassPropComponent "com_fulcrologic_fulcro-css_localized-dom-spec_SymbolicClassPropComponent__a x")
  (check-kw-processing "It is passed a style kw and cljs binding for props" SymbolicClassPropsComponent "com_fulcrologic_fulcro-css_localized-dom-spec_SymbolicClassPropsComponent__a x")
  (check-kw-processing "It is passed a style kw and js binding for props" SymbolicClassJSPropsComponent "com_fulcrologic_fulcro-css_localized-dom-spec_SymbolicClassJSPropsComponent__a x")
  (check-kw-processing "It is passed a style kw and a nil binding for props" SymbolicClassNilPropsComponent "com_fulcrologic_fulcro-css_localized-dom-spec_SymbolicClassNilPropsComponent__a")
  (check-kw-processing "It is passed a style kw with global marker:" ExtendedCSSComponent "com_fulcrologic_fulcro-css_localized-dom-spec_ExtendedCSSComponent__a b x")
  (check-kw-processing "It is passed js props with class and ID:" NoKWComponent "x")
  (check-kw-processing "It is passed cljs props with class and ID:" NoKWCLJComponent "x")
  (check-kw-processing "It is passed props with css/classes:" DynamicClassesComponent "com_fulcrologic_fulcro-css_localized-dom-spec_DynamicClassesComponent__a b")
  (check-kw-processing "It is passed props with symbolic css/classes:" DynamicSymClassesComponent "com_fulcrologic_fulcro-css_localized-dom-spec_DynamicSymClassesComponent__a b")
  (check-kw-processing "It is passed symbolic props that have css/classes:" DynamicSymPropsComponent "com_fulcrologic_fulcro-css_localized-dom-spec_DynamicSymPropsComponent__a b")
  (check-kw-processing "It is passed symbolic props that have css/classes:" DynamicSymPropsWithNilEntryComponent "b ")
  (check-kw-processing "It is passed symbolic props with css/classes and kw:" DynamicSymPropsWithKWComponent "com_fulcrologic_fulcro-css_localized-dom-spec_DynamicSymPropsWithKWComponent__z x com_fulcrologic_fulcro-css_localized-dom-spec_DynamicSymPropsWithKWComponent__a b"))
