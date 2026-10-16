// Apply the saved palette before styles load, avoiding a flash of the wrong theme.
(() => {
  const key = 'electrotechnique.local.v1';
  function apply(value) {
    const theme = value === 'light' ? 'light' : 'dark';
    document.documentElement.dataset.theme = theme;
    document.querySelector('meta[name="theme-color"]')?.setAttribute('content', theme === 'light' ? '#F3F6FA' : '#05070A');
    return theme;
  }
  let saved;
  try { saved = JSON.parse(localStorage.getItem(key) || '{}')?.theme; } catch {}
  apply(saved);
  window.guideTheme = {apply};
})();
