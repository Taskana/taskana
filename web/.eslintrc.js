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
    "arrow-parens": ["off", "as-needed"],
    "@typescript-eslint/indent": ['error', 2],
    "import/extensions": "off",
    "import/no-unresolved": "off",
    "max-classes-per-file": "off",
    "max-len": ["error", { "code": 140, "ignorePattern": "import *" }], // smaller than 140?
    "no-plusplus": "off",
    "object-curly-newline": ["off", { "ImportDeclaration": "never" }],
    "quote-props": ["error", "as-needed"],

    // This rules shall be activated after they were corrected
    "lines-between-class-members":  ["error", "always", { exceptAfterSingleLine: true }],
    "comma-dangle": ["error", "only-multiline"],
    "no-underscore-dangle": ["warn", { "allow": ["_links", "__karma__"] }],
    "no-param-reassign": ["warn", { "props": false }],

    // all following rules SHOULD be removed
    "class-methods-use-this": "off",
    "import/order": "off",
    "import/prefer-default-export": "off",
    "no-useless-escape": "off",
    "object-curly-spacing": "off",
    "padded-blocks": "off",
    "@typescript-eslint/no-unused-vars": "off",
    "@typescript-eslint/no-unused-expressions": "off",
    "@typescript-eslint/semi": "off",

    // all following rules MUST be removed (mostly autofix)
    "linebreak-style": ["off", "unix"],
    "no-restricted-syntax": "off",
    "consistent-return": "off",
    "no-return-assign": "off",
    "prefer-destructuring": "off",
    "@typescript-eslint/no-empty-function": "off",
    "@typescript-eslint/no-useless-constructor": "off",
    "@typescript-eslint/no-use-before-define": "off",
    "@typescript-eslint/camelcase": "off",
    "no-multi-assign": "off",
    "no-new-object": "off",
    "array-callback-return": "off",
    "no-mixed-operators": "off",
    "no-multi-str": "off",
    "no-nested-ternary": "off",
    "no-sequences": "off",
    "no-tabs": "off",
    "no-self-assign": "off",
    "global-require": "off",
    "no-prototype-builtins": "off",
  }
};
