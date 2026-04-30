#!/bin/bash
set -e

DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"

echo "=== Building main.pdf ==="
pdflatex -interaction=nonstopmode main.tex
biber main
pdflatex -interaction=nonstopmode main.tex
pdflatex -interaction=nonstopmode main.tex

echo "=== Building main-handout.pdf ==="
pdflatex -interaction=nonstopmode -jobname=main-handout \
  "\PassOptionsToClass{handout}{beamer}\input{main.tex}"
biber main-handout
pdflatex -interaction=nonstopmode -jobname=main-handout \
  "\PassOptionsToClass{handout}{beamer}\input{main.tex}"
pdflatex -interaction=nonstopmode -jobname=main-handout \
  "\PassOptionsToClass{handout}{beamer}\input{main.tex}"

echo "=== Done: main.pdf and main-handout.pdf ==="
