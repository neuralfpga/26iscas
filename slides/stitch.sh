#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUTPUT="${SCRIPT_DIR}/combined.pdf"

mapfile -t PDFS < <(
  find "$SCRIPT_DIR" -maxdepth 2 -regex '.*/[0-9][^/]*/main\.pdf' | sort
)

if [[ ${#PDFS[@]} -eq 0 ]]; then
  echo "No numbered slide directories with main.pdf found." >&2
  exit 1
fi

echo "Stitching:"
printf '  %s\n' "${PDFS[@]}"

pdfunite "${PDFS[@]}" "$OUTPUT"
echo "Written to $OUTPUT"
