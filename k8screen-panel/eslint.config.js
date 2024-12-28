import pluginJs from '@eslint/js';
import tseslint from 'typescript-eslint';
import globals from 'globals';

const IGNORES = [
  '.DS_Store',
  'node_modules',
  '/build',
  '.svelte-kit/**',
  '/package',
  '.env',
  '.env.*',
  '!.env.example',
  'pnpm-lock.yaml',
  'package-lock.json',
  'yarn.lock',
  'vite.config.js'
];

const CONFIGS = [
  { languageOptions: { globals: globals.browser } },
  { ...pluginJs.configs.recommended },
  ...tseslint.configs.recommended,
  { rules: { 
    '@typescript-eslint/require-await': 'off',
    '@typescript-eslint/no-explicit-any': 'off'
  }},
  {}
];

CONFIGS.map((c) => {
  if (!c.ignores) {
    c.ignores = [];
  }

  IGNORES.forEach((i) => c.ignores.push(i));
});

export default CONFIGS;
