# Building VoxDub AI APK using GitHub Actions Workflow

This project includes an automated GitHub Actions CI/CD workflow configured in `.github/workflows/build-apk.yml` to compile and package error-free Android APKs.

---

## 🚀 How to Build and Download Your APK

### Step 1: Push Project to Your GitHub Repository
Ensure all project files, including `.github/workflows/build-apk.yml`, are committed and pushed to GitHub:
```bash
git add .
git commit -m "Add GitHub Actions workflow for APK build"
git push origin main
```

### Step 2: (Optional) Set API Key Secret in GitHub
If you want your APK to include an automated Gemini API Key:
1. On GitHub, go to your repository **Settings** > **Secrets and variables** > **Actions**.
2. Click **New repository secret**.
3. Name: `GEMINI_API_KEY`
4. Value: Paste your Gemini API key.

*(Note: Users can also input or change their API key directly inside the app under the **Settings** tab!)*

### Step 3: Run the Workflow
The build automatically triggers on every `push` to `main` or `master`. You can also trigger it manually:
1. Navigate to the **Actions** tab in your GitHub repository.
2. Select **Build Android APK** from the left sidebar.
3. Click the **Run workflow** dropdown button on the right, select branch `main`, and click **Run workflow**.

### Step 4: Download Your Built APK
1. Click on the completed workflow run (marked with a green checkmark ✅).
2. Scroll down to the **Artifacts** section at the bottom of the summary page.
3. Click on **VoxDub-AI-Video-Dubber-Debug-APK** to download the ZIP file containing the compiled `.apk`.
4. Extract the ZIP to obtain `app-debug.apk`.

### Step 5: Install on Android Device
1. Transfer `app-debug.apk` to your Android device via USB, Google Drive, or direct download.
2. Tap the APK file to install (enable "Install unknown apps" permission if prompted).
3. Open **VoxDub AI** and start dubbing videos!
