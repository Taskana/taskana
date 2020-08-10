context('TASKANA Login', () => {
  if (Cypress.env('isLocal')) {
    it('should not be run because its local development', () => {
      cy.log('Local development - No need for testing login functionality');
      expect(true).to.be.true;
    });
  } else {
    it('should redirect to login when not logged in yet', () => {
      cy.visit(Cypress.env('baseUrl') + '/taskana/workplace');

      cy.location().should((location) => {
        expect(location.href).to.eq(Cypress.env('baseUrl') + '/login');
      });
    });

    it('should login via taskana login page', () => {
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
  }
});
