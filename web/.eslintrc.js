module.exports = {
  "extends": [
    "airbnb-typescript/base"
  ],
  "env": {
    "browser": true,
    "node": true
  },
  "parser": "@typescript-eslint/parser",
  "parserOptions": {
    "project": "tsconfig.json",
  },
  "plugins": [
        "@typescript-eslint",
        "@typescript-eslint/tslint"
  ],
  "rules": {
    "arrow-parens": ["error", "as-needed"],
    "@typescript-eslint/indent": ['error', 2],
    "max-len": ["off", { "code": 140, "ignorePattern": "import *" }], // smaller than 140?
    "object-curly-newline": ["error", { "ImportDeclaration": "never" }],
    "quote-props": ["error", "as-needed"],
    "lines-between-class-members":  ["error", "always", { "exceptAfterSingleLine": true }],
    "comma-dangle": ["error", "only-multiline"],
    "no-underscore-dangle": ["off", { "allow": ["_links", "__karma__"] }],
    "no-param-reassign": ["off", { "props": false }],

    // all following rules SHOULD be removed
    "class-methods-use-this": "off",
    "import/extensions": "off",
    "import/no-unresolved": "off",
    "import/prefer-default-export": "off",
    "max-classes-per-file": "off",
    "@typescript-eslint/no-unused-vars": "off",

    // all following rules MUST be removed (mostly autofix)
    "linebreak-style": ["off", "unix"], // own PR
    "no-restricted-syntax": "off",
    "@typescript-eslint/no-use-before-define": "off",
    "@typescript-eslint/camelcase": "off",
    "no-plusplus": "off",
    "no-prototype-builtins": "off",
  }
};
