---
theme: kotlin
favicon: /Exposed icon.svg
transition: fade
layout: cover
class: fundamentals-cover
canvasWidth: 1440
aspectRatio: 16/9
highlighter: shiki
themeConfig:
  kodee: greeting
  siteUrl: https://nomisrev.github.io/exposed-fundamentals/
  snippets:
    dir: src/main/kotlin/presentation/snippets
    package: presentation.snippets
    imports:
      - presentation.support.*
kodee: welcome
---

# Exposed Fundamentals

## From relational model to expressive Kotlin queries

Exposed 1.5.0 · Kotlin 2.4.20 · PostgreSQL 18

---
layout: intro
class: section-slide agenda-slide
kodee: wave
---

# Today’s route

1. What is Exposed?
2. SQL DSL
3. Advanced queries
4. Migration & database generated values


---
src: ./lessons/lesson-1.md
---
---
src: ./lessons/lesson-2.md
---
---
src: ./lessons/lesson-4.md
---
---
src: ./lessons/lesson-6.md
---
---
src: ./lessons/closing.md
---

<!-- End of imported slides. -->
