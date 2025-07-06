#!/bin/bash

set -e

VERSION=$1
APP_NAME="PokemonXhenos"
APP_BUNDLE="${APP_NAME}.app"
DMG_NAME="${APP_NAME}_Mac_${VERSION}.dmg"
BUILD_DIR="mac_bundle"
STAGING_DIR="dmg_staging"
OUTPUT_DIR="releases"

# Colors
GREEN="\033[0;32m"
NC="\033[0m"

echo -e "${GREEN}==> Creating .dmg for version ${VERSION}${NC}"

# Cleanup and setup
rm -rf "$STAGING_DIR"
mkdir -p "$STAGING_DIR" "$OUTPUT_DIR"

# Copy .app into staging
cp -R "$BUILD_DIR/$APP_BUNDLE" "$STAGING_DIR/"

# Ensure executable bit is set
chmod +x "$STAGING_DIR/$APP_BUNDLE/Contents/MacOS/$APP_NAME"

# Applications shortcut
ln -s /Applications "$STAGING_DIR/Applications"

# Create DMG
hdiutil create -volname "$APP_NAME" \
  -srcfolder "$STAGING_DIR" \
  -ov -format UDZO "$OUTPUT_DIR/$DMG_NAME"

echo -e "${GREEN}✔ Created: $OUTPUT_DIR/$DMG_NAME${NC}"