---
name: marp-skill
description: |
  Skill for understanding and authoring Marp (Markdown + Marpit) presentations.
  Provides syntax & semantics, links to authoritative docs, usage patterns, and
  a small checklist to validate slide decks before export.
scope: workspace
---

# Marp Skill

**Summary**
- Purpose: Help edit, validate, and generate Marp presentations written in Markdown.
- Scope: Workspace-scoped guidance and examples for `.md` slide decks using Marp.

**When to use this skill**
- Convert notes to Marp slides.
- Add Marp front-matter, themes, or custom CSS.
- Validate slide separators, background directives, and image paths.
- Produce export commands for PDF/HTML using Marp CLI.

**Quick Workflow (step-by-step)**
1. Ensure file front-matter contains `marp: true` and desired global options (theme, paginate, size).
2. Structure slides using horizontal separators (`---`) for new slides and `--` or `----` only when using alternate separators per theme.
3. Apply per-slide directives via comments or attributes (e.g., background, style, class) as supported by Marp/Marpit.
4. Preview locally with Marp preview extensions or render via Marp CLI to check exports.
5. Export to PDF/HTML and verify page sizes, margins, and image DPI.

**Decision points & branching**
- Export target: `PDF` → prefer CLI render (`marp --pdf` or `npx @marp-team/marp-cli`) for consistent pagination; `HTML` → export with `--html` or use GitHub Pages.
- Theme customization: Use built-in themes for quick work; add `style:` blocks or external CSS for brand consistency.
- Advanced layout: If you need CSS grid/flex layouts, include scoped `<style>` blocks but validate compatibility with Marp renderers.

**Quality checklist / Completion criteria**
- YAML front-matter exists and begins with `---` and `marp: true`.
- Slides separated by `---` where intended; no rogue separators inside code blocks.
- Images referenced with workspace-relative paths and present in repo.
- No unescaped HTML that could break renderers.
- Exports (PDF/HTML) render without missing assets and with expected slide count.

**Useful references**
- Marp official site / docs: https://marp.app/
- Marp docs (syntax, front-matter, directives): https://marp.app/docs/
- Marp CLI (rendering, export options): https://github.com/marp-team/marp-cli
- Marpit (underlying engine) and slide separators: https://marpit.marp.app/
- CommonMark spec (Markdown baseline): https://spec.commonmark.org/
- Example templates & themes: https://github.com/marp-team/marp-core

**Common snippets**
YAML front-matter example:

---
marp: true
theme: default
paginate: true
size: 16:9
---

Slide separator:

---

Per-slide background example:

---
<!-- _backgroundColor: #002b36 -->

Notes and speaker notes (use Marp supported patterns or plugin-specific syntax depending on renderer).

Export example (CLI):
- Using npx: `npx @marp-team/marp-cli main.md --pdf -o main.pdf`
- If `marp` CLI is installed globally: `marp main.md --pdf -o main.pdf`

**Suggested prompts to use this skill**
- "Create a 10-slide Marp deck about X with title slide, section breaks, and conclusion."
- "Add a dark theme and center the title on the first slide using Marp front-matter." 
- "Validate this Marp file for export to PDF and list issues." 
- "Convert these notes into Marp slides using `---` separators and include speaker notes."

**Converting from LaTeX**

- Use Pandoc to produce Markdown from LaTeX (works well for `article`/`beamer` sources). Example:

```
pandoc slides.tex -f beamer -t markdown -s -o slides.md --extract-media=images --wrap=none
```

- If your source is plain LaTeX (not Beamer), omit `-f beamer`. `--extract-media=images` writes embedded/linked images into `images/`.
- Pandoc preserves math as LaTeX (inline `$...$` and display `$$...$$`). Keep those delimiters so Marp can render via KaTeX.
- After conversion:
  - Add `marp: true` and `math: true` (or enable KaTeX in your Marp renderer/settings) to YAML front-matter so equations render.
  - Convert top-level `\\section`/`\\frame` titles into slide headings and insert `---` separators where logical slide breaks belong.
  - Check image paths (pandoc's extracted media path may need adjusting to workspace-relative URLs).
- Complex macros, custom commands, and table/layout code usually require manual cleanup. Consider using Pandoc filters or `--lua-filter` to expand macros where possible.
- Export tip: render and verify math/images with Marp CLI:

```
npx @marp-team/marp-cli slides.md --pdf -o slides.pdf --allow-local-files
```

- Quick checklist after conversion: math renders correctly, images load, slide count matches expected, and any custom macros are replaced or removed.

**Ambiguities / Questions**
- Should this skill be available workspace-wide or as a personal skill in your VS Code profile?
- Do you want sample theme files or a small template added to the repo alongside this skill?

**Next actions**
- I can add a `templates/` folder with a ready-to-use `slides-template.md` and a basic CSS theme.
- I can wire up example `tasks.json` or simple CLI commands in the repo's README for quick exports.

---

Generated by the Marp skill generator. Update the `references` links or templates as needed.
