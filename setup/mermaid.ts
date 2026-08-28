import type { MermaidConfig } from 'mermaid'
import { defineMermaidSetup } from '@slidev/types'

/**
 * A small, presentation-friendly Mermaid palette.
 *
 * Keep this in the deck setup rather than on individual fences so every
 * Mermaid diagram shares the same visual language as the Kotlin theme.
 */
const lightThemeVariables = {
  background: '#ffffff',
  primaryColor: '#f1eeff',
  primaryTextColor: '#1f2023',
  primaryBorderColor: '#7954f6',
  secondaryColor: '#fff0fd',
  secondaryTextColor: '#1f2023',
  secondaryBorderColor: '#a769f7',
  tertiaryColor: '#fbfaff',
  tertiaryTextColor: '#1f2023',
  tertiaryBorderColor: '#d74ae5',
  lineColor: '#7954f6',
  arrowheadColor: '#7954f6',
  textColor: '#1f2023',
  mainBkg: '#f1eeff',
  nodeBkg: '#f1eeff',
  nodeBorder: '#7954f6',
  clusterBkg: '#fbfaff',
  clusterBorder: '#a769f7',
  edgeLabelBackground: '#ffffff',
  labelBackground: '#ffffff',
  fontFamily: 'JetBrains Sans, ui-sans-serif, system-ui, sans-serif',
  fontSize: '18px',
  fontWeight: 700,
  strokeWidth: 3.5,
  rowOdd: '#f1eeff',
  rowEven: '#ffffff',
}

const darkThemeVariables = {
  background: '#1f1d27',
  primaryColor: '#302942',
  primaryTextColor: '#f4f2f7',
  primaryBorderColor: '#a769f7',
  secondaryColor: '#39243b',
  secondaryTextColor: '#f4f2f7',
  secondaryBorderColor: '#d75fe4',
  tertiaryColor: '#292631',
  tertiaryTextColor: '#f4f2f7',
  tertiaryBorderColor: '#a769f7',
  lineColor: '#a769f7',
  arrowheadColor: '#a769f7',
  textColor: '#f4f2f7',
  mainBkg: '#302942',
  nodeBkg: '#302942',
  nodeBorder: '#a769f7',
  clusterBkg: '#292631',
  clusterBorder: '#a769f7',
  edgeLabelBackground: '#292631',
  labelBackground: '#292631',
  fontFamily: 'JetBrains Sans, ui-sans-serif, system-ui, sans-serif',
  fontSize: '18px',
  fontWeight: 700,
  strokeWidth: 3.5,
  rowOdd: '#302942',
  rowEven: '#292631',
}

export default defineMermaidSetup((): Partial<MermaidConfig> => {
  const isDark = typeof document !== 'undefined'
    && document.documentElement.classList.contains('dark')

  return {
    theme: 'base',
    look: 'neo',
    er: {
      nodeSpacing: 90,
      rankSpacing: 120,
    },
    // Mermaid adds an inline max-width based on the diagram's intrinsic size.
    // The SVG lives in Slidev's shadow root, so this rule must be injected into
    // Mermaid's own stylesheet rather than added to the deck stylesheet.
    themeCSS: `
      svg {
        width: 100% !important;
        max-width: none !important;
      }

      /* Entity boxes: match the deck's solid purple, rounded treatment. */
      .node > rect.basic.label-container {
        fill: #7954f6 !important;
        stroke: #7954f6 !important;
        stroke-width: 0 !important;
        rx: 16px;
        ry: 16px;
      }
      .node > .label > rect {
        fill: transparent !important;
        stroke: none !important;
      }
      .nodeLabel,
      .nodeLabel p {
        color: #ffffff !important;
        font-family: JetBrains Sans, ui-sans-serif, system-ui, sans-serif !important;
        font-size: 18px !important;
        font-weight: 700 !important;
      }

      /* Relationship lines: a confident, arrow-like weight in purple. */
      .relationshipLine {
        fill: none !important;
        stroke: #7954f6 !important;
        stroke-width: 3.5px !important;
        stroke-linecap: round !important;
        stroke-linejoin: round !important;
      }
      .marker {
        fill: none !important;
        stroke: none !important;
      }
      .marker path {
        fill: none !important;
        stroke: #7954f6 !important;
        stroke-width: 1.6px !important;
        stroke-linecap: round !important;
        stroke-linejoin: round !important;
      }
      /* ER cardinality variants: one, one-or-more, and zero-or-more keep
         the same visual language while retaining their distinct geometry. */
      .marker.onlyOne path { stroke-width: 1.8px !important; }
      .marker.oneOrMore path { stroke-width: 1.8px !important; }
      .marker.zeroOrMore path { stroke-width: 1.8px !important; }
      .marker.zeroOrOne circle,
      .marker.zeroOrMore circle {
        /* Keep optionality visible without letting the circle overpower the
           relationship itself or collide with the rounded entity boxes. */
        r: 4px;
        fill: #ffffff !important;
        stroke: #7954f6 !important;
        stroke-width: 1.6px !important;
      }

      /* Relationship labels: large, unboxed purple annotations. */
      .edgeLabel,
      .edgeLabel .label,
      .edgeLabel p {
        color: #7954f6 !important;
        fill: #7954f6 !important;
        font-family: JetBrains Sans, ui-sans-serif, system-ui, sans-serif !important;
        font-size: 16px !important;
        font-weight: 650 !important;
      }
      .edgeLabel .labelBkg,
      .edgeLabel .label rect {
        background: transparent !important;
        fill: transparent !important;
        stroke: none !important;
      }
      .edgeLabel {
        /* Mermaid centers labels on the edge. Lift them into the whitespace
           above the relationship so both straight and diagonal edges remain
           legible at the slide's export scale. */
        translate: 0 -26px;
      }
    `,
    themeVariables: isDark ? darkThemeVariables : lightThemeVariables,
  }
})
