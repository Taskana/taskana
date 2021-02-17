context('TASKANA Classifications', () => {
  beforeEach(() => cy.loginAs('admin'));

  it('should be possible to edit the Service Level of a classification', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
    cy.contains(Cypress.env('testValueClassificationSelectionName')).click();

    const editedValue = 'P99D';
    cy.get('#classification-service-level').clear().type(editedValue);
    cy.get('button').contains('Save').click();

    cy.reloadPageWithWait();

    cy.wait(Cypress.env('pageReload'));
    cy.get('#classification-service-level').should('have.value', editedValue);
  });


  it('should be able to visit Classifications and filter by manual', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
 
    cy.get('button[mattooltip="Filter Category"]').click()
    .then(() => {
      cy.get('.mat-menu-content').contains('MANUAL').click()
      cy.get('tree-node-collection').find('tree-node').should('have.length', 2);
    });
  });


  it('should be possible to edit the Name of a classification', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
    cy.contains(Cypress.env('testValueClassificationSelectionName')).click();

    const editedValue = 'CY-TEST';
    cy.get('#classification-name').clear().type(editedValue);
    cy.get('button').contains('Save').click();

    cy.reloadPageWithWait();

    cy.wait(Cypress.env('pageReload'));
    cy.get('#classification-name').should('have.value', editedValue);
  });


  it('should be possible to edit the Category of a classification', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
    cy.contains(Cypress.env('testValueClassificationSelectionName')).click();

    cy.get('ng-form').find('mat-form-field').find('mat-select[role="listbox"]').click();
 
    cy.wait(Cypress.env('dropdownWait'));

    cy.get('mat-option').contains('PROCESS').click();
    cy.get('button').contains('Save').click();
    
    cy.reloadPageWithWait();
   
    // assure that its process now
    cy.get('ng-form').find('mat-form-field').find('mat-select[role="listbox"]').contains('PROCESS').should('be.visible');
    // change back to external
    cy.get('ng-form').find('mat-form-field').find('mat-select[role="listbox"]').click();

    cy.wait(Cypress.env('dropdownWait'));

    cy.get('mat-option').contains('EXTERNAL').should('be.visible').click();
    cy.get('button').contains('Save').click();
  });

  
  it('should be possible to edit the Description of a classification', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
    cy.contains(Cypress.env('testValueClassificationSelectionName')).click();

    const editedValue = 'CY-TEST-DESC';
    cy.get('#classification-description').clear().type(editedValue);
    cy.get('button').contains('Save').click();

    cy.reloadPageWithWait();

    cy.wait(Cypress.env('pageReload'));
    cy.get('#classification-description').should('have.value', editedValue);
  });


  it('should be possible to edit classification custom 1 to 8', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
    cy.contains(Cypress.env('testValueClassificationSelectionName')).click();

    cy.wrap([1, 2, 3, 4, 5, 6, 7]).each((index) => {
      cy.get('#classification-custom-' + index)
        .clear()
        .type(Cypress.env('testValueClassifications'));

      cy.get('button').contains('Save').click();

      cy.reloadPageWithWait();

      cy.wait(Cypress.env('pageReload'));
      cy.get('#classification-custom-' + index).should('have.value', Cypress.env('testValueClassifications'));
    });
  });


  it('should be possible to edit the application entry point', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/classifications');
    cy.contains(Cypress.env('testValueClassificationSelectionName')).click();

    cy.get('#classification-application-entry-point').clear().type(Cypress.env('testValueClassifications'));
    cy.get('button').contains('Save').click();

    cy.reloadPageWithWait();

    cy.wait(Cypress.env('pageReload'));
    cy.get('#classification-application-entry-point').should('have.value', Cypress.env('testValueClassifications'));
  });

});


