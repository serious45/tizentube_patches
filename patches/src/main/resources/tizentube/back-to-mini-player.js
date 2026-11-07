/*
 * Back button -> Mini Player for TizenTube Cobalt.
 *
 * Injected by the Morphe patch on every key press. The guard below makes it
 * install only once per page load. It catches the remote's Back key while a
 * video plays full screen and runs TizenTube's own Mini Player action
 * (ENTER_MP), which goes back to the previous screen and keeps the video
 * playing small in the corner.
 *
 * The patch strips block comments, joins the lines and embeds the result as
 * one smali const-string, so use block comments only (no line comments).
 */
(function () {
    if (window.__ttBackToMiniPlayer) return;
    window.__ttBackToMiniPlayer = true;

    var swallowUntilKeyUp = false;

    function isBackKey(e) {
        return e.keyCode === 27 || e.keyCode === 166 || e.key === 'GoBack' || e.key === 'BrowserBack';
    }

    function findYouTubeApp() {
        var yttv = window._yttv;
        if (!yttv) return null;
        for (var key in yttv) {
            var entry = yttv[key];
            if (entry && entry.instance && typeof entry.instance.resolveCommand === 'function') return entry.instance;
        }
        return null;
    }

    function canMinimize() {
        /* TizenTube sets window.isPipPlaying when it loads. Without TizenTube there is no Mini Player. */
        if (typeof window.isPipPlaying === 'undefined') return false;
        if (window.isPipPlaying) return false;

        /* TizenTube's own theme panel is open: let Back close it. */
        var themePanel = document.querySelector('.ytaf-ui-container');
        if (themePanel && themePanel.style.display !== 'none') return false;

        /* Typing in a text field. */
        var focused = document.activeElement;
        if (focused && (focused.tagName === 'INPUT' || focused.tagName === 'TEXTAREA')) return false;

        /* The watch page must be the active layer. A popup on top of the player makes it non-focusable. */
        var watchPage = document.querySelector('ytlr-watch-default');
        if (!watchPage || watchPage.getAttribute('hybridnavfocusable') !== 'true') return false;

        var player = document.querySelector('ytlr-player');
        if (!player || !player.classList.contains('ytLrPlayerEnabled')) return false;

        var video = document.querySelector('video');
        if (!video || !video.currentSrc || video.ended) return false;

        return findYouTubeApp() !== null;
    }

    function onKey(e) {
        if (!isBackKey(e)) return;

        /* A fresh press always starts clean, in case a keyup was ever lost. */
        if (e.type === 'keydown' && !e.repeat) swallowUntilKeyUp = false;

        if (swallowUntilKeyUp) {
            if (e.type === 'keyup') swallowUntilKeyUp = false;
            e.preventDefault();
            e.stopImmediatePropagation();
            return;
        }

        if (e.type !== 'keydown' || !canMinimize()) return;

        swallowUntilKeyUp = true;
        e.preventDefault();
        e.stopImmediatePropagation();
        try {
            findYouTubeApp().resolveCommand({
                commandExecutorCommand: {
                    commands: [{ customAction: { action: 'ENTER_MP' } }]
                }
            });
        } catch (err) {
            console.error('[BackToMiniPlayer] could not enter Mini Player', err);
        }
    }

    window.addEventListener('keydown', onKey, true);
    window.addEventListener('keypress', onKey, true);
    window.addEventListener('keyup', onKey, true);
    console.info('[BackToMiniPlayer] installed');
})();
