context('TASKANA History', () => {
  beforeEach(() => cy.loginAs('admin'));

  it('should display the history', () => {
    if (Cypress.env('isHistoryEnabled')) {
      cy.visit(Cypress.env('appUrl') + '/history');
      cy.verifyPageLoad('/history');

      cy.get('table').find('tr').should('have.length.greaterThan', 8);
    } else {
      cy.log('History plugin not enabled - No need for testing history functionality');
    }
  });
});
