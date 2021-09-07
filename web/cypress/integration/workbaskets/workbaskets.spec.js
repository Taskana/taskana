context('TASKANA Workbaskets', () => {
  beforeEach(() => cy.loginAs('admin'));

  it('should be able to see all workbaskets', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/workbaskets');
    cy.verifyPageLoad('/workbaskets');
  });

  it('should be able to filter workbaskets by name', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/workbaskets');
    cy.verifyPageLoad('/workbaskets');

    cy.get('input[mattooltip="Type to filter by name"]')
      .type(Cypress.env('testValueWorkbasketSelectionName'))
      .type('{enter}')
      .then(() => {
        cy.get('mat-selection-list[role="listbox"]').should('have.length', 1);
      });
  });

  it('should be possible to edit workbasket custom information', () => {
    cy.visitTestWorkbasket();

    cy.get('#wb-custom-1').clear().type(Cypress.env('testValueWorkbaskets'));

    cy.saveWorkbaskets();
    cy.get('#wb-custom-1').should('have.value', Cypress.env('testValueWorkbaskets'));
  });

  it('should be possible to edit workbasket information orgLevel', () => {
    cy.visitTestWorkbasket();

    cy.get('input[name="workbasket.orgLevel1"]').clear().type(Cypress.env('testValueWorkbaskets'));

    cy.saveWorkbaskets();
    cy.get('input[name="workbasket.orgLevel1"]').should('have.value', Cypress.env('testValueWorkbaskets'));
  });

  it('should be possible to edit workbasket description', () => {
    cy.visitTestWorkbasket();

    cy.get('#workbasket-description')
      .clear()
      .type(Cypress.env('testValueWorkbaskets'))
      .then(() => {
        cy.saveWorkbaskets();
        cy.get('#workbasket-description').should('have.value', Cypress.env('testValueWorkbaskets'));
      });
  });

  it('should be possible to edit the type of a workbasket', () => {
    cy.visitTestWorkbasket();

    cy.get('mat-form-field').contains('mat-form-field', 'Type').find('mat-select').click();
    cy.wait(Cypress.env('dropdownWait'));
    cy.get('mat-option').contains('Clearance').click();
    cy.saveWorkbaskets();
    cy.wait(3050); //wait for toasts to disappear

    // assure that its Clearance now
    cy.get('mat-form-field').contains('mat-form-field', 'Type').contains('Clearance').should('be.visible');

    // change back to Group
    cy.get('mat-form-field').contains('mat-form-field', 'Type').find('mat-select').click();
    cy.wait(Cypress.env('dropdownWait'));
    cy.get('mat-option').contains('Group').should('be.visible').click();

    cy.saveWorkbaskets();
  });

  it('should be possible to add new access', () => {
    cy.visitTestWorkbasket();
    cy.visitWorkbasketsAccessPage();

    cy.get('button[mattooltip="Add new access"')
      .click()
      .then(() => {
        cy.get('mat-form-field')
          .contains('mat-form-field', 'Access id')
          .find('input')
          .type('teamlead-2', { force: true });
        cy.get('input[aria-label="checkAll"]:first').click();
        cy.saveWorkbaskets();
      });

    cy.visitWorkbasketsAccessPage();
    cy.get('table#table-access-items > tbody > tr').should('have.length', 2);
  });

  it('should be possible to add a distribution target', () => {
    cy.visitTestWorkbasket();
    cy.visitWorkbasketsDistributionTargetsPage();

    cy.get('taskana-administration-workbasket-distribution-targets-list[header="Available distribution targets"]')
      .find('mat-list-option:first')
      .find('mat-pseudo-checkbox')
      .click();
    cy.get('button').contains('Add selected distribution targets').click();

    cy.saveWorkbaskets();

    cy.visitWorkbasketsDistributionTargetsPage();
    cy.get('taskana-administration-workbasket-distribution-targets-list[header="Selected distribution targets"]')
      .find('mat-selection-list')
      .should('not.be.empty');

    // undo changes
    cy.get('taskana-administration-workbasket-distribution-targets-list[header="Selected distribution targets"]')
      .find('mat-list-option:first')
      .find('mat-pseudo-checkbox')
      .click();
    cy.get('button').contains('Remove selected distribution target').click();

    cy.saveWorkbaskets();
  });
});
