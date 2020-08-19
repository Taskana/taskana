context('TASKANA History', () => {
  beforeEach(() => cy.loginAs('admin'));

  it('should display the history', () => {
    cy.visit(Cypress.env('appUrl') + '/history');
    cy.get('.table > form > .table-body > .table-row').should('have.length.greaterThan', 10);
  });
});
