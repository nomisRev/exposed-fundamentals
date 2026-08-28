import lz from 'lz-string'
import { toKeyedTokens } from '@shikijs/magic-move/core'

const SYNC_MAGIC_MOVE = /^md sync-magic-move\s*(?:\[([^\]]*)\])?$/
const CODE_BLOCK = /^```([\w'-]+)?[^\n]*\n([\s\S]*?)^```$/gm

/**
 * A pair of these blocks can use the same click range. Slidev's built-in
 * magic-move defaults each instance to the next available click range, which
 * makes adjacent code windows animate one after the other.
 */
export default function setupTransformers() {
  return {
    codeblocks: [async ({ info, fence, code, options }: any) => {
      if (fence !== 4)
        return

      const match = info.match(SYNC_MAGIC_MOVE)
      if (!match)
        return

      const blocks = Array.from(code.matchAll(CODE_BLOCK))
      if (!blocks.length)
        throw new Error('sync-magic-move requires at least one code block')

      // A single sync-magic-move can contain several code windows. Grouping
      // fences by language lets their corresponding steps advance together,
      // rather than treating (for example) Kotlin and SQL as four steps in
      // one code window.
      const blockGroups = new Map<string, string[]>()
      for (const [, language = 'text', source] of blocks) {
        const snippets = blockGroups.get(language) ?? []
        snippets.push(source.trimEnd())
        blockGroups.set(language, snippets)
      }

      const title = match[1] ?? ''
      return (await Promise.all([...blockGroups].map(async ([language, snippets]) => {
        const shikiOptions = { ...options.utils.shikiOptions, lang: language }
        const steps = await Promise.all(snippets.map(async (snippet) => {
          const { tokens, bg, fg, rootStyle, themeName } = await options.utils.shiki.codeToTokens(snippet, shikiOptions)
          return {
            ...toKeyedTokens(snippet, tokens, JSON.stringify([language, 'themes' in shikiOptions ? shikiOptions.themes : shikiOptions.theme]), options.data.config.lineNumbers),
            bg,
            fg,
            rootStyle,
            themeName,
            lang: language,
          }
        }))

        // The Kotlin theme styles SQL as PostgreSQL. Magic Move bypasses the
        // theme's normal Shiki transformer, so translate the fence language to
        // its code-window identity before passing the class to the component.
        const codeWindowIcon = language === 'sql' ? 'postgresql' : language
        const ranges = steps.map(() => [])
        return `<ShikiMagicMove class="code-window-icon--${codeWindowIcon}" :at="1" steps-lz="${lz.compressToBase64(JSON.stringify(steps))}" :title='${JSON.stringify(title)}' :step-ranges='${JSON.stringify(ranges)}' />`
      }))).join('\n')
    }],
  }
}
