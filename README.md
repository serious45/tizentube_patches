# TizenTube Remote Patches

Patches for [TizenTube Cobalt](https://github.com/reisxd/TizenTubeCobalt) (the Android TV version of TizenTube), for use with Morphe.

This project is not affiliated with TizenTube or Morphe.

### How to use these patches

Add this repo URL to Morphe Manager or Morphe Desktop as a patch source: `https://github.com/GITHUB_USER/tizentube-patches`

<a href="https://morphe.software/add-source?github=GITHUB_USER/tizentube-patches">
  <img src=".github/assets/add-to-morphe.svg" alt="Add to Morphe" height="48">
</a>

## 🩹 Patches

<!-- PATCHES_START EXPANDED -->

### Back button opens Mini Player

Pressing **Back** on the remote while a video plays full screen opens TizenTube's **Mini Player**. The previous screen is shown, and the video keeps playing in the bottom-right corner.

Back keeps its normal behaviour when:
- a popup is open over the player (quality, captions, TizenTube settings…)
- you are typing in search
- the video has ended, or no video is playing
- the Mini Player is already showing

To return the video to full screen, use TizenTube's existing control: focus the search bar and press **Right**.

Tested against TizenTube Cobalt **2.0.3** (`io.gh.reisxd.tizentube.cobalt`). Newer versions are marked experimental.

<!-- PATCHES_END -->

## How it works

TizenTube's features are JavaScript that runs inside the app's built-in browser engine. The app's Java code does not change that script. The patch adds two instructions at the start of `dev.cobalt.coat.CobaltActivity.onKeyDown`. They pass a small script ([`back-to-mini-player.js`](patches/src/main/resources/tizentube/back-to-mini-player.js)) to the page through the app's own `evaluateJavaScript` method. The script installs only once per page load. It catches Back in the capture phase and runs TizenTube's own `ENTER_MP` (Mini Player) action.

## Things to know

- **Uninstall the original app first.** The patched APK is signed with a different key, so it cannot install over the original. You will need to sign in to YouTube and redo your TizenTube settings.
- **Updates:** TizenTube's built-in updater offers the unpatched APK. To update, patch the new APK again with Morphe and reinstall it.
- TizenTube's own script still loads fresh from jsDelivr at every launch, so TizenTube feature updates keep arriving without re-patching.

## 🧑‍💻 Building

Releases are built by GitHub Actions (`.github/workflows/release.yml`) with semantic-release:
- `feat:` and `fix:` commits on `main` create a release. `chore:` commits do not.
- Do not create releases by hand. The workflow updates `patches-bundle.json` and `patches-list.json`, which Morphe Manager reads.

Building locally (`./gradlew buildAndroid`) needs a GitHub personal access token with `read:packages`. Put it in `~/.gradle/gradle.properties` as `gpr.user` / `gpr.key`. See the [Morphe patcher docs](https://github.com/MorpheApp/morphe-patcher/blob/main/docs/2_1_setup.md#-prepare-the-environment). The output is `patches/build/libs/patches-*.mpp`.

## 📜 License

GPLv3, inherited from the Morphe patches template. See [LICENSE](LICENSE) and [NOTICE](NOTICE).

## 🧪 Tests

The injected script has a small test that runs in Node.js with no dependencies and uses a mock of the YouTube TV page:

```
node tests/back-to-mini-player.test.js
```
