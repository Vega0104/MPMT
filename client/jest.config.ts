import type { Config } from '@jest/types';

const config: Config.InitialOptions = {
  preset: 'jest-preset-angular',
  testEnvironment: 'jsdom',
  setupFilesAfterEnv: ['<rootDir>/src/setup-jest.ts'],
  testMatch: ['**/__tests__/**/*.spec.ts', '**/?(*.)+(spec).ts'],
  transform: {
    '^.+\\.(ts|js|html)$': [
      'jest-preset-angular',
      {
        tsconfig: '<rootDir>/tsconfig.spec.json',
        stringifyContentPathRegex: '\\.(html|svg)$',
      },
    ],
  },
  transformIgnorePatterns: [
    'node_modules/(?!.*\\.mjs$|@ngrx|angular2-uuid|ng-dynamic)'
  ],
  moduleNameMapper: {
    '^@app/(.*)$': '<rootDir>/src/app/$1',
    '^@env$': '<rootDir>/src/environments/environment',
  },
  collectCoverage: false,
  moduleFileExtensions: ['ts', 'html', 'js', 'json', 'mjs'],
};

export default config;
