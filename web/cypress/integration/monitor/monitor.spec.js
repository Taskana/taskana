context.skip('KADAI Monitor', () => {
  beforeEach(() => cy.loginAs('admin'));

  it('should visit kadai tasks by status monitor page', () => {
    cy.intercept('**/monitor/task-status-report*').as('monitorData');
    cy.visitMonitor();
    cy.get('nav').find('a').contains('Tasks by Status').click();
    cy.verifyPageLoad('/tasks-status');
    cy.get('nav').find('.mat-tab-label-active').should('contain', 'Tasks by Status');
    cy.wait('@monitorData');
    cy.get('canvas.chartjs-render-monitor').should('be.visible');
  });

  it('should visit kadai tasks by priority monitor page', () => {
    cy.intercept('**/monitor/workbasket-priority-report*').as('monitorData');
    cy.visitMonitor();
    cy.get('nav').find('a').contains('Tasks by Priority').click();
    cy.verifyPageLoad('/tasks-priority');
    cy.get('nav').find('.mat-tab-label-active').should('contain', 'Tasks by Priority');
    cy.wait('@monitorData');
    cy.get('canvas.chartjs-render-monitor').should('be.visible');
  });

  it('should visit kadai workbaskets monitor page', () => {
    cy.intercept('**/monitor/workbasket-report*').as('monitorData');
    cy.visitMonitor();
    cy.get('nav').find('a').contains('Workbaskets').click();
    cy.verifyPageLoad('/workbaskets');
    cy.get('nav').find('.mat-tab-label-active').should('contain', 'Workbaskets');
    cy.wait('@monitorData');
    cy.get('canvas.chartjs-render-monitor').should('be.visible');
  });

  it('should visit kadai classifications monitor page', () => {
    cy.intercept('**/monitor/classification-report').as('monitorData');
    cy.visitMonitor();
    cy.get('nav').find('a').contains('Classifications').click();
    cy.verifyPageLoad('/classifications');
    cy.get('nav').find('.mat-tab-label-active').should('contain', 'Classifications');
    cy.wait('@monitorData');
    cy.get('canvas.chartjs-render-monitor').should('be.visible');
  });

  it('should visit kadai timestamp monitor page', () => {
    cy.intercept('**/monitor/timestamp*').as('monitorData');
    cy.visitMonitor();
    cy.get('nav').find('a').contains('Timestamp').click();
    cy.verifyPageLoad('/timestamp');
    cy.get('nav').find('.mat-tab-label-active').should('contain', 'Timestamp');
    cy.wait('@monitorData');
    cy.contains('TimestampReport').should('be.visible');
  });
});
