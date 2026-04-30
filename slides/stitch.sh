#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUTPUT="${SCRIPT_DIR}/combined.pdf"
OUTPUT_HANDOUT="${SCRIPT_DIR}/combined-handout.pdf"

mapfile -t PDFS < <(
  find "$SCRIPT_DIR" -maxdepth 2 -regex '.*/[0-9][^/]*/main\.pdf' | sort
)

if [[ ${#PDFS[@]} -eq 0 ]]; then
  echo "No numbered slide directories with main.pdf found." >&2
  exit 1
fi

echo "Stitching:"
printf '  %s\n' "${PDFS[@]}"

TMPDIR="$(mktemp -d)"
trap 'rm -rf "$TMPDIR"' EXIT

NORMALISED=()
NORMALISED_HANDOUT=()
for PDF in "${PDFS[@]}"; do
  NAME="$(basename "$(dirname "$PDF")")"
  DIR="$(dirname "$PDF")"
  OUT="$TMPDIR/${NAME}.pdf"
  pdfjam --quiet --outfile "$OUT" --papersize '{160mm,90mm}' -- "$PDF"
  NORMALISED+=("$OUT")

  HANDOUT_PDF="${DIR}/main-handout.pdf"
  OUT_HANDOUT="$TMPDIR/${NAME}-handout.pdf"
  pdfjam --quiet --outfile "$OUT_HANDOUT" --papersize '{160mm,90mm}' -- "$HANDOUT_PDF"
  NORMALISED_HANDOUT+=("$OUT_HANDOUT")
done

pdfunite "${NORMALISED[@]}" "$OUTPUT"
echo "Written to $OUTPUT"

pdfunite "${NORMALISED_HANDOUT[@]}" "$OUTPUT_HANDOUT"
echo "Written to $OUTPUT_HANDOUT"
