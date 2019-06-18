tests:
	npm install
	npx shadow-cljs compile ci-tests
	npx karma start --single-run
	clojure -A:provided:test:clj-tests
