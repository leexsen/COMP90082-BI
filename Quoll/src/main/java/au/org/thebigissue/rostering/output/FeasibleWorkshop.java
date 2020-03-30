package au.org.thebigissue.rostering.output;

//import au.org.thebigissue.rostering.solver.AbstractPersistable;

//This class is designed to contain the workshop information and some information on the feasibility and hard
//Score. It's used by WordOutput to print green rosters for feasible rosters and red rosters for infeasible workshops
//Perhaps could be refactored by being placed in entities, yet this class is not used when creating a roster solution

import au.org.thebigissue.rostering.solver.entities.Workshop;

public class FeasibleWorkshop {

    Workshop workshop;

    int hardScore;

    boolean feasible;

    public FeasibleWorkshop(Workshop workshop, boolean feasible, int hardScore) {

        this. workshop = workshop;
        this.feasible = feasible;
        this.hardScore = hardScore;

    }

    public Workshop getWorkshop() {

        return workshop;

    }

    public int getHardScore() {

        return hardScore;

    }

    public boolean isFeasible() {

        return feasible;

    }

}
