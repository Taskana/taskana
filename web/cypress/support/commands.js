/**
 * @memberof cy
 * @method visitWorkbasketsInformationPage
 * @returns Chainable
 */
Cypress.Commands.add('visitWorkbasketsInformationPage', () => {
  cy.get('mat-tab-header').contains('Information').click();
});

/**
 * @memberof cy
 * @method visitWorkbasketsAccessPage
 * @returns Chainable
 */
Cypress.Commands.add('visitWorkbasketsAccessPage', () => {
  cy.get('mat-tab-header').contains('Access').click();
});

/**
 * @memberof cy
 * @method visitWorkbasketsDistributionTargetsPage
 * @returns Chainable
 */
Cypress.Commands.add('visitWorkbasketsDistributionTargetsPage', () => {
  cy.get('mat-tab-header').contains('Distribution Targets').click();
});

/**
 * @memberof cy
 * @method saveWorkbaskets
 * @returns Chainable
 */
Cypress.Commands.add('saveWorkbaskets', () => {
  cy.get('button').contains('Save').click();
});

/**
 * @memberof cy
 * @method undoWorkbaskets
 * @returns Chainable
 */
Cypress.Commands.add('undoWorkbaskets', () => {
  cy.get('button').contains('Undo Changes').click();
});

/**
 * @memberof cy
 * @method verifyPageLoad
 * @param {string} path
 * @returns Chainable
 */
Cypress.Commands.add('verifyPageLoad', (path) => {
  cy.location('hash', { timeout: 10000 }).should('include', path);
});

/**
 * @memberof cy
 * @method visitTestWorkbasket
 * @returns Chainable
 */
Cypress.Commands.add('visitTestWorkbasket', () => {
  cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/workbaskets');
  cy.verifyPageLoad('/workbaskets');

  // since the list is loaded dynamically, we need to explicitly wait 1000ms for the results
  // in order to avoid errors regarding detached DOM elements although it is a bad practice
  cy.wait(1000);
  cy.get('mat-selection-list').contains(Cypress.env('testValueWorkbasketSelectionName')).should('exist').click();
  cy.visitWorkbasketsInformationPage();
});

/**
 * @memberof cy
 * @method visitTestClassification
 * @returns Chainable
 */
Cypress.Commands.add('visitTestClassification', () => {
  cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
  cy.verifyPageLoad('/classifications');

  cy.get('kadai-administration-tree')
    .contains(Cypress.env('testValueClassificationSelectionName'))
    .should('exist')
    .click();
});

/**
 * @memberof cy
 * @method visitMonitor
 * @returns Chainable
 */
Cypress.Commands.add('visitMonitor', () => {
  cy.visit(Cypress.env('appUrl') + '/monitor');
  cy.wait(1000);
  cy.verifyPageLoad('/monitor');
});

/**
 * @memberof cy
 * @method loginAs
 * @param {string} username
 * @returns Chainable
 */
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

    cy.verifyPageLoad('/kadai/administration/workbaskets');
  }
});
