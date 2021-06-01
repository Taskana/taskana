context('TASKANA Classifications', () => {
  beforeEach(() => cy.loginAs('admin'));

  it('should be possible to edit the service level of a classification', () => {
    const editedValue = 'P99D';

    cy.visitTestClassification();

    cy.get('#classification-service-level').clear({ force: true }).type(editedValue);
    cy.get('button').contains('Save').click({ force: true });

    cy.get('#classification-service-level').should('have.value', editedValue);
  });

  it('should be able to visit classifications and filter by manual', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
    cy.verifyPageLoad('/classifications');

    cy.get('button[mattooltip="Filter Category"]')
      .click({ force: true })
      .then(() => {
        cy.get('.mat-menu-content').contains('MANUAL').click();
        cy.get('tree-node-collection').find('tree-node').should('have.length', 8);
      });
  });

  it('should be possible to edit the name of a classification', () => {
    const editedValue = 'CY-TEST';

    cy.visitTestClassification();

    cy.get('#classification-name').scrollIntoView().clear().type(editedValue);
    cy.get('button').contains('Save').click();

    cy.get('#classification-name').should('have.value', editedValue);
  });

  it('should be possible to edit the category of a classification', () => {
    cy.visitTestClassification();

    cy.get('ng-form').find('mat-form-field mat-select[required]').click({ force: true });
    cy.wait(Cypress.env('dropdownWait'));
    cy.get('mat-option').contains('PROCESS').click({ force: true });
    cy.get('button').contains('Save').click({ force: true });

    // assure that its process now
    cy.get('ng-form').find('mat-form-field mat-select[required]').contains('PROCESS').should('be.visible');

    // change back to external
    cy.get('ng-form').find('mat-form-field mat-select[required]').click({ force: true });
    cy.wait(Cypress.env('dropdownWait'));
    cy.get('mat-option').contains('EXTERNAL').should('be.visible').click({ force: true });

    cy.get('button').contains('Save').click({ force: true });
  });

  it('should be possible to edit the description of a classification', () => {
    const editedValue = 'CY-TEST-DESC';

    cy.visitTestClassification();

    cy.get('#classification-description').clear({ force: true }).type(editedValue);
    cy.get('button').contains('Save').click({ force: true });

    cy.get('#classification-description').should('have.value', editedValue);
  });

  it('should be possible to edit the custom classification', () => {
    cy.visitTestClassification();

    cy.get('#classification-custom-1').clear({ force: true }).type(Cypress.env('testValueClassifications'));

    cy.get('button').contains('Save').click({ force: true });

    cy.get('#classification-custom-1').should('have.value', Cypress.env('testValueClassifications'));
  });

  it('should be possible to edit the application entry point', () => {
    cy.visitTestClassification();

    cy.get('#classification-application-entry-point')
      .clear({ force: true })
      .type(Cypress.env('testValueClassifications'));
    cy.get('button').contains('Save').click({ force: true });

    cy.get('#classification-application-entry-point').should('have.value', Cypress.env('testValueClassifications'));
  });
});
