package au.org.thebigissue.rostering.output;

class PDFOutputTest {



   /* @BeforeEach
    void setUp(){

        //create a roster here
        Roster roster=new Roster();
        List<Workshop> workshopList=new ArrayList<Workshop>();
        Workshop workshop=new Workshop(1,"Harvard","HTML","NewYork","somewhere","4","20","Jun","303@qq.com","837291837","super workshop",2,3,3,4, LocalDate.of(2019,2,1), LocalTime.of(3,20),LocalTime.of(5,30));
        workshopList.add(workshop);
        roster.setWorkshopList(workshopList);

        ArrayList<Staff> facilitatorList=new ArrayList<Staff>();
        Facilitator staff=new Facilitator("Jones","William",new Availability(),true);
        facilitatorList.add(staff);
        ArrayList<Staff> guestSpeakerList=new ArrayList<Staff>();
        GuestSpeaker guestSpeaker=new GuestSpeaker("Bryant","Kobe",new Availability(),true,5);
        guestSpeakerList.add(guestSpeaker);
        roster.setShiftList(facilitatorList,guestSpeakerList,LocalDate.of(2019,2,5), LocalDate.of(2019,3,10));

        List<FacilitatorShift> facilitatorShiftList=new ArrayList<FacilitatorShift>();
        FacilitatorShift facilitatorShift=new FacilitatorShift(1,new Facilitator("Jones","William",new Availability(),true), LocalDate.of(2019,2,3));
        roster.setFacilitatorShiftList(facilitatorShiftList);

        // more things to set up for a roster
//        roster.setGuestSpeakerShiftList();

    }

    *//*
        java.lang.IllegalArgumentException:The solutionClass (class au.org.thebigissue.rostering.solver.solution.Roster)'s factCollectionProperty (bean property facilitatorShiftList on class au.org.thebigissue.rostering.solver.solution.Roster) should never return null.
        Maybe the getter/method always returns null instead of the actual data.
        Maybe that property (facilitatorShiftList) was set with null instead of an empty collection/array when the class (Roster) instance was created.
    *//*
    @Test
    void printGeneralRoster() {

        // Build the Solver
        SolverFactory<Roster> solverFactory = SolverFactory.createFromXmlResource(
                "au/org/thebigissue/rostering/solver/rosteringSolverConfig.xml");
        Solver<Roster> solver = solverFactory.buildSolver();

        // Load the problem
        Roster unsolvedRoster = new RosteringGenerator().createRoster();

        // Import the problem
//        Roster unsolvedRoster = new RosteringGenerator().createRoster();


        //java.lang.System.exit(0);

        // Solve the problem
        Roster solvedRoster = solver.solve(unsolvedRoster);

        // Display the result
        System.out.println("\nSolved roster:\n\n");
        System.out.println(solver.explainBestScore());
//        printResult(solvedRoster);

        // Display shifts
        for (FacilitatorShift shift : solvedRoster.getFacilitatorShiftList()) {
            shift.printShift();
        }
        for (GuestSpeakerShift shift : solvedRoster.getGuestSpeakerShiftList()) {
            shift.printShift();
        }


        System.out.println("------------start printing Roster to PDF--------------");
        try {
            PDFOutput.printGeneralRoster(solvedRoster);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("-------------Ends printing Roster to PDF--------------");




    }


    *//*
        WARNING: An illegal reflective access operation has occurred
        WARNING: Illegal reflective access by com.thoughtworks.xstream.core.util.Fields (file:/C:/Users/Junkai/.m2/repository/com/thoughtworks/xstream/xstream/1.4.11.1/xstream-1.4.11.1.jar) to field java.util.TreeMap.comparator
        WARNING: Please consider reporting this to the maintainers of com.thoughtworks.xstream.core.util.Fields
        WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
        WARNING: All illegal access operations will be denied in a future release
    *//*
    @Test
    void printRosterFacilitator() throws IOException {

        // Build the Solver
        SolverFactory<Roster> solverFactory = SolverFactory.createFromXmlResource(
                "au/org/thebigissue/rostering/solver/rosteringSolverConfig.xml");
        Solver<Roster> solver = solverFactory.buildSolver();

        // Load the problem
        Roster unsolvedRoster = new RosteringGenerator().createRoster();

        // Import the problem
//        Roster unsolvedRoster = new RosteringGenerator().createRoster();


        //java.lang.System.exit(0);

        // Solve the problem
        Roster solvedRoster = solver.solve(unsolvedRoster);

        // Display the result
        System.out.println("\nSolved roster:\n\n");
        System.out.println(solver.explainBestScore());
//        printResult(solvedRoster);

        // Display shifts
        for (FacilitatorShift shift : solvedRoster.getFacilitatorShiftList()) {
            shift.printShift();
        }
        for (GuestSpeakerShift shift : solvedRoster.getGuestSpeakerShiftList()) {
            shift.printShift();
        }


        *//*System.out.println("------------start printing Roster to PDF--------------");
        try {
            PDFOutput.printGeneralRoster(solvedRoster);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("-------------Ends printing Roster to PDF--------------");*//*


        System.out.println("------------start printing Roster to PDF facilitator--------------");
         *//*for (FacilitatorShift shift : solvedRoster.getFacilitatorShiftList()) {
            shift.printShiftToPDF();
        }*//*

        PDFOutput.printRosterFacilitator(solvedRoster);
        System.out.println("-------------Ends printing Roster to PDF facilitator--------------");


    }



    @Test
    void printRosterGuestSpeakerGroupByName() {
    }

    @Test
    void printGuestSpeakerShiftAndWorkshop() {
    }

    @Test
    void printRosterGuestSpeakerShift() {
    }

    @Test
    void printGuestSpeakerShiftAndWorkshopsForAll() {
    }

    @Test
    void printGuestShift() {
    }

    @Test
    void printWorkshopForGuestSpeaker() {
    }

    @Test
    void printGuestSpeaker() {
    }

    @Test
    void printForSpecifiedRolesProcedure() {
    }

    @Test
    void revertPages() {
    }*/

}