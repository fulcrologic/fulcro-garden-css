(ns com.fulcrologic.fulcro-css.localized-dom
  (:refer-clojure :exclude [map meta time use set symbol filter])
  (:require
    [com.fulcrologic.fulcro.dom :as adom]
    com.fulcrologic.fulcro.dom-common
    com.fulcrologic.fulcro-css.localized-dom-common
    [com.fulcrologic.fulcro.algorithms.do-not-use :as util]
    [com.fulcrologic.fulcro.components :as comp])
  (:import (cljs.tagged_literals JSValue)))

(defn emit-tag [str-tag-name args]
  (let [conformed-args (util/conform! ::adom/dom-macro-args args)
        {attrs    :attrs
         children :children
         css      :css} conformed-args
        css-props      (if css `(com.fulcrologic.fulcro-css.localized-dom-common/add-kwprops-to-props nil ~css) nil)
        children       (mapv (fn [[_ c]]
                               (if (or (nil? c) (string? c))
                                 c
                                 `(comp/force-children ~c))) children)
        attrs-type     (or (first attrs) :nil)              ; attrs omitted == nil
        attrs-value    (or (second attrs) {})
        create-element (case str-tag-name
                         "input" 'com.fulcrologic.fulcro.dom/macro-create-wrapped-form-element
                         "textarea" 'com.fulcrologic.fulcro.dom/macro-create-wrapped-form-element
                         "select" 'com.fulcrologic.fulcro.dom/macro-create-wrapped-form-element
                         "option" 'com.fulcrologic.fulcro.dom/macro-create-wrapped-form-element
                         'com.fulcrologic.fulcro.dom/macro-create-element*)]
    (case attrs-type
      :js-object
      (let [attr-expr `(com.fulcrologic.fulcro-css.localized-dom-common/add-kwprops-to-props ~attrs-value ~css)]
        `(~create-element
           ~(JSValue. (into [str-tag-name attr-expr] children))))

      :map
      (let [attr-expr (if (or css (contains? attrs-value :classes))
                        `(com.fulcrologic.fulcro-css.localized-dom-common/add-kwprops-to-props ~(adom/clj-map->js-object attrs-value) ~css)
                        (adom/clj-map->js-object attrs-value))]
        `(~create-element ~(JSValue. (into [str-tag-name attr-expr] children))))

      :runtime-map
      (let [attr-expr `(com.fulcrologic.fulcro-css.localized-dom-common/add-kwprops-to-props ~(adom/clj-map->js-object attrs-value) ~css)]
        `(~create-element
           ~(JSValue. (into [str-tag-name attr-expr] children))))

      (:symbol :expression)
      `(com.fulcrologic.fulcro-css.localized-dom/macro-create-element
         ~str-tag-name ~(into [attrs-value] children) ~css)

      ;; also used for MISSING props
      :nil
      `(~create-element
         ~(JSValue. (into [str-tag-name css-props] children)))

      ;; pure children
      `(com.fulcrologic.fulcro-css.localized-dom/macro-create-element
         ~str-tag-name ~(JSValue. (into [attrs-value] children)) ~css))))

(adom/gen-dom-macros com.fulcrologic.fulcro-css.localized-dom/emit-tag)
