# Kotlin Fundamentals

The Kotlin Fundamentals course using [Slidev](https://sli.dev) with [`slidev-theme-kotlin`](https://www.npmjs.com/package/slidev-theme-kotlin).

## Run locally

Requires Node.js 18 or newer.

```bash
npm install
npm run dev
```

## Compile-check Kotlin snippets

`slidev-kotlin-snippets` (shipped by `slidev-theme-kotlin`) generates one
Kotlin file per Kotlin fence in `lessons/*.md` under
`src/main/kotlin/presentation/snippets/`, with the fence's own imports plus
`presentation.support.*`, the hand-written package holding the tables and
types the slides use without defining them. The generated files are committed
and compile against the same Kotlin 2.4 / Exposed 1.5.0 API the deck
advertises.

After changing a Kotlin fence:

```bash
npm run snippets       # regenerates src/main/kotlin/presentation/snippets/
./gradlew build        # compiles the snippets
```

`snippetsCheck` fails when the generated
sources no longer match the slides. Mark a fence that is not meant to compile
(library internals, an intermediate step) with `no-compile`, and add whatever a
new snippet needs from its surroundings to `src/main/kotlin/presentation/support/`.

## Build and export

```bash
npm run build
npm run export
```
