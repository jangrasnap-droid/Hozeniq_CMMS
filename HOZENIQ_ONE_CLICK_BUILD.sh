#!/usr/bin/env bash
set -Eeuo pipefail

ZIP_FILE="HOZENIQ_CMMS_Audited_Project.zip"
ROOT="$(pwd)"

if [[ ! -d "$ROOT/.git" ]]; then
  echo "ERROR: Open the HOZENIQ GitHub repository root in Codespaces first."
  exit 1
fi
if [[ ! -f "$ROOT/$ZIP_FILE" ]]; then
  echo "ERROR: Upload $ZIP_FILE into the repository root (Explorer) and run again."
  exit 1
fi
if ! command -v unzip >/dev/null 2>&1; then
  echo "ERROR: unzip is not installed in this Codespace."
  exit 1
fi

TMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TMP_DIR"' EXIT
unzip -q "$ROOT/$ZIP_FILE" -d "$TMP_DIR"
PROJECT_DIR="$TMP_DIR/CMMS_Android"
if [[ ! -f "$PROJECT_DIR/settings.gradle.kts" || ! -d "$PROJECT_DIR/app/src/main" ]]; then
  echo "ERROR: ZIP does not contain the expected CMMS_Android Android project."
  exit 1
fi

# Copy the audited Android project into the repository root.
cp -a "$PROJECT_DIR/." "$ROOT/"

# Verify key files before pushing.
for f in settings.gradle.kts build.gradle.kts app/build.gradle.kts app/src/main/AndroidManifest.xml app/src/main/java/com/dharmvir/cmms/MainActivity.kt app/src/main/assets/index.html .github/workflows/android-apk.yml; do
  [[ -s "$ROOT/$f" ]] || { echo "ERROR: Missing or empty required file: $f"; exit 1; }
done

# Ensure the workflow runs on the main branch as shown in the project setup.
BRANCH="$(git -C "$ROOT" branch --show-current)"
if [[ "$BRANCH" != "main" ]]; then
  echo "ERROR: Current branch is '$BRANCH', not 'main'. Switch to main and run again."
  exit 1
fi

git -C "$ROOT" add -A
if git -C "$ROOT" diff --cached --quiet; then
  echo "No file changes to commit. The project files are already up to date."
else
  git -C "$ROOT" commit -m "Prepare HOZENIQ CMMS Android APK build"
fi

echo
echo "Pushing to GitHub to start the Android APK build..."
git -C "$ROOT" push origin main

echo
echo "DONE: Source project pushed. GitHub Actions will build the debug APK."
echo "Open GitHub repository > Actions > Build HOZENIQ Android APK."
echo "When complete, open the workflow run and download HOZENIQ-CMMS-debug-APK."
