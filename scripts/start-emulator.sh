#!/bin/bash
# Start Firebase emulators and seed test data for seasonal events
#
# Usage:
#   ./scripts/start-emulator.sh          # start emulator + seed
#   ./scripts/start-emulator.sh --seed   # seed only (emulator already running)

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

# Install firebase-admin if not present
if [ ! -d "node_modules/firebase-admin" ]; then
    echo "Installing firebase-admin..."
    npm install --save-dev firebase-admin
fi

if [ "$1" = "--seed" ]; then
    echo "Seeding emulator with test data..."
    node scripts/seed-seasonal-events.js
    exit 0
fi

echo "Starting Firebase emulators..."
echo ""
echo "  Firestore:    http://localhost:8080"
echo "  Emulator UI:  http://localhost:4000"
echo ""
echo "Tip: In another terminal, run './scripts/start-emulator.sh --seed' to populate test data"
echo ""

firebase emulators:start --import=./emulator-data --export-on-exit=./emulator-data
