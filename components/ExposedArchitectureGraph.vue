<template>
  <div
    class="architecture-graph"
    role="img"
    aria-label="Exposed starts with tables, offers SQL DSL or DAO access, and connects to a database through JDBC or R2DBC."
  >
    <svg class="architecture-graph__wires" viewBox="0 0 1200 460" aria-hidden="true">
      <defs>
        <linearGradient id="architecture-wire" x1="0" x2="1" y1="0" y2="1">
          <stop offset="0" stop-color="#7954f6" />
          <stop offset="1" stop-color="#eb55e6" />
        </linearGradient>
      </defs>
      <path class="graph-line" d="M600 104 C600 125 318 130 318 165" />
      <path class="graph-line" d="M600 104 C600 125 882 130 882 165" />
      <path class="graph-line" d="M318 275 C318 294 486 304 600 342" />
      <path class="graph-line" d="M882 275 C882 294 714 304 600 342" />
      <circle class="graph-dot" cx="600" cy="104" r="6" />
      <circle class="graph-dot" cx="600" cy="342" r="6" />
    </svg>

    <div class="graph-card graph-card--foundation">
      <span>Table</span>
      <i>/</i>
      <span>IdTable</span>
    </div>

    <div class="graph-card graph-card--api graph-card--dsl">SQL DSL</div>
    <div class="graph-card graph-card--api graph-card--dao">DAO API</div>

    <div class="graph-card graph-card--transport">
      <span>JDBC</span>
      <i>/</i>
      <span>R2DBC</span>
      <b>→</b>
      <strong>database</strong>
    </div>
  </div>
</template>

<style scoped>
.architecture-graph {
  --graph-ink: var(--fundamentals-ink, #1f2023);
  --graph-purple: var(--fundamentals-purple, #7954f6);
  --graph-pink: var(--fundamentals-pink, #eb55e6);
  --graph-surface: rgb(255 255 255 / 90%);
  position: relative;
  width: min(100%, 70rem);
  height: 23rem;
  margin: 0.65rem auto 1.1rem;
  isolation: isolate;
}

.architecture-graph__wires {
  position: absolute;
  z-index: -1;
  inset: 0;
  width: 100%;
  height: 100%;
  overflow: visible;
}

.graph-line {
  fill: none;
  stroke: url('#architecture-wire');
  stroke-width: 3;
  stroke-linecap: round;
  opacity: 0.8;
}

.graph-dot {
  fill: var(--graph-pink);
  stroke: var(--graph-surface);
  stroke-width: 3;
}

.graph-card {
  position: absolute;
  display: flex;
  box-sizing: border-box;
  align-items: center;
  justify-content: center;
  border: 1px solid rgb(121 84 246 / 42%);
  border-radius: 1rem;
  background: var(--graph-surface);
  box-shadow: 0 0.55rem 1.45rem rgb(31 32 35 / 9%);
  color: var(--graph-ink);
  font-size: 1.65rem;
  font-weight: 800;
  letter-spacing: -0.035em;
}

.graph-card i, .graph-card b { color: var(--graph-pink); font-style: normal; }

.graph-card--foundation {
  top: 0;
  left: 31%;
  width: 38%;
  height: 5.2rem;
  gap: 0.55rem;
  background: linear-gradient(110deg, rgb(121 84 246 / 13%), rgb(235 85 230 / 10%));
}

.graph-card--api {
  top: 8.15rem;
  width: 32%;
  height: 6.8rem;
  color: var(--graph-purple);
}
.graph-card--dsl { left: 10.5%; }
.graph-card--dao {
  right: 10.5%;
  border-color: rgb(235 85 230 / 48%);
  background: linear-gradient(135deg, rgb(235 85 230 / 11%), var(--graph-surface));
  color: #b731ae;
}

.graph-card--transport {
  top: 17.1rem;
  left: 25%;
  width: 50%;
  height: 4.55rem;
  gap: 0.52rem;
  border-color: rgb(235 85 230 / 52%);
  background: linear-gradient(110deg, rgb(121 84 246 / 10%), rgb(235 85 230 / 12%));
  font-size: 1.35rem;
}
.graph-card--transport b { margin: 0 0.1rem; font-size: 1.7rem; }
.graph-card--transport strong { color: var(--graph-purple); }

html.dark .architecture-graph { --graph-surface: rgb(35 34 42 / 92%); }
html.dark .graph-card { box-shadow: 0 0.55rem 1.45rem rgb(0 0 0 / 24%); }

@media (max-width: 800px) {
  .architecture-graph { height: 21rem; }
  .graph-card { font-size: 1.25rem; }
  .graph-card--api { left: 4%; width: 40%; }
  .graph-card--dao { right: 4%; left: auto; }
  .graph-card--transport { left: 17%; width: 66%; font-size: 1.05rem; }
}
</style>
