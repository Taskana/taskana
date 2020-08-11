const { pathsToModuleNameMapper } = require('ts-jest/utils');
const { compilerOptions } = require('./tsconfig');

module.exports = {
  preset: 'jest-preset-angular',
  roots: ['<rootDir>/src'],
  modulePaths: ['<rootDir>'],
  moduleDirectories: ['node_modules', 'src'],
  testMatch: ['**/+(*.)+(spec).+(ts)'],
  setupFilesAfterEnv: ['<rootDir>/src/test.ts'],
  collectCoverage: true,
  coverageReporters: ['html', 'text'],
  coverageDirectory: 'coverage/taskana-web',
  moduleNameMapper: pathsToModuleNameMapper(compilerOptions.paths || {}, {
    prefix: '<rootDir>/'
  })
};
