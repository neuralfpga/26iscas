#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

echo "=== Building main.pdf ==="

TMPDIR="$(mktemp -d)"
trap 'rm -rf "$TMPDIR"' EXIT

gs -dBATCH -dNOPAUSE -q -sDEVICE=pdfwrite \
  -dFirstPage=1 -dLastPage=1 \
  -sOutputFile="$TMPDIR/first_slide.pdf" \
  main-handout.pdf

pdfjam --quiet --outfile "$TMPDIR/first_slide_norm.pdf" --papersize '{160mm,90mm}' -- "$TMPDIR/first_slide.pdf"
pdfjam --quiet --outfile "$TMPDIR/accompany_norm.pdf"   --papersize '{160mm,90mm}' -- accompany.pdf

pdfunite "$TMPDIR/first_slide_norm.pdf" "$TMPDIR/accompany_norm.pdf" main.pdf

echo "=== Done: main.pdf ==="
