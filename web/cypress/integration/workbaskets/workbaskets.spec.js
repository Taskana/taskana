context('KADAI Workbaskets', () => {
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

    cy.get('kadai-administration-workbasket-distribution-targets-list[header="Available distribution targets"]')
      .find('mat-list-option:first')
      .find('mat-pseudo-checkbox')
      .click();
    cy.get('button').contains('Add selected distribution targets').click();

    cy.saveWorkbaskets();

    cy.visitWorkbasketsDistributionTargetsPage();
    cy.get('kadai-administration-workbasket-distribution-targets-list[header="Selected distribution targets"]')
      .find('mat-selection-list')
      .should('not.be.empty');

    // undo changes
    cy.get('kadai-administration-workbasket-distribution-targets-list[header="Selected distribution targets"]')
      .find('mat-list-option:first')
      .find('mat-pseudo-checkbox')
      .click();
    cy.get('button').contains('Remove selected distribution target').click();

    cy.saveWorkbaskets();
  });

  it('should be possible to change the name of a workbasket and switch tabs and transfer workbaskets without loosing changes', () => {
    cy.visitTestWorkbasket();

    cy.get('#workbasket-name').clear().type(Cypress.env('testValueWorkbaskets'));

    cy.visitWorkbasketsDistributionTargetsPage();

    cy.get(
      '#dual-list-Left > .distribution-targets-list > .mat-selection-list > .cdk-virtual-scroll-viewport > .cdk-virtual-scroll-content-wrapper > :nth-child(1)'
    )
      .should('contain.text', 'Basxet1')
      .click();

    cy.get('.distribution-targets-list__action-buttons--chooser').click();

    cy.get('.workbasket-details__title-name').should('contain.text', Cypress.env('testValueWorkbaskets'));
  });

  it('should be possible to transfer distribution targets and switch tabs without loosing changes', () => {
    cy.visitTestWorkbasket();
    cy.visitWorkbasketsDistributionTargetsPage();

    cy.get(
      '#dual-list-Left > .distribution-targets-list > .mat-selection-list > .cdk-virtual-scroll-viewport > .cdk-virtual-scroll-content-wrapper > :nth-child(1)'
    )
      .should('contain.text', 'Basxet1')
      .click();

    cy.get('.distribution-targets-list__action-buttons--chooser').click();

    cy.visitWorkbasketsInformationPage();
    cy.visitWorkbasketsDistributionTargetsPage();

    cy.get(
      '#dual-list-Right > .distribution-targets-list > .mat-selection-list > .cdk-virtual-scroll-viewport > .cdk-virtual-scroll-content-wrapper > :nth-child(2)'
    ).should('contain.text', 'Basxet1');

    cy.undoWorkbaskets();
    cy.get(
      '#dual-list-Right > .distribution-targets-list > .mat-selection-list > .cdk-virtual-scroll-viewport > .cdk-virtual-scroll-content-wrapper'
    )
      .children()
      .should('have.length', 1);
  });

  it('should be possible to transfer distribution targets and save changes from another tab', () => {
    cy.visitTestWorkbasket();
    cy.visitWorkbasketsDistributionTargetsPage();

    cy.get(
      '#dual-list-Left > .distribution-targets-list > .mat-selection-list > .cdk-virtual-scroll-viewport > .cdk-virtual-scroll-content-wrapper > :nth-child(1)'
    )
      .should('contain.text', 'Basxet1')
      .click();

    cy.get('.distribution-targets-list__action-buttons--chooser').click();

    cy.visitWorkbasketsInformationPage();
    cy.saveWorkbaskets();
    cy.visitWorkbasketsDistributionTargetsPage();

    cy.get(
      '#dual-list-Right > .distribution-targets-list > .mat-selection-list > .cdk-virtual-scroll-viewport > .cdk-virtual-scroll-content-wrapper > :nth-child(2)'
    )
      .should('contain.text', 'Basxet1')
      .click();

    cy.get('.distribution-targets-list__action-buttons--selected').click();
    cy.saveWorkbaskets();
  });

  it('should be possible to change workbasket information and save changes from another tab', () => {
    cy.visitTestWorkbasket();

    cy.get('#wb-custom-4').clear().type(Cypress.env('testValueWorkbaskets'));

    cy.visitWorkbasketsDistributionTargetsPage();
    cy.saveWorkbaskets();

    cy.visitWorkbasketsInformationPage();
    cy.get('#wb-custom-4').should('have.value', Cypress.env('testValueWorkbaskets'));
  });

  it('should be possible to remove all distribution targets', () => {
    cy.visitTestWorkbasket();
    cy.visitWorkbasketsDistributionTargetsPage();

    cy.get('#dual-list-Right > .distribution-targets-list > .mat-toolbar > :nth-child(4)').click();
    cy.get('.distribution-targets-list__action-buttons--selected').click();

    cy.get(
      '#dual-list-Right > .distribution-targets-list > .mat-selection-list > .cdk-virtual-scroll-viewport > .cdk-virtual-scroll-content-wrapper'
    )
      .children()
      .should('have.length', 0);

    cy.undoWorkbaskets();
  });

  it('should filter selected distribution targets including newly transferred targets', () => {
    cy.visitTestWorkbasket();
    cy.visitWorkbasketsDistributionTargetsPage();

    cy.get('#dual-list-Right > .distribution-targets-list > .mat-toolbar > :nth-child(2)').click();
    cy.get(
      '#dual-list-Right > .distribution-targets-list > kadai-shared-workbasket-filter > .filter > .filter__expanded-filter > .filter__text-input > :nth-child(1) > :nth-child(2) > .mat-form-field-wrapper > .mat-form-field-flex > .mat-form-field-infix > .mat-input-element'
    )
      .clear()
      .type('002');
    cy.get('.filter__action-buttons > .filter__search-button').click();

    cy.get(
      '#dual-list-Right > .distribution-targets-list > .mat-selection-list > .cdk-virtual-scroll-viewport > .cdk-virtual-scroll-content-wrapper > :nth-child(1)'
    ).should('contain.text', 'Basxet1');

    cy.get('.filter__action-buttons > [mattooltip="Clear Workbasket filter"]').click();

    cy.get(
      '#dual-list-Right > .distribution-targets-list > .mat-selection-list > .cdk-virtual-scroll-viewport > .cdk-virtual-scroll-content-wrapper'
    )
      .children()
      .should('have.length', 2);
  });

  it('should filter available distribution targets', () => {
    cy.visitTestWorkbasket();
    cy.visitWorkbasketsDistributionTargetsPage();

    cy.get('#dual-list-Left > .distribution-targets-list > .mat-toolbar > :nth-child(2)').click();
    cy.get(
      '#dual-list-Left > .distribution-targets-list > kadai-shared-workbasket-filter > .filter > .filter__expanded-filter > .filter__text-input > :nth-child(1) > :nth-child(2) > .mat-form-field-wrapper > .mat-form-field-flex > .mat-form-field-infix > .mat-input-element'
    )
      .clear()
      .type('008');

    cy.get('.filter__action-buttons > .filter__search-button').click();

    cy.get(
      '#dual-list-Left > .distribution-targets-list > .mat-selection-list > .cdk-virtual-scroll-viewport > .cdk-virtual-scroll-content-wrapper'
    )
      .children()
      .should('have.length', 1);

    cy.get(
      '#dual-list-Left > .distribution-targets-list > .mat-selection-list > .cdk-virtual-scroll-viewport > .cdk-virtual-scroll-content-wrapper > :nth-child(1)'
    ).should('contain.text', 'BAsxet7');
  });
});
