# reagent-dnd

This is a fork of the (reagent-dnd)[https://github.com/johnswanson/reagent-dnd], a clojurescript reagent wrapper 
for react-dnd the main changes are:

- The cljsjs dependencies have been removed (I'm using shadow-cljs)
- Libraries have been updated (react/react-dnd/reagent/clojure/re-frame)
- A few small bug fixes
- Ported the Simple Sortable example from the react-dnd examples

Since the cljsjs dependencies have been removed consuming projects will need to provide them,
I use shadow-cljs for this but npm-deps, yarn, etc should all work as well, required dependencies are listed 
in package.json.

Not all of the examples have been reviewed / updated for the enw library versions, if there are layout issues or bugs it's probably my fault not the original authors.


## Development Mode

### Run application:

```
npm install
npm run watch
```

Shadow-CLJS will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:8700](http://localhost:8700).

## Production Build

```
???
```
