// This file is required by karma.conf.js and loads recursively all the .spec and framework files

import 'zone.js/dist/long-stack-trace-zone';
import 'zone.js/dist/proxy.js';
import 'zone.js/dist/sync-test';
import 'zone.js/dist/jasmine-patch';
import 'zone.js/dist/async-test';
import 'zone.js/dist/fake-async-test';
import { getTestBed } from '@angular/core/testing';
import { BrowserDynamicTestingModule, platformBrowserDynamicTesting } from '@angular/platform-browser-dynamic/testing';
import 'rxjs';

// Unfortunately there's no typing for the `__karma__` variable. Just declare it as any.
declare let __karma__: any;
declare let require: any;

// Prevent Karma from running prematurely.
__karma__.loaded = function noop() {};

// First, initialize the Angular testing environment.
getTestBed().initTestEnvironment(BrowserDynamicTestingModule, platformBrowserDynamicTesting());
// Then we find all the tests.
const contextAdministration = require.context('./app/administration', true, /\.spec\.ts$/);
const contextWorkplace = require.context('./app/workplace', true, /\.spec\.ts$/);
// const contextMonitor = require.context('./app/monitor', true, /\.spec\.ts$/);
const contextShared = require.context('./app/shared', true, /\.spec\.ts$/);
// And load the modules.
contextAdministration.keys().map(contextAdministration);
contextWorkplace.keys().map(contextWorkplace);
// contextMonitor.keys().map(contextMonitor);
contextShared.keys().map(contextShared);
// Finally, start Karma to run the tests.
__karma__.start();
