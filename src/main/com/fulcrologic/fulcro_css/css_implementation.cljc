(ns com.fulcrologic.fulcro-css.css-implementation
  "Implementation details for co-located CSS. Do not use these directly."
  ;; IMPORTANT: DO NOT INCLUDE GARDEN HERE!!!!
  (:require
    [cljs.tagged-literals]
    [garden.selectors :as gs]
    [com.fulcrologic.fulcro.components :as comp]
    [clojure.string :as str])
  #?(:clj
     (:import [garden.selectors CSSSelector])))

;; from core
(defn cssify
  "Replaces slashes and dots with underscore."
  [str] (when str (str/replace str #"[./]" "_")))

(defn fqname [comp-class] (-> comp-class comp/class->registry-key str (str/replace #"^:" "")))

(defn local-class
  "Generates a string name of a localized CSS class. This function combines the fully-qualified name of the given class
     with the (optional) specified name."
  ([comp-class]
   (str (cssify (fqname comp-class))))
  ([comp-class nm]
   (str (cssify (fqname comp-class)) "__" (name nm))))

(defn set-classname
  [m subclasses]
  #?(:clj  (-> m
             (assoc :className subclasses)
             (dissoc :class))
     :cljs (cljs.core/clj->js (-> m
                                (assoc :className subclasses)
                                (dissoc :class)))))

(defn CSS?
  "Returns true if the given component has css"
  [x]
  (boolean (some-> x comp/component-options :css)))

(defn get-local-rules
  "Get the *raw* value from the local-rules of a component."
  [component]
  (if-let [entry (some-> component comp/component-options :css)]
    (cond
      (fn? entry) (entry)
      (vector? entry) entry
      :otherwise (do
                   (println "Invalid :css on " (comp/component-name component))
                   entry))
    []))

(defn prefixed-name?
  "Returns true if the given string starts with one of [. $ &$ &.]"
  [nm]
  (some? (re-matches #"(\.|\$|&\.|&\$).*" nm)))

(defn get-prefix
  "Returns the prefix of a string. [. $ &$ &.]"
  [nm]
  (let [[_ prefix] (re-matches #"(\.|\$|&\.|&\$).*" nm)]
    prefix))

(defn prefixed-keyword?
  "Returns true if the given keyword starts with one of [. $ &$ &.]"
  [kw]
  (and (keyword? kw)
    (prefixed-name? (name kw))))

(defn remove-prefix
  "Removes the prefix of a string."
  [nm]
  (subs nm (count (get-prefix nm))))

(defn remove-prefix-kw
  "Removes the prefix of a keyword."
  [kw]
  (keyword (remove-prefix (name kw))))

(defn get-includes
  "Returns the list of components from the include-children method of a component"
  [component]
  (or (some-> component comp/component-options :css-include) []))

(defn get-nested-includes
  "Recursively finds all includes starting at the given component."
  [component]
  (let [direct-children (get-includes component)]
    (if (empty? direct-children)
      []
      (concat direct-children (reduce #(concat %1 (get-nested-includes %2)) [] direct-children)))))

(defn localize-name
  [nm comp]
  (let [no-prefix (remove-prefix nm)
        prefix    (get-prefix nm)]
    (case prefix
      ("." "&.") (str prefix (local-class comp (keyword no-prefix)))
      "$" (str "." no-prefix)
      "&$" (str "&." no-prefix))))

(defn localize-kw
  [kw comp]
  (keyword (localize-name (name kw) comp)))

(defn kw->localized-classname
  "Gives the localized classname for the given keyword."
  [comp kw]
  (let [nm        (name kw)
        prefix    (get-prefix nm)
        no-prefix (subs nm (count prefix))]
    (case prefix
      ("$" "&$") no-prefix
      ("." "&.") (local-class comp no-prefix))))

(defn selector?
  [x]
  (try
    #?(:clj  (= garden.selectors.CSSSelector (type x))
       :cljs (= js/garden.selectors.CSSSelector (type x)))
    (catch #?(:cljs :default :clj Throwable) e
      false)))

(defn get-selector-keywords
  "Gets all the keywords that are present in a selector"
  [selector]
  (let [val        (gs/css-selector selector)
        classnames (filter #(re-matches #"[.$].*" %) (str/split val #" "))]
    (map keyword classnames)))

(defn get-class-keys
  "Gets all used classnames in from the given rules as keywords"
  [rules]
  (let [flattened-rules (flatten rules)
        selectors       (filter selector? flattened-rules)
        prefixed-kws    (filter prefixed-keyword? flattened-rules)]
    (distinct (concat (flatten (map get-selector-keywords selectors)) prefixed-kws))))

(defn get-classnames
  "Returns a map from user-given CSS rule names to localized names of the given component."
  [comp]
  (let [local-class-keys (get-class-keys (get-local-rules comp))
        local-classnames (zipmap (map remove-prefix-kw local-class-keys) (map #(kw->localized-classname comp %) local-class-keys))]
    local-classnames))
