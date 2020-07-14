module.exports = {
  "extends": [
    "airbnb-typescript/base",
    'prettier/@typescript-eslint',
    'plugin:prettier/recommended',
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
    /*
    "arrow-parens": ["error", "as-needed"],
    "@typescript-eslint/indent": ['error', 2],
    "max-len": ["error", { "code": 140, "ignorePattern": "import *" }], // smaller than 140?
    "object-curly-newline": ["error", { "ImportDeclaration": "never" }],
    "quote-props": ["error", "as-needed"],
    "lines-between-class-members":  ["error", "always", { "exceptAfterSingleLine": true }],
    "comma-dangle": ["error", "only-multiline"],
    "no-underscore-dangle": ["error", { "allow": ["_links", "__karma__"] }],
    "no-param-reassign": ["error", { "props": false }],
    "no-plusplus" : ["error", { "allowForLoopAfterthoughts": true }],
    "@typescript-eslint/no-use-before-define": ["error", { "functions": false, "classes": false }],
    "@typescript-eslint/no-unused-expressions": ["error", { "allowTernary": true }],

    // all following rules SHOULD be removed
    "class-methods-use-this": "off",
    "import/extensions": "off",
    "import/no-unresolved": "off",
    "import/prefer-default-export": "off",
    "max-classes-per-file": "off",
    "@typescript-eslint/no-unused-vars": "off",
    */
    // all following rules MUST be removed (mostly autofix)
    "linebreak-style": ["off", "unix"], // own PR
  }
};
