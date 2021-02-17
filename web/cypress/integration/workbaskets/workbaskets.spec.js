context('TASKANA Workbaskets', () => {
  beforeEach(() => cy.loginAs('admin'));

  it('should be able to see all Workbaskets', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/workbaskets');
    cy.location().should((location) => {
      expect(location.href).to.eq(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/workbaskets');
    });
  });

  it('be able to filter workbaskets via owner', () => { 
    cy.visitTestWorkbasket();

    cy.get('button[mattooltip="Display more filter options"]').click()
    cy.wait(Cypress.env('dropdownWait'));
      
    cy.get('mat-form-field').find('input[mattooltip="Type to filter by owner"]')
      .type('user-1-2')
      .type('{enter}')
      .then(() => {
        // Length equal to 1 because the empty starting element of the list, only one ListEntry with values added
        cy.get('mat-selection-list[role="listbox"]').should('have.length', 1);
      }); 

  });

  it('should be possible to edit workbasket information custom 1 to 3', () => {
    cy.visitTestWorkbasket();

    cy.wrap([1, 2, 3]).each((index) => {
      cy.get('#wb-custom-' + index)
        .clear()
        .type(Cypress.env('testValueWorkbaskets'));

      cy.saveWorkbaskets();
      cy.reloadPageWithWait();

      cy.get('#wb-custom-' + index).should('have.value', Cypress.env('testValueWorkbaskets'));
    });
  });

  it('should be possible to edit workbasket information OrgLevel 1 to 4', () => {
    cy.visitTestWorkbasket();

    cy.wrap([1, 2, 3, 4]).each((index) => {
      cy.get('input[name="workbasket.orgLevel' + index + '"]')
        .clear()
        .type(Cypress.env('testValueWorkbaskets'));

      cy.saveWorkbaskets();
      cy.reloadPageWithWait();

      cy.get('input[name="workbasket.orgLevel' + index + '"]').should('have.value', Cypress.env('testValueWorkbaskets'));
    });
  });


  it('should be possible to edit workbasket description', () => {
    cy.visitTestWorkbasket();

    cy.get('#workbasket-description')
      .clear()
      .type(Cypress.env('testValueWorkbaskets'))
      .then(() => {
        cy.saveWorkbaskets();
        cy.reloadPageWithWait();
        cy.get('#workbasket-description').should('have.value', Cypress.env('testValueWorkbaskets'));
      });
  });
  
  it('should be possible to edit the Type of a workbasket', () => {
    cy.visitTestWorkbasket();

    cy.get('mat-form-field').contains('mat-form-field', 'Type').find('mat-select').click();
    cy.wait(Cypress.env('dropdownWait'));
    cy.get('mat-option').contains('Clearance').click();
    cy.saveWorkbaskets();
   
    cy.reloadPageWithWait();
    // secure longer response times
    cy.wait(Cypress.env('pageReload'));

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
        cy.get('mat-form-field').contains('mat-form-field', "Access id").find('input')
          .type('teamlead-2');
        cy.get('input[aria-label="checkAll"]:first').click();
        cy.saveWorkbaskets();
      });

    cy.reloadPageWithWait();

    cy.visitWorkbasketsAccessPage();
    cy.get('table#table-access-items > tbody > tr').should('have.length', 1);
  });

  it('should be possible to add a distribution target', () => {
    cy.visitTestWorkbasket();
    cy.visitWorkbasketsDistributionTargetsPage();

    cy.get('taskana-administration-workbasket-distribution-targets-list[header="Available distribution targets"]').find('mat-list-option:first')
      .find('mat-pseudo-checkbox').click()
    cy.get('button').contains('Add selected distribution targets').click()

    cy.saveWorkbaskets();
    
    cy.wait(Cypress.env('pageReload'));
    // secure longer response times
    cy.reloadPageWithWait();
    
    cy.visitWorkbasketsDistributionTargetsPage();
    cy.get('taskana-administration-workbasket-distribution-targets-list[header="Selected distribution targets"]').find('mat-selection-list').should('not.be.empty');

    // undo changes
    cy.get('taskana-administration-workbasket-distribution-targets-list[header="Selected distribution targets"]').find('mat-list-option:first')
      .find('mat-pseudo-checkbox').click()
    cy.get('button').contains('Remove selected distribution target').click()

    cy.saveWorkbaskets();
    cy.reloadPageWithWait();
  });

});
