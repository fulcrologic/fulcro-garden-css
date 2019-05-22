(ns com.fulcrologic.fulcro-css.css
  (:require [cljs.tagged-literals]
            [com.fulcrologic.fulcro-css.css-implementation :as ci]
            [com.fulcrologic.fulcro.components :as comp]
            [clojure.string :as str]
            #?(:cljs [cljsjs.react.dom])
            [clojure.walk :as walk]
            [garden.core :as g]
            [garden.selectors :as gs]))

(def cssify "Replaces slashes and dots with underscore." ci/cssify)
(def fq-component ci/fqname)
(def local-class "Generates a string name of a localized CSS class. This function combines the fully-qualified name of the given class
     with the (optional) specified name."
  ci/local-class)
(def set-classname ci/set-classname)
(def CSS? "`(CSS? class)` : Returns true if the given component has css." ci/CSS?)
(def get-local-rules "`(get-local-rules class)` : Get the *raw* value from the local-rules of a component." ci/get-local-rules)
(def get-includes "`(get-inculdes class)` :Returns the list of components from the include-children method of a component" ci/get-includes)
(def get-nested-includes "`(get-nested-includes class)` : Recursively finds all includes starting at the given component." ci/get-nested-includes)
(def get-classnames "`(get-classnames class)` : Returns a map from user-given CSS rule names to localized names of the given component." ci/get-classnames)

(defn localize-selector
  [selector comp]
  (let [val                 (:selector selector)
        split-cns-selectors (str/split val #" ")]
    (gs/selector (str/join " " (map #(if (ci/prefixed-name? %)
                                       (ci/localize-name % comp)
                                       %)
                                 split-cns-selectors)))))

(defn localize-css
  "Converts prefixed keywords into localized keywords and localizes the values of garden selectors"
  [component]
  (walk/postwalk (fn [ele]
                   (cond
                     (ci/prefixed-keyword? ele) (ci/localize-kw ele component)
                     (ci/selector? ele) (localize-selector ele component)
                     :otherwise ele)) (get-local-rules component)))

(defn get-css-rules
  "Gets the raw local and global rules from the given component."
  [component]
  (localize-css component))

(defn get-css
  "Recursively gets all global and localized rules (in garden notation) starting at the given component."
  [component]
  (let [own-rules             (get-css-rules component)
        nested-children       (distinct (get-nested-includes component))
        nested-children-rules (reduce #(into %1 (get-css-rules %2)) [] nested-children)]
    (concat own-rules nested-children-rules)))

(defn raw-css
  "Returns a string that contains the raw CSS for the rules defined on the given component's sub-tree. This can be used for
   server-side rendering of the style element, or in a `style` element as the :dangerouslySetInnerHTML/:html value:

   (dom/style #js {:dangerouslySetInnerHTML #js {:__html (raw-css component)}})
   "
  [component]
  (g/css (get-css component)))

