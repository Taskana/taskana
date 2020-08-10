context('TASKANA Classifications', () => {
  beforeEach(() => cy.loginAs('admin'));

  it('should be able to visit Classifications and filter by manual', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');

    cy.get('#dropdown-classification-filter')
      .click()
      .then(() => {
        cy.contains('MANUAL').click();

        cy.get('tree-node-collection').find('tree-node').should('have.length', 2);
      });
  });

  it('should be possible to edit the Name of a classification', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
    cy.contains(Cypress.env('testValueClassificationSelectionName')).click();

    const editedValue = 'CY-TEST';

    cy.get('#classification-name').clear().type(editedValue);

    cy.get('[title="Save"] > .material-icons').click();

    cy.reload();

    cy.wait(Cypress.env('pageReload'));

    cy.get('#classification-name').should('have.value', editedValue);
  });

  it('should be possible to edit the Priority of a classification', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
    cy.contains(Cypress.env('testValueClassificationSelectionName')).click();
    cy.get('input[name="number"]')
      .invoke('val')
      .then((oldPriorityValue) => {
        oldPriorityValue = parseFloat(oldPriorityValue);
        cy.get('[title="increase value"] > .material-icons').click();
        cy.get('[title="Save"] > .material-icons').click();
        cy.reload();
        cy.wait(Cypress.env('pageReload'));
        cy.get('.input-group > .form-control')
          .invoke('val')
          .then((newValueOfPriority) => {
            newValueOfPriority = parseFloat(newValueOfPriority);
            expect(newValueOfPriority).to.eq(oldPriorityValue + 1);
          });
      });
  });

  it('should be possible to edit the Category of a classification', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
    cy.contains(Cypress.env('testValueClassificationSelectionName')).click();

    cy.get('.required > .dropdown > .btn').click();

    cy.wait(Cypress.env('dropdownWait'));

    cy.get('.dropdown-menu.show > li').contains('PROCESS').click();

    cy.get('[title="Save"] > .material-icons').click();

    cy.reload();

    cy.wait(Cypress.env('pageReload'));

    // assure that its process now

    cy.get('.required > .dropdown > .btn').contains('PROCESS').should('be.visible');

    // change back to external

    cy.get('.required > .dropdown > .btn').click();

    cy.wait(Cypress.env('dropdownWait'));

    cy.get('.dropdown-menu.show > li').contains('EXTERNAL').should('be.visible').click();

    cy.get('[title="Save"] > .material-icons').click();
  });

  it('should be possible to edit the Description of a classification', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
    cy.contains(Cypress.env('testValueClassificationSelectionName')).click();

    const editedValue = 'CY-TEST-DESC';

    cy.get('#classification-description').clear().type(editedValue);

    cy.get('[title="Save"] > .material-icons').click();

    cy.reload();

    cy.wait(Cypress.env('pageReload'));

    cy.get('#classification-description').should('have.value', editedValue);
  });

  it('should be possible to edit the Service Level of a classification', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
    cy.contains(Cypress.env('testValueClassificationSelectionName')).click();

    const editedValue = 'P99D';

    cy.get('#classification-service-level').clear().type(editedValue);

    cy.get('[title="Save"] > .material-icons').click();

    cy.reload();

    cy.wait(Cypress.env('pageReload'));

    cy.get('#classification-service-level').should('have.value', editedValue);
  });

  it('should be possible to edit classification custom 1 to 8', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
    cy.contains(Cypress.env('testValueClassificationSelectionName')).click();

    cy.wrap([1, 2, 4, 5, 6, 7, 8]).each((index) => {
      cy.get('#classification-custom-' + index)
        .clear()
        .type(Cypress.env('testValueClassifications'));

      cy.get('[title="Save"] > .material-icons').click();

      cy.reload();

      cy.wait(Cypress.env('pageReload'));

      cy.get('#classification-custom-' + index).should('have.value', Cypress.env('testValueClassifications'));
    });
  });

  it('should be possible to edit the application entry point', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
    cy.contains(Cypress.env('testValueClassificationSelectionName')).click();

    cy.get('#classification-application-entry-point').clear().type(Cypress.env('testValueClassifications'));

    cy.get('[title="Save"] > .material-icons').click();

    cy.reload();

    cy.wait(Cypress.env('pageReload'));

    cy.get('#classification-application-entry-point').should('have.value', Cypress.env('testValueClassifications'));
  });
});
