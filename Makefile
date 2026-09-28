tests:
	pnpm install --frozen-lockfile
	pnpm exec shadow-cljs compile ci-tests
	pnpm exec karma start --single-run
	clojure -A:provided:test:clj-tests
