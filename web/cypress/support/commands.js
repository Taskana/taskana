// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

Cypress.Commands.add('visitWorkbasketsAccessPage', () => {
  cy.get('.nav a').contains('Access').click();
});

Cypress.Commands.add('visitWorkbasketsDistributionTargetsPage', () => {
  cy.get('.nav a').contains('Distribution targets').click();
});

Cypress.Commands.add('saveWorkbaskets', () => {
  cy.get('.tab-pane.active > > .panel>.panel-heading>.pull-right > .btn-primary').click();
});

Cypress.Commands.add('visitTestWorkbasket', () => {
  cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/workbaskets');
  cy.contains(Cypress.env('testValueWorkbasketSelectionName')).click();
});

Cypress.Commands.add('reloadPageWithWait', () => {
  cy.reload();
  cy.wait(Cypress.env('pageReload'));
});

Cypress.Commands.add('loginAs', (username) => {
  return cy.request({
    method: 'POST',
    url: Cypress.env('baseUrl') + '/login',
    form: true,
    body: {
      username,
      password: username
    }
  });
});
