package au.org.thebigissue.rostering.input;

import au.org.thebigissue.rostering.solver.solution.Roster;

import static org.junit.jupiter.api.Assertions.*;

class RosteringGeneratorTest {

    @org.junit.jupiter.api.Test
    void createRoster() {

        RosteringGenerator tester= new RosteringGenerator();
        Roster roster=tester.createRoster();
        System.out.println(roster.getWorkshopList().get(0).getDate());
        assertTrue(Roster.class.equals(roster.getClass()));

    }

}