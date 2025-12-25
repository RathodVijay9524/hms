// MedLab Pro - Theme Switcher
(function (window) {
    'use strict';

    const THEME_KEY = 'medlab-theme';

    // Set theme on <html> and sync with Bootstrap 5.3+ native theme
    const applyTheme = (theme) => {
        document.documentElement.setAttribute('data-theme', theme);
        document.documentElement.setAttribute('data-bs-theme', theme);
        localStorage.setItem(THEME_KEY, theme);

        // Dispatch custom event for UI updates (e.g., icons)
        document.dispatchEvent(new CustomEvent('themeChanged', { detail: { theme } }));
    };

    const toggleTheme = () => {
        const current = localStorage.getItem(THEME_KEY) || 'dark';
        const next = current === 'dark' ? 'light' : 'dark';
        applyTheme(next);
    };

    // Initialize theme and listeners
    const init = () => {
        const savedTheme = localStorage.getItem(THEME_KEY) || 'dark';
        applyTheme(savedTheme);

        document.querySelectorAll('.theme-toggle').forEach(btn => {
            btn.addEventListener('click', toggleTheme);
        });

        // Sidebar Toggle Logic
        const sidebarToggle = document.getElementById('sidebarToggle');
        const sidebar = document.querySelector('.sidebar');
        
        if (sidebarToggle && sidebar) {
            sidebarToggle.addEventListener('click', () => {
                sidebar.classList.toggle('show');
            });

            // Close sidebar when clicking outside on mobile
            document.addEventListener('click', (e) => {
                if (window.innerWidth < 992 && 
                    sidebar.classList.contains('show') && 
                    !sidebar.contains(e.target) && 
                    !sidebarToggle.contains(e.target)) {
                    sidebar.classList.remove('show');
                }
            });
        }
    };

    // Public API
    window.MedLabTheme = {
        toggle: toggleTheme,
        apply: applyTheme,
        get: () => localStorage.getItem(THEME_KEY) || 'dark'
    };

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})(window);
