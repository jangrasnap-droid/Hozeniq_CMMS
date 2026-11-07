HOZENIQ CMMS ANDROID BUILD PACKAGE

1. Extract this package.
2. Upload BOTH files into the root of your GitHub Codespaces repository:
   - HOZENIQ_CMMS_Audited_Project.zip
   - HOZENIQ_ONE_CLICK_BUILD.sh
3. Open the repository root in the Codespaces terminal.
4. Run:
   bash HOZENIQ_ONE_CLICK_BUILD.sh

The script copies the Android project files, verifies required files,
commits changes, and pushes to origin/main. GitHub Actions then attempts
the APK build. Compilation success is confirmed only by the Actions result.
