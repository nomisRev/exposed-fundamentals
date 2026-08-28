import type { ShikiSetupReturn } from '@slidev/types'
import { defineShikiSetup } from '@slidev/types'
import type { ThemeRegistration } from 'shiki'

/**
 * IntelliJ IDEA's default light editor palette.
 *
 * Keep the rules language-agnostic where possible: this presentation uses
 * Kotlin, XML, YAML, and Bash, and all of them should look like they belong to
 * the same IDE rather than falling back to Shiki's unstyled foreground.
 */
const intellijLight: ThemeRegistration = {
  name: 'intellij-light',
  type: 'light',
  colors: {
    'editor.background': '#ffffff',
    'editor.foreground': '#080808',
  },
  settings: [
    {
      settings: {
        background: '#ffffff',
        foreground: '#080808',
      },
    },
    {
      scope: ['comment', 'punctuation.definition.comment'],
      settings: { foreground: '#8c8c8c' },
    },
    {
      scope: ['comment.block.documentation', 'comment.block.javadoc'],
      settings: { foreground: '#8c8c8c', fontStyle: 'italic' },
    },
    {
      scope: ['keyword', 'storage.type', 'storage.modifier'],
      settings: { foreground: '#0033b3' },
    },
    {
      scope: ['constant.language', 'variable.language'],
      settings: { foreground: '#0033b3' },
    },
    {
      scope: ['string', 'string.quoted', 'string.unquoted'],
      settings: { foreground: '#067d17' },
    },
    {
      scope: [
        'constant.character.escape',
        'constant.character.string.escape',
        'variable.string-escape',
      ],
      settings: { foreground: '#0037a6' },
    },
    {
      scope: ['constant.numeric'],
      settings: { foreground: '#1750eb' },
    },
    {
      scope: [
        'entity.name.function',
        'support.function',
        'meta.function-call',
        'support.function.builtin.shell',
      ],
      settings: { foreground: '#00627a' },
    },
    {
      scope: [
        'entity.name.type',
        'entity.name.class',
        'support.type',
        'support.class',
      ],
      settings: { foreground: '#000000' },
    },
    {
      scope: [
        'entity.name.type.annotation',
        'storage.type.annotation',
        'meta.annotation',
      ],
      settings: { foreground: '#9e880d' },
    },
    {
      scope: [
        'variable.other',
        'variable.parameter',
        'entity.name.variable',
        'support.variable',
      ],
      settings: { foreground: '#871094' },
    },
    {
      scope: [
        'entity.name.tag',
        'punctuation.definition.tag',
        'punctuation.definition.tag.begin',
        'punctuation.definition.tag.end',
      ],
      settings: { foreground: '#0033b3' },
    },
    {
      scope: ['entity.other.attribute-name'],
      settings: { foreground: '#174ad4' },
    },
    {
      scope: [
        'entity.name.tag.yaml',
        'entity.name.type.anchor.yaml',
        'variable.other.alias.yaml',
        'variable.other.key',
      ],
      settings: { foreground: '#871094' },
    },
    {
      scope: ['invalid', 'invalid.illegal'],
      settings: { foreground: '#e00000' },
    },
  ],
}

/** IntelliJ IDEA's classic Darcula editor palette. */
const intellijDarcula: ThemeRegistration = {
  name: 'intellij-darcula',
  type: 'dark',
  colors: {
    'editor.background': '#2b2b2b',
    'editor.foreground': '#a9b7c6',
  },
  settings: [
    {
      settings: {
        background: '#2b2b2b',
        foreground: '#a9b7c6',
      },
    },
    {
      scope: ['comment', 'punctuation.definition.comment'],
      settings: { foreground: '#808080' },
    },
    {
      scope: ['comment.block.documentation', 'comment.block.javadoc'],
      settings: { foreground: '#629755', fontStyle: 'italic' },
    },
    {
      scope: ['keyword', 'storage.type', 'storage.modifier'],
      settings: { foreground: '#cc7832' },
    },
    {
      scope: ['constant.language', 'variable.language'],
      settings: { foreground: '#cc7832' },
    },
    {
      scope: ['string', 'string.quoted', 'string.unquoted'],
      settings: { foreground: '#6a8759' },
    },
    {
      scope: [
        'constant.character.escape',
        'constant.character.string.escape',
        'variable.string-escape',
      ],
      settings: { foreground: '#cc7832' },
    },
    {
      scope: ['constant.numeric'],
      settings: { foreground: '#6897bb' },
    },
    {
      scope: [
        'entity.name.function',
        'support.function',
        'meta.function-call',
        'support.function.builtin.shell',
      ],
      settings: { foreground: '#ffc66d' },
    },
    {
      scope: [
        'entity.name.type',
        'entity.name.class',
        'support.type',
        'support.class',
      ],
      settings: { foreground: '#a9b7c6' },
    },
    {
      scope: [
        'entity.name.type.annotation',
        'storage.type.annotation',
        'meta.annotation',
      ],
      settings: { foreground: '#bbb529' },
    },
    {
      scope: [
        'variable.other',
        'variable.parameter',
        'entity.name.variable',
        'support.variable',
      ],
      settings: { foreground: '#9876aa' },
    },
    {
      scope: [
        'entity.name.tag',
        'punctuation.definition.tag',
        'punctuation.definition.tag.begin',
        'punctuation.definition.tag.end',
      ],
      settings: { foreground: '#e8bf6a' },
    },
    {
      scope: ['entity.other.attribute-name'],
      settings: { foreground: '#bababa' },
    },
    {
      scope: [
        'entity.name.tag.yaml',
        'entity.name.type.anchor.yaml',
        'variable.other.alias.yaml',
        'variable.other.key',
      ],
      settings: { foreground: '#ffc66d' },
    },
    {
      scope: ['invalid', 'invalid.illegal'],
      settings: { foreground: '#ff6b68' },
    },
  ],
}

export default defineShikiSetup((): ShikiSetupReturn => ({
  themes: {
    light: intellijLight,
    dark: intellijDarcula,
  },
}))
