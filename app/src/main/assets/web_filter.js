(function () {
  'use strict';
  if (!/(^|\.)youtube\.com$/i.test(location.hostname) || window.__pulseFilterInstalled) return;
  Object.defineProperty(window, '__pulseFilterInstalled', { value: true });
  var stats = { inspectedPlayers: 0, removedFields: 0, skipClicks: 0 };
  Object.defineProperty(window, '__pulseFilterStats', { value: stats });
  var adFields = ['adPlacements', 'playerAds', 'adSlots', 'adBreakParams'];
  function cleanPlayer(value) {
    if (!value || typeof value !== 'object' || Array.isArray(value)) return value;
    if (value.videoDetails || value.streamingData || value.playabilityStatus) {
      stats.inspectedPlayers++;
      adFields.forEach(function (key) { try { if (Object.prototype.hasOwnProperty.call(value, key) && delete value[key]) stats.removedFields++; } catch (_) {} });
    }
    if (value.playerResponse && typeof value.playerResponse === 'object') cleanPlayer(value.playerResponse);
    return value;
  }
  function isPlayerUrl(value) {
    try { var u = new URL(typeof value === 'string' ? value : value.url, location.href);
      return /(^|\.)youtube\.com$/i.test(u.hostname) && /^\/youtubei\/v1\/player\/?$/.test(u.pathname);
    } catch (_) { return false; }
  }
  function cleanText(text) {
    if (typeof text !== 'string' || text.length > 12000000) return text;
    var prefix = '';
    if (text.indexOf(")]}'") === 0) { var n = text.indexOf('\n'); n = n < 0 ? 4 : n + 1; prefix = text.slice(0, n); text = text.slice(n); }
    try { return prefix + JSON.stringify(cleanPlayer(JSON.parse(text))); } catch (_) { return prefix + text; }
  }
  // Bootstrap responses arrive before the first web request on some pages.
  try {
    var initial = cleanPlayer(window.ytInitialPlayerResponse);
    var descriptor = Object.getOwnPropertyDescriptor(window, 'ytInitialPlayerResponse');
    if (!descriptor || descriptor.configurable) Object.defineProperty(window, 'ytInitialPlayerResponse', {
      configurable: true, enumerable: true,
      get: function () { return initial; }, set: function (value) { initial = cleanPlayer(value); }
    });
  } catch (_) {}
  // Leave media bytes, authentication, request bodies and non-player APIs untouched.
  if (typeof window.fetch === 'function') {
    var originalFetch = window.fetch;
    window.fetch = function () {
      var args = arguments;
      return originalFetch.apply(this, args).then(function (response) {
        if (!isPlayerUrl(args[0]) || !response.ok) return response;
        return response.clone().text().then(function (text) {
          var cleaned = cleanText(text); if (cleaned === text) return response;
          var headers = new Headers(response.headers); headers.delete('content-length'); headers.delete('content-encoding');
          var result = new Response(cleaned, { status: response.status, statusText: response.statusText, headers: headers });
          ['url', 'redirected', 'type'].forEach(function (name) { try { Object.defineProperty(result, name, { value: response[name] }); } catch (_) {} });
          return result;
        }).catch(function () { return response; });
      });
    };
  }
  if (typeof XMLHttpRequest !== 'undefined') {
    var proto = XMLHttpRequest.prototype, open = proto.open, xhrUrls = new WeakMap(), hooked = new WeakSet(), rewritten = new WeakSet();
    proto.open = function (method, url) {
      if (rewritten.has(this)) { try { delete this.responseText; delete this.response; } catch (_) {} rewritten.delete(this); }
      xhrUrls.set(this, url);
      if (isPlayerUrl(url) && !hooked.has(this)) { hooked.add(this); this.addEventListener('readystatechange', function () {
        if (!isPlayerUrl(xhrUrls.get(this)) || this.readyState !== 4 || this.status < 200 || this.status >= 300) return;
        try {
          if (this.responseType === 'json') { var data = cleanPlayer(this.response); Object.defineProperty(this, 'response', { configurable: true, get: function () { return data; } }); rewritten.add(this); }
          else if (!this.responseType || this.responseType === 'text') {
            var text = cleanText(this.responseText);
            Object.defineProperty(this, 'responseText', { configurable: true, get: function () { return text; } });
            Object.defineProperty(this, 'response', { configurable: true, get: function () { return text; } }); rewritten.add(this);
          }
        } catch (_) {}
      }, true); }
      return open.apply(this, arguments);
    };
  }
  var css = 'ytd-ad-slot-renderer,ytm-ad-slot-renderer,ytd-display-ad-renderer,ytd-in-feed-ad-layout-renderer,ytd-promoted-sparkles-web-renderer,ytm-promoted-sparkles-web-renderer,ytm-promoted-video-renderer,ytm-companion-ad-renderer,.ytp-ad-overlay-container{display:none!important}';
  function refresh() {
    if (!document.documentElement) return;
    if (!document.getElementById('pulse-filter')) { var style = document.createElement('style'); style.id = 'pulse-filter'; style.textContent = css; document.documentElement.appendChild(style); }
    // Click only an available skip control. Never seek the video to its end.
    document.querySelectorAll('.ytp-ad-skip-button,.ytp-ad-skip-button-modern,.ytp-skip-ad-button').forEach(function (button) {
      if (!button.disabled && button.getClientRects().length) button.click(); stats.skipClicks++;
    });
  }
  var pending = false;
  function schedule() { if (!pending) { pending = true; setTimeout(function () { pending = false; refresh(); }, 250); } }
  function watch() { refresh(); if (document.documentElement && typeof MutationObserver === 'function') new MutationObserver(schedule).observe(document.documentElement, { childList: true, subtree: true }); }
  if (document.documentElement) watch(); else document.addEventListener('DOMContentLoaded', watch, { once: true });
  document.addEventListener('yt-navigate-finish', schedule);
}());
