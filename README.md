# Kotlin Fundamentals

The Kotlin Fundamentals course using [Slidev](https://sli.dev) with [`slidev-theme-kotlin`](https://www.npmjs.com/package/slidev-theme-kotlin).

## Run locally

Requires Node.js 18 or newer.

```bash
npm install
npm run dev
```

## Compile-check Kotlin snippets

[KotlinX Knit](https://github.com/Kotlin/kotlinx-knit) generates Kotlin sources
from `presentation-snippets.md`. The generated files under
`src/main/kotlin/presentation/snippets/` are committed and compile against the
same Kotlin 2.4 / Exposed 1.4.0 API advertised by the deck.

After changing a checked Kotlin card:

```bash
npm run check:snippets # confirms the companion Knit snippet still matches the Slidev card
./gradlew knit         # regenerates src/main/kotlin/presentation/snippets/
./gradlew check        # verifies generated sources are current and compile
```

`./gradlew check` runs `knitCheck`; CI also runs the Slidev-source sync check.
Add each new Kotlin card to `presentation-snippets.md` with a `KNIT` directive
and any required hidden imports/context.

## Build and export

```bash
npm run build
npm run export
```
