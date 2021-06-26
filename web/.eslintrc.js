module.exports = {
  extends: ['prettier', 'plugin:prettier/recommended'],
  env: {
    browser: true,
    node: true
  },
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 2018,
    sourceType: 'module',
    project: './tsconfig.json',
    errorOnUnknownASTType: true,
    errorOnTypeScriptSyntacticAndSemanticIssues: true
  },
  plugins: ['@typescript-eslint', '@typescript-eslint/tslint'],
  rules: {}
};
