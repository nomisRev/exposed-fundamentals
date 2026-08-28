<template>
  <div
    class="meetup-domain-graph"
    role="img"
    aria-label="Meetup platform relational model: Profiles speak at and host Talks, Talks have Tags through TalkTags, and Profiles save Talks through Bookmarks."
  >
    <svg viewBox="0 0 1200 390" preserveAspectRatio="xMidYMid meet" aria-hidden="true">
      <defs>
        <linearGradient id="meetup-graph-line" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0" stop-color="#7954f6" />
          <stop offset="1" stop-color="#d74ae5" />
        </linearGradient>
        <linearGradient id="meetup-graph-core" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0" stop-color="#7954f6" />
          <stop offset="1" stop-color="#9a4def" />
        </linearGradient>
        <linearGradient id="meetup-graph-join" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0" stop-color="#a94cf0" />
          <stop offset="1" stop-color="#d74ae5" />
        </linearGradient>
        <filter id="meetup-graph-shadow" x="-20%" y="-30%" width="140%" height="170%">
          <feDropShadow dx="0" dy="6" stdDeviation="7" flood-color="#7954f6" flood-opacity="0.14" />
        </filter>
      </defs>

      <text class="graph-kicker" x="36" y="28">RELATIONAL MODEL</text>
      <text class="graph-note" x="1164" y="28" text-anchor="end">tables · relationships · join tables</text>

      <g class="graph-wires">
        <path class="graph-wire" d="M540 92 C540 116 518 132 540 160" />
        <path class="graph-wire" d="M620 92 C620 116 642 132 620 160" />
        <path class="graph-wire graph-wire--save" d="M690 78 C810 58 1020 106 1030 170 C1040 220 1020 252 995 280" />
        <path class="graph-wire" d="M190 222 L190 280" />
        <path class="graph-wire" d="M500 222 C452 246 370 248 308 311" />
        <path class="graph-wire" d="M700 222 C760 246 830 248 890 311" />
      </g>

      <g class="graph-labels">
        <g class="graph-label" transform="translate(466 111)"><rect width="72" height="28" rx="14" /><text x="36" y="14">hosts</text></g>
        <g class="graph-label" transform="translate(644 111)"><rect width="78" height="28" rx="14" /><text x="39" y="14">speaks</text></g>
        <g class="graph-label graph-label--accent" transform="translate(806 66)"><rect width="70" height="28" rx="14" /><text x="35" y="14">saves</text></g>
        <g class="graph-label" transform="translate(151 238)"><rect width="78" height="28" rx="14" /><text x="39" y="14">labels</text></g>
        <g class="graph-label" transform="translate(386 238)"><rect width="54" height="28" rx="14" /><text x="27" y="14">has</text></g>
        <g class="graph-label graph-label--accent" transform="translate(754 238)"><rect width="88" height="28" rx="14" /><text x="44" y="14">is_saved</text></g>
      </g>

      <g class="graph-nodes" filter="url(#meetup-graph-shadow)">
        <g class="graph-node graph-node--core" transform="translate(460 30)">
          <rect width="240" height="62" rx="20" />
          <text class="graph-node__eyebrow" x="120" y="19">IDENTITY</text>
          <text class="graph-node__name" x="120" y="45">PROFILES</text>
        </g>
        <g class="graph-node graph-node--core" transform="translate(460 160)">
          <rect width="240" height="62" rx="20" />
          <text class="graph-node__eyebrow" x="120" y="19">CONTENT</text>
          <text class="graph-node__name" x="120" y="45">TALKS</text>
        </g>
        <g class="graph-node graph-node--soft" transform="translate(90 160)">
          <rect width="200" height="62" rx="20" />
          <text class="graph-node__eyebrow" x="100" y="19">CLASSIFICATION</text>
          <text class="graph-node__name" x="100" y="45">TAGS</text>
        </g>
        <g class="graph-node graph-node--join" transform="translate(90 280)">
          <rect width="220" height="62" rx="20" />
          <text class="graph-node__eyebrow" x="110" y="19">JOIN TABLE</text>
          <text class="graph-node__name" x="110" y="45">TALK_TAGS</text>
        </g>
        <g class="graph-node graph-node--join" transform="translate(880 280)">
          <rect width="230" height="62" rx="20" />
          <text class="graph-node__eyebrow" x="115" y="19">JOIN TABLE</text>
          <text class="graph-node__name" x="115" y="45">BOOKMARKS</text>
        </g>
      </g>

      <g class="graph-points" aria-hidden="true">
        <circle cx="540" cy="92" r="5" />
        <circle cx="620" cy="92" r="5" />
        <circle cx="190" cy="222" r="5" />
        <circle cx="308" cy="311" r="5" />
        <circle cx="890" cy="311" r="5" />
      </g>
    </svg>
  </div>
