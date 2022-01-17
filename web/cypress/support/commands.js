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

Cypress.Commands.add('verifyPageLoad', (path) => {
  cy.location('hash', { timeout: 10000 }).should('include', path);
});

Cypress.Commands.add('visitTestWorkbasket', () => {
  cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/workbaskets');
  cy.verifyPageLoad('/workbaskets');

  // since the list is loaded dynamically, we need to explicitly wait 1000ms for the results
  // in order to avoid errors regarding detached DOM elements although it is a bad practice
  cy.wait(1000);
  cy.get('mat-selection-list').contains(Cypress.env('testValueWorkbasketSelectionName')).should('exist').click();
  cy.visitWorkbasketsInformationPage();
});

Cypress.Commands.add('visitTestClassification', () => {
  cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
  cy.verifyPageLoad('/classifications');

  cy.get('taskana-administration-tree')
    .contains(Cypress.env('testValueClassificationSelectionName'))
    .should('exist')
    .click();
});

Cypress.Commands.add('loginAs', (username) => {
  if (Cypress.env('isLocal')) {
    cy.log('Local development - No need for testing login functionality');
  } else {
    cy.visit(Cypress.env('loginUrl') + '/login');
    // not calling verifyPageLoad as we cannot verify via hash in this case
    cy.location('pathname', { timeout: 10000 }).should('include', '/login');

    cy.get('#username').type('admin').should('have.value', 'admin');
    cy.get('#password').type('admin').should('have.value', 'admin');
    cy.get('#login-submit').click();

    cy.verifyPageLoad('/workplace/tasks');
  }
});
