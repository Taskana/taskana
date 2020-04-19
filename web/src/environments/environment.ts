// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --configuration=production` then `environment.prod.ts` will be used instead.
// The classification-list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  taskanaRestUrl: 'http://localhost:8080/taskana/api',
  taskanaLogoutUrl: 'http://localhost:8080/taskana/logout'
};
