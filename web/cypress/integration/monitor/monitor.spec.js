context('TASKANA Monitor', () => {
  beforeEach(() => cy.loginAs('admin'));

  it('should visit taskana tasks monitor page', () => {
    cy.visit(Cypress.env('appUrl') + '/monitor');
    cy.verifyPageLoad('/monitor');

    cy.get('nav').find('.mat-tab-label-active').should('contain', 'Tasks');
    cy.get('canvas.chartjs-render-monitor').should('be.visible');
  });


  it('should visit taskana workbaskets monitor page', () => {
    cy.visit(Cypress.env('appUrl') + '/monitor');
    cy.verifyPageLoad('/monitor');

    cy.get('nav').find('a').contains('Workbaskets').click();
    cy.get('nav').find('.mat-tab-label-active').should('contain', 'Workbaskets');
    cy.get('canvas.chartjs-render-monitor').should('be.visible');
  });


  it('should visit taskana classifications monitor page', () => {
    cy.visit(Cypress.env('appUrl') + '/monitor');
    cy.verifyPageLoad('/monitor');

    cy.get('nav').find('a').contains('Classifications').click();
    cy.get('nav').find('.mat-tab-label-active').should('contain', 'Classifications');
    cy.get('canvas.chartjs-render-monitor').should('be.visible');
  });


  it('should visit taskana timestamp monitor page', () => {
    cy.visit(Cypress.env('appUrl') + '/monitor');
    cy.verifyPageLoad('/monitor');

    cy.get('nav').find('a').contains('Timestamp').click();
    cy.get('nav').find('.mat-tab-label-active').should('contain', 'Timestamp');
    cy.contains('TimestampReport').should('be.visible');
  });

});
