<template>
  <div
    class="entity-boundary"
    role="img"
    aria-label="A Talk DAO entity is managed inside its transaction. Map it to a detached TalkPreview before returning it to the application."
  >
    <svg viewBox="0 0 1200 230" preserveAspectRatio="xMidYMid meet" aria-hidden="true">
      <defs>
        <marker id="dto-arrow" markerWidth="9" markerHeight="9" refX="7" refY="4.5" orient="auto">
          <path d="M0 0 L9 4.5 L0 9z" fill="#7954f6" />
        </marker>
      </defs>

      <text class="section-label" x="42" y="28">TRANSACTION SCOPE</text>
      <text class="section-label app-label" x="895" y="28">APPLICATION</text>

      <g class="transaction">
        <rect x="42" y="48" width="640" height="140" rx="18" />
        <text class="transaction-title" x="72" y="80">transaction(database)</text>
        <path d="M72 98 H652" />

        <text class="muted" x="72" y="133">entity cache</text>
        <path class="cache-arrow" d="M208 127 H287" marker-end="url(#dto-arrow)" />
        <rect class="managed-entity" x="311" y="105" width="220" height="50" rx="11" />
        <text class="entity-name" x="336" y="136">Talk(id = 42)</text>
        <text class="managed" x="552" y="136">managed</text>
      </g>

      <path class="map-arrow" d="M710 119 H848" marker-end="url(#dto-arrow)" />
      <text class="map-label" x="758" y="100">map</text>

      <g class="dto">
        <rect x="875" y="82" width="280" height="76" rx="14" />
        <text class="dto-name" x="904" y="115">TalkPreview(...)</text>
        <text class="dto-detail" x="904" y="140">detached data</text>
      </g>

      <text class="caption" x="42" y="216">Managed entities stay inside the transaction; DTOs can cross the boundary.</text>
    </svg>
  </div>
</template>

<style scoped>
.entity-boundary {
  --ink: #292531;
  --muted: #746d80;
  width: min(100%, 73rem);
  margin: 0.85rem auto 0.4rem;
}
svg { display: block; width: 100%; height: auto; }
.section-label, .transaction-title, .muted, .managed, .map-label, .dto-detail, .caption {
  font-family: 'JetBrains Mono', ui-monospace, monospace;
}
.section-label { fill: var(--muted); font-size: 12px; font-weight: 800; letter-spacing: .12em; }
.app-label { fill: #4f4570; }
.transaction > rect { fill: rgb(121 84 246 / 5%); stroke: rgb(121 84 246 / 45%); stroke-width: 2; }
.transaction > path { stroke: rgb(121 84 246 / 22%); stroke-width: 1.5; }
.transaction-title { fill: var(--ink); font-size: 17px; font-weight: 750; }
.muted { fill: var(--muted); font-size: 14px; }
.cache-arrow, .map-arrow { fill: none; stroke: #7954f6 !important; stroke-width: 2.5 !important; stroke-linecap: round; }
.managed-entity { fill: #7954f6; }
.entity-name { fill: white; font: 700 16px 'JetBrains Mono', ui-monospace, monospace; }
.managed { fill: #ded5ff; font-size: 11px; font-weight: 700; }
.map-label { fill: #7954f6; font-size: 12px; font-weight: 800; }
.dto > rect { fill: #fff; stroke: rgb(121 84 246 / 48%); stroke-width: 2; }
.dto-name { fill: #493493; font: 750 20px 'JetBrains Sans', Arial, sans-serif; }
.dto-detail { fill: var(--muted); font-size: 13px; }
.caption { fill: var(--muted); font-size: 12px; }
html.dark .entity-boundary { --ink: #f2edf8; --muted: #b8afc5; }
html.dark .transaction > rect { fill: rgb(121 84 246 / 13%); }
html.dark .dto > rect { fill: #2b2733; }
html.dark .dto-name { fill: #c9baff; }
</style>
