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

Cypress.Commands.add('visitWorkbasketsInformationPage', () => {
  cy.get('mat-tab-header').contains('Information').click();
});

Cypress.Commands.add('visitWorkbasketsAccessPage', () => {
  cy.get('mat-tab-header').contains('Access').click();
});

Cypress.Commands.add('visitWorkbasketsDistributionTargetsPage', () => {
  cy.get('mat-tab-header').contains('Distribution Targets').click();
});

Cypress.Commands.add('saveWorkbaskets', () => {
  cy.get('button').contains('Save').click();
});

Cypress.Commands.add('visitTestWorkbasket', () => {
  cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/workbaskets');
  cy.contains(Cypress.env('testValueWorkbasketSelectionName')).click();
  cy.visitWorkbasketsInformationPage();
});

Cypress.Commands.add('reloadPageWithWait', () => {
  cy.reload();
  cy.wait(Cypress.env('pageReload'));
});

Cypress.Commands.add('loginAs', (username) => {

  cy.visit(Cypress.env('baseUrl') + '/login');

  cy.get('#username').type('admin').should('have.value', 'admin');

  cy.get('#password').type('admin').should('have.value', 'admin');

  cy.get('#login-submit').click();

  cy.reload();
  cy.wait(Cypress.env('pageReload'));

  cy.location().should((location) => {
    expect(location.href).to.eq(Cypress.env('baseUrl') + '/#/taskana/workplace/tasks');
  });
  
});
