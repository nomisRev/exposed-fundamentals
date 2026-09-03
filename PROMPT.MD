Fix: fold shown.prefix back in

> - Heading levels in llms-full.txt and the handout invert the document outline. A slide's own ## Subtitle keeps its level while slide titles are written as ## (single section) or ### (multi section). In a multi-section deck a slide's sub-heading therefore outranks the slide it belongs to, and the section heading is immediately repeated as the first slide's title (## Deck then ### Deck). The handout HTML has the same shape: 63 <h2> for 48 slides, and any second # in a slide would produce a competing <h1>. For an LLM reading the full text this is the most damaging structural issue. Demote content headings by the slide-title depth in cleanSlideMarkdown or when rendering.

Demote in cleanSlideMarkdown

Run the flag-stripping edit unconditionally

> bin/kotlin-snippets.mjs depends on packages the theme does not declare. It imports jiti and @slidev/parser/fs, and setup/magic-move-between.ts imports @slidev/parser. Neither is in dependencies; @slidev/parser is a devDependency only and jiti arrives transitively through @slidev/cli. With npm hoisting this happens to work, with pnpm or Yarn PnP npx slidev-kotlin-snippets fails to resolve. Declare both as dependencies or peerDependencies
 
Declare and pin versions for dependencies 

> robots.txt is inert on GitHub project pages. It lands at /slidev-theme-kotlin/robots.txt, but crawlers only read robots at the host root. The sitemap is still discoverable through the rel="sitemap" link and manual submission, so nothing breaks. The README and CHANGELOG should say the file only takes effect when the deck is served from the root of its host.

So we need to explicitly reference is on the base-url (which is my blog)?

> Duplicate <meta name="description"> once info is set. Slidev already emits a description from the raw info value, and indexHtmlTags adds a second one from deckDescription. Google reads the first, which is Slidev's unclean Markdown. Either skip the theme's tag when Slidev's exists or overwrite Slidev's.

Fix no strong preference

> The showcase deck has no description at all. slides.md sets siteUrl but no info, so the built root page has no description and the handout falls back to "Kotlin Slidev Theme — Kotlin Slidev Theme" (the section title equals the deck title in a single-section deck). Add info to the showcase, and use a neutral fallback such as the existing lead text instead of the section-title composition.

This is responsibility of downstream themes i.e. ~/Developer/presenstations/exposed-fundamentals

> <noscript> is a weak discovery vector for text extractors. Google parses it, but readability-style extractors used by many AI crawlers drop <noscript> entirely. A plain <div> before #app with the same links, hidden by CSS once the app has mounted, would reach every crawler. Keep the noscript too if you like.

Do whatever works best for everything

> The handout carries no Open Graph tags or structured data, unlike the root page. og:title, og:description, and og:type="article" are cheap wins for sharing and for crawlers that rely on them.

Implement / fix

>  lang="en" is hard-coded in the handout shell while Slidev honours headmatter.lang. Thread that through.

Fix although it will practically always be english


