set -e

APP_NAME="PokemonXhenos"
APP_DIR="mac_bundle/${APP_NAME}.app"
JAR_PATH=$(find "$APP_DIR" -name "${APP_NAME}-v*.jar" | head -n 1)

if [ ! -f "$JAR_PATH" ]; then
  osascript -e 'display dialog "Could not find PokemonXhenos-v*.jar in the .app bundle." buttons {"OK"} default button "OK" with icon stop'
  exit 1
fi

# Extract version from JAR filename
VERSION=$(basename "$JAR_PATH" | sed -E "s/${APP_NAME}-v([0-9\.]+)\.jar/\1/")

if [ -z "$VERSION" ]; then
  osascript -e 'display dialog "Failed to extract version from the .jar file name." buttons {"OK"} default button "OK" with icon stop'
  exit 1
fi

# GUI confirmation
osascript <<EOF
set userResponse to button returned of (display dialog "Build .dmg for version $VERSION?" buttons {"Cancel", "Continue"} default button "Continue" with icon note)
if userResponse is "Cancel" then error number -128
EOF

# Run the build
echo "📦 Building DMG for version: $VERSION"
bash create_dmg.sh "$VERSION"

# Custom success sound (pick your own .aiff or .wav if desired)
afplay /System/Library/Sounds/Ping.aiff &

# Final .dmg file
DMG_PATH="$(pwd)/releases/${APP_NAME}_Mac_${VERSION}.dmg"

# Show GUI success dialog
osascript <<EOF
display dialog "DMG build complete for version $VERSION!" buttons {"OK"} default button "OK" with title "Success 🎉" with icon note
EOF

# Reveal .dmg in Finder
osascript -e "tell application \"Finder\" to reveal POSIX file \"$DMG_PATH\""
osascript -e "tell application \"Finder\" to activate"