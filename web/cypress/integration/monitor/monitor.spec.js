context('TASKANA Monitor', () => {
  beforeEach(() => cy.loginAs('admin'));

  it('should visit taskana tasks monitor page', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('monitorUrl'));
    cy.location().should((location) => {
      expect(location.href).to.eq(Cypress.env('appUrl') + Cypress.env('monitorUrl'));
    });

    cy.get('li.active').find('> a').should('have.text', 'Tasks');

    cy.get('.col-xs-12 > .chartjs-render-monitor').should('be.visible');
  });

  it('should visit taskana workbaskets monitor page', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('monitorUrl'));
    cy.location().should((location) => {
      expect(location.href).to.eq(Cypress.env('appUrl') + Cypress.env('monitorUrl'));
    });

    cy.get('.nav a').contains('Workbaskets').click();

    cy.get('li.active').find('> a').should('have.text', 'Workbaskets');

    cy.get('.panel > .panel-body').should('be.visible');

    cy.get('.row > .col-xs-12 > > .chartjs-render-monitor').should('be.visible');
  });

  it('should visit taskana classifications monitor page', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('monitorUrl'));
    cy.location().should((location) => {
      expect(location.href).to.eq(Cypress.env('appUrl') + Cypress.env('monitorUrl'));
    });

    cy.get('.nav a').contains('Classifications').click();

    cy.get('li.active').find('> a').should('have.text', 'Classifications');

    cy.get('.panel > .panel-body').should('be.visible');

    cy.get('.panel-body > .row > .col-xs-12 > [style="display: block;"] > .chartjs-render-monitor').should(
      'be.visible'
    );
  });

  it('should visit taskana timestamp monitor page', () => {
    cy.visit(Cypress.env('appUrl') + Cypress.env('monitorUrl'));
    cy.location().should((location) => {
      expect(location.href).to.eq(Cypress.env('appUrl') + Cypress.env('monitorUrl'));
    });

    cy.get('.nav a').contains('Timestamp').click();

    cy.get('li.active').find('> a').should('have.text', 'Timestamp');

    cy.get('.panel > .panel-body').should('be.visible');

    cy.get(
      '.panel > .panel-body > taskana-monitor-report-table > .report > .table-header > .table-row > .table-cell--justify'
    ).should('be.visible');
  });
});
