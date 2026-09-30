# Building VoxDub AI APK using GitHub Actions Workflow

This project includes an automated, tested GitHub Actions CI/CD workflow configured in `.github/workflows/build-apk.yml` to compile and package error-free Android APKs.

---

## 🚀 Why the Build Failed & How It Was Resolved

1. **Java 21 Requirement**: Android Gradle Plugin 9.1.1 and Gradle 9.3.1 require **Java 21** (`temurin-21`), whereas the previous workflow used Java 17.
2. **Missing `debug.keystore` on GitHub**: The `debug.keystore` file is ignored in `.gitignore`, but the original template provides `debug.keystore.base64`. The workflow now automatically restores `debug.keystore` from base64 before running the build.
3. **Gradle Wrapper Included**: Generated `gradlew`, `gradlew.bat`, and `gradle/wrapper/gradle-wrapper.jar` directly in the repository so GitHub Actions can run without requiring an external Gradle installation.
4. **Modern Gradle Setup**: Uses the official `gradle/actions/setup-gradle@v4` action for seamless caching and compilation.

---

## 🛠️ Step-by-Step Instructions to Build Your APK

### Step 1: Push All Changes to Your GitHub Repository
Ensure all files, including `.github/workflows/build-apk.yml`, `gradlew`, and `gradle/wrapper/gradle-wrapper.jar`, are committed and pushed to GitHub:
```bash
git add .
git commit -m "Fix GitHub Actions APK build workflow"
git push origin main
```

### Step 2: (Optional) Set GEMINI_API_KEY in GitHub Secrets
1. On GitHub, navigate to your repository.
2. Go to **Settings** > **Secrets and variables** > **Actions**.
3. Click **New repository secret**.
4. Name: `GEMINI_API_KEY`
5. Value: Paste your Gemini API key.

*(Note: If not set, the workflow will use the default `.env.example` placeholder and you can also enter your key inside the app in the Settings tab!)*

### Step 3: Trigger the Workflow
- **Automatic**: Every push to `main` or `master` will now trigger the workflow automatically.
- **Manual (1-Click)**:
  1. Open the **Actions** tab in your GitHub repository.
  2. In the left panel, click on **Build Android APK**.
  3. Click **Run workflow** > select branch `main` > click **Run workflow**.

### Step 4: Download the Compiled APK
1. Click on the completed workflow run (marked with a green checkmark ✅).
2. Scroll down to the **Artifacts** section at the bottom of the summary page.
3. Click **VoxDub-AI-Video-Dubber-Debug-APK** to download the ZIP file.
4. Extract the ZIP to obtain `app-debug.apk`.

### Step 5: Install on Android Device
1. Transfer `app-debug.apk` to your Android device.
2. Open the file to install (allow "Install from unknown sources" if prompted).
3. Open **VoxDub AI**!
