(ns com.fulcrologic.fulcro-css.css-spec
  (:require
    [fulcro-spec.core :refer [specification assertions behavior]]
    [com.fulcrologic.fulcro-css.css :as css]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    #?(:cljs [com.fulcrologic.fulcro.dom :as dom]
       :clj  [com.fulcrologic.fulcro.dom-server :as dom])
    [garden.selectors :as sel]))

(defsc ListItem [this _]
  {:css [[:.item {:font-weight "bold"}]]}
  (let [{:keys [item]} (css/get-classnames ListItem)]
    (dom/li #js {:className item} "listitem")))

(defsc ListComponent [this _]
  {:css         [[:.items-wrapper {:background-color "blue"}]]
   :css-include [ListItem]}
  (let [{:keys [items-wrapper]} (css/get-classnames ListComponent)]
    (dom/ul #js {:className items-wrapper} "list")))

(defsc Root [this _]
  {:css         [[:.container {:background-color "red"}]]
   :css-include [ListComponent]
   :css-global  [[:.text {:color "green"}]]}
  (dom/div nil "root"))

(defsc Child1 [_ _]
  {:css [[:.child1class {:color "red"}]]}
  (dom/div nil "test"))

(defsc Child2 [this _]
  {:css [[:.child2class {:color "blue"}]]}
  (dom/div nil "test"))

(defsc Parent [this _]
  {:css-include [Child1 Child2]}
  (dom/div nil "test"))

(defsc MyLabel [this _]
  {:css [[:.my-label {:color "green"}]]})

(defsc MyButton [this _]
  {:css         [[:.my-button {:color "black"}]]
   :css-include [MyLabel]}
  (dom/div nil "test"))

(defsc MyForm [this _]
  {:css         [[:.form {:background-color "white"}]]
   :css-include [MyButton]}
  (dom/div nil "test"))

(defsc MyNavigation [this _]
  {:css         [[:.nav {:width "100px"}]]
   :css-include [MyButton]}
  (dom/div nil "test"))

(defsc MyRoot [this _]
  {:css-include [MyForm MyNavigation]}
  (dom/div nil "test"))

(specification "Obtain CSS from classes"
  (behavior "can be obtained from"
    (assertions
      "a single component"
      (css/get-css ListItem) => '([:.com_fulcrologic_fulcro-css_css-spec_ListItem__item {:font-weight "bold"}])
      "a component with a child"
      (css/get-css ListComponent) => '([:.com_fulcrologic_fulcro-css_css-spec_ListComponent__items-wrapper {:background-color "blue"}]
                                       [:.com_fulcrologic_fulcro-css_css-spec_ListItem__item {:font-weight "bold"}])
      "a component with nested children"
      (css/get-css Root) => '([:.com_fulcrologic_fulcro-css_css-spec_Root__container {:background-color "red"}]
                              [:.text {:color "green"}]
                              [:.com_fulcrologic_fulcro-css_css-spec_ListComponent__items-wrapper {:background-color "blue"}]
                              [:.com_fulcrologic_fulcro-css_css-spec_ListItem__item {:font-weight "bold"}])
      "a component with multiple direct children"
      (css/get-css Parent) => '([:.com_fulcrologic_fulcro-css_css-spec_Child1__child1class {:color "red"}]
                                [:.com_fulcrologic_fulcro-css_css-spec_Child2__child2class {:color "blue"}])
      "a component with multiple direct children without duplicating rules"
      (css/get-css MyRoot) => '([:.com_fulcrologic_fulcro-css_css-spec_MyForm__form {:background-color "white"}]
                                [:.com_fulcrologic_fulcro-css_css-spec_MyNavigation__nav {:width "100px"}]
                                [:.com_fulcrologic_fulcro-css_css-spec_MyButton__my-button {:color "black"}]
                                [:.com_fulcrologic_fulcro-css_css-spec_MyLabel__my-label {:color "green"}]))))

(specification "Generate classnames from CSS"
  (assertions
    "global classnames are untouched"
    (:text (css/get-classnames Root)) => "text"
    "local classnames are transformed"
    (:container (css/get-classnames Root)) => "com_fulcrologic_fulcro-css_css-spec_Root__container"
    "does not generate children-classnames"
    (:items-wrapper (css/get-classnames Root)) => nil))

(defsc A [this _]
  {:css [[(sel/> :.a :.b :.c) {:color "blue"}]]})

(defsc B [_ _]
  {:css [[(sel/> :$a :.b :span :$c) {:color "red"}]]})

(defsc C [_ _]
  {:css [[(sel/+ :.a :$b) {:color "green"}]]})

(defsc D [_ _]
  {:css [[(sel/- :.a :.b) {:color "yellow"}]]})

(defsc E [_ _]
  {:css [[(sel/+ :.a (sel/> :$b :span)) {:color "brown"}]]})

(defsc F [_ _]
  {:css        [[(sel/+ :.a (sel/> :$b :span)) {:color "brown"}]]
   :css-global [[(sel/> :.c :.d) {:color "blue"}]]})

(defn- first-css-selector [css-rules]
  (garden.selectors/css-selector (ffirst css-rules)))

(specification "CSS Combinators"
  (assertions
    "Child selector"
    (first-css-selector (css/get-css A)) => ".com_fulcrologic_fulcro-css_css-spec_A__a > .com_fulcrologic_fulcro-css_css-spec_A__b > .com_fulcrologic_fulcro-css_css-spec_A__c"
    "Child selector with localization prevention"
    (first-css-selector (css/get-css B)) => ".a > .com_fulcrologic_fulcro-css_css-spec_B__b > span > .c"
    "Adjacent sibling selector"
    (first-css-selector (css/get-css C)) => ".com_fulcrologic_fulcro-css_css-spec_C__a + .b"
    "General sibling selector"
    (first-css-selector (css/get-css D)) => ".com_fulcrologic_fulcro-css_css-spec_D__a ~ .com_fulcrologic_fulcro-css_css-spec_D__b"
    "Multiple different selectors"
    (first-css-selector (css/get-css E)) => ".com_fulcrologic_fulcro-css_css-spec_E__a + .b > span"
    "Get classnames"
    (css/get-classnames F) => {:a "com_fulcrologic_fulcro-css_css-spec_F__a"
                               :b "b"
                               :c "c"
                               :d "d"}))

(defsc G [_ _]
  {:css        [[:.a {:color "orange"}
                 [:&.b {:font-weight "bold"}]
                 [:&$c {:background-color "black"}]]]
   :css-global [[:.d {:color "green"}
                 [:&.e {:color "gray"}]]]})

(specification "Special &-selector"
  (assertions
    "Get CSS rules"
    (css/get-css G) => '([:.com_fulcrologic_fulcro-css_css-spec_G__a {:color "orange"}
                          [:&.com_fulcrologic_fulcro-css_css-spec_G__b {:font-weight "bold"}]
                          [:&.c {:background-color "black"}]]
                         [:.d {:color "green"}
                          [:&.e {:color "gray"}]])
    "Get classnames"
    (css/get-classnames G) => {:a "com_fulcrologic_fulcro-css_css-spec_G__a"
                               :b "com_fulcrologic_fulcro-css_css-spec_G__b"
                               :c "c"
                               :d "d"
                               :e "e"}))

