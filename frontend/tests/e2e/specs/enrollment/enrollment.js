describe('Enrollment', () => {
    beforeEach(() => {
        cy.deleteAllButArs();
        cy.createEnrollmentsDemoEntities();
    });

    afterEach(() => {
        cy.deleteAllButArs();
    });

    it('create enrollment', () => {

        cy.intercept('GET', '/users/*/getInstitution').as('getInstitution');
    

        cy.demoMemberLogin()

        // Get the activities
        cy.get('[data-cy="institution"]').click();
        cy.get('[data-cy="activities"]').click();
        cy.wait('@getInstitution');

        // Check if there are 3 activities
        cy.get('[data-cy="memberActivitiesTable"] tbody tr').should('have.length', 3);

        cy.logout();
        
    });
});