</template>

<style scoped>
.meetup-domain-graph {
  --graph-ink: #1f2023;
  --graph-muted: #716a7d;
  --graph-purple: #7954f6;
  --graph-pink: #d74ae5;
  width: 100%;
  margin: 0.15rem auto 0.5rem;
  padding: 0.35rem 0.65rem 0.2rem;
  box-sizing: border-box;
  border: 1px solid rgb(121 84 246 / 20%);
  border-radius: 1.1rem;
  background: linear-gradient(135deg, rgb(121 84 246 / 5%), rgb(235 85 230 / 5%));
}
svg { display: block; width: 100%; height: auto; overflow: visible; }
.graph-kicker, .graph-note {
  fill: var(--graph-muted);
  font: 700 14px 'JetBrains Mono', ui-monospace, monospace;
  letter-spacing: 0.1em;
}
.graph-note { font-size: 13px; font-weight: 600; letter-spacing: 0.02em; }
.graph-wire {
  fill: none;
  stroke: url('#meetup-graph-line');
  stroke-width: 5;
  stroke-linecap: round;
  stroke-linejoin: round;
  opacity: 0.9;
}
.graph-wire--save { stroke-dasharray: 11 9; opacity: 0.7; }
.graph-label rect {
  fill: #fff;
  stroke: rgb(121 84 246 / 32%);
  stroke-width: 1.5;
}
.graph-label text {
  fill: var(--graph-purple);
  font: 700 14px 'JetBrains Mono', ui-monospace, monospace;
  text-anchor: middle;
  dominant-baseline: central;
}
.graph-label--accent rect { stroke: rgb(215 74 229 / 42%); fill: #fff8ff; }
.graph-label--accent text { fill: #b33ab1; }
.graph-node rect { stroke-width: 2; }
.graph-node--core rect { fill: url('#meetup-graph-core'); stroke: #7954f6; }
.graph-node--soft rect { fill: #f1eeff; stroke: #9a80f8; }
.graph-node--join rect { fill: url('#meetup-graph-join'); stroke: #c74ddf; }
.graph-node__eyebrow {
  fill: rgb(255 255 255 / 72%);
  font: 700 11px 'JetBrains Mono', ui-monospace, monospace;
  letter-spacing: 0.1em;
  text-anchor: middle;
}
.graph-node--soft .graph-node__eyebrow { fill: #776f8b; }
.graph-node__name {
  fill: #fff;
  font: 750 25px 'JetBrains Sans', Arial, sans-serif;
  letter-spacing: 0.02em;
  text-anchor: middle;
}
.graph-node--soft .graph-node__name { fill: var(--graph-purple); }
.graph-points circle { fill: var(--graph-pink); stroke: #fff; stroke-width: 3; }

html.dark .meetup-domain-graph {
  --graph-ink: #f4f2f7;
  --graph-muted: #b7afc2;
  border-color: rgb(167 105 247 / 35%);
  background: linear-gradient(135deg, rgb(121 84 246 / 16%), rgb(235 85 230 / 11%));
}
html.dark .graph-label rect { fill: #292631; stroke: rgb(167 105 247 / 50%); }
html.dark .graph-label--accent rect { fill: #332434; }
html.dark .graph-node--soft rect { fill: #302942; stroke: #a769f7; }
html.dark .graph-node--soft .graph-node__eyebrow { fill: #c2b8d5; }
html.dark .graph-points circle { stroke: #292631; }
</style>
