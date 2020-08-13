context('TASKANA Workbaskets', () => {
  beforeEach(() => cy.loginAs('admin'));

  it('should be able to see all Workbaskets', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/workbaskets');
    cy.location().should((location) => {
      expect(location.href).to.eq(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/workbaskets');
    });
    // should contain #wb-list-container
  });

  it('should be able to filter workbaskets via owner', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('adminUrl') + '/workbaskets').then(() =>
      cy.get('#collapsedMenufilterWb').click()
    );

    cy.get('[placeholder="Filter owner"]')
      .type('owner0815')
      .type('{enter}')
      .then(() => {
        // Length equal to 2 because the empty starting element of the list, only one ListEntry with values added
        cy.get('#wb-list-container').find('.list-group-item').should('have.length', 2);
      });
  });

  it('should be possible to edit workbasket information custom 1 to 4', () => {
    cy.visitTestWorkbasket();

    cy.wrap([1, 2, 4]).each((index) => {
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
      cy.get('#wb-org-level-' + index)
        .clear()
        .type(Cypress.env('testValueWorkbaskets'));

      cy.saveWorkbaskets();
      cy.reloadPageWithWait();

      cy.get('#wb-org-level-' + index).should('have.value', Cypress.env('testValueWorkbaskets'));
    });
  });

  it('should be possible to edit workbasket description', () => {
    cy.visitTestWorkbasket();

    cy.get('#wb-description')
      .clear()
      .type(Cypress.env('testValueWorkbaskets'))
      .then(() => {
        cy.saveWorkbaskets();
        cy.reloadPageWithWait();
        cy.get('#wb-description').should('have.value', Cypress.env('testValueWorkbaskets'));
      });
  });

  it('should be possible to edit the Type of a workbasket', () => {
    cy.visitTestWorkbasket();

    cy.get('#dropdownMenu24').click();

    cy.wait(Cypress.env('dropdownWait'));

    cy.get('.col-xs-4 > .dropdown > .dropdown-menu > li > ').contains('Clearance').click();

    cy.saveWorkbaskets();

    cy.reloadPageWithWait();

    // assure that its process now

    cy.get('#dropdownMenu24').contains('Clearance').should('be.visible');

    // change back to external

    cy.get('#dropdownMenu24').click();

    cy.wait(Cypress.env('dropdownWait'));

    cy.get('.col-xs-4 > .dropdown > .dropdown-menu > li > ').contains('Group').should('be.visible').click();

    cy.saveWorkbaskets();
  });

  it('should be possible to add new access', () => {
    cy.visitTestWorkbasket();
    cy.visitWorkbasketsAccessPage();

    cy.get('[title="Add new access"]')
      .click()
      .then(() => {
        cy.get(
          '.ng-invalid.ng-star-inserted > .text-align > > .custom-form-control > :nth-child(2) > .input-group > .form-control'
        )
          // .contains("Access id is required")
          .type('teamlead-2');
        cy.get('.input-group > .dropdown > .dropdown-menu >').click();
        cy.saveWorkbaskets();
      });
    cy.reloadPageWithWait();
    cy.visitWorkbasketsAccessPage();
    cy.get('table#table-access-items > tbody > tr').should('have.length', 2);
  });

  it('should be possible to add a distribution target', () => {
    cy.server();
    cy.route(
      'http://localhost:8080/taskana/api/v1/workbaskets/WBI:000000000000000000000000000000000900/distribution-targets'
    ).as('workbasketsDistributionTargets');

    cy.visitTestWorkbasket();
    cy.visitWorkbasketsDistributionTargetsPage();
    cy.get('#dual-list-Left > .dual-list.list-left > .infinite-scroll > .list-group > :nth-child(1)').click();
    cy.get('.list-arrows > .move-right').contains('chevron_right').click();
    cy.saveWorkbaskets();
    cy.reloadPageWithWait();
    cy.visitWorkbasketsDistributionTargetsPage();
    cy.wait('@workbasketsDistributionTargets');
    cy.get('#dual-list-right > .dual-list.list-left > .infinite-scroll > .list-group').should('have.length', 1);
    cy.get('#dual-list-right > .dual-list.list-left > .infinite-scroll > .list-group')
      .contains('owner0815')
      .should('exist');
  });
});
