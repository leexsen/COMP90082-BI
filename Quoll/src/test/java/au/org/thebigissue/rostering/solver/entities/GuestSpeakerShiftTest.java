package au.org.thebigissue.rostering.solver.entities;

class GuestSpeakerShiftTest {

    GuestSpeakerShift guestSpeakerShift;

    /*@BeforeEach
    void setup(){
        guestSpeakerShift=new GuestSpeakerShift(1,new GuestSpeaker("Jones","William",new Availability(),true,5), LocalDate.of(2019,2,2));
    }


    @Test
    void getWorkshopList() {

        GuestSpeaker guestSpeaker=guestSpeakerShift.getStaff();
        System.out.println(guestSpeaker.getName());
        assertTrue(guestSpeaker.getName().equals("Jones William"));

    }

    @Test
    void getStaff() {

        List<Workshop> workshopList = guestSpeakerShift.getWorkshopList();
        for (Workshop ws: workshopList){
            ws.printWorkshop();
        }

    }

    @Test
    void updateUtilisation() {

        guestSpeakerShift.updateUtilisation();
        System.out.println(guestSpeakerShift.getUtilisation());
        assertTrue(guestSpeakerShift.getUtilisation()==0);

    }

    @Test
    void updateUtilisationTC1() {

        List<Workshop> workshopList=new ArrayList<Workshop>();
        Workshop workshop=new Workshop(1,"Harvard","HTML","NewYork","somewhere","4","20","Jun","303@qq.com","837291837","super workshop",2,3,3,4,LocalDate.of(2019,2,1), LocalTime.of(3,20),LocalTime.of(5,30));
        workshopList.add(workshop);
        guestSpeakerShift.setWorkshopList(workshopList);
        guestSpeakerShift.updateUtilisation();
        System.out.println(guestSpeakerShift.getUtilisation());
//        assertTrue(guestSpeakerShift.getUtilisedTime()==130);

    }

    @Test
    void updateUtilisedTime() {

        guestSpeakerShift.updateUtilisedTime();
        System.out.println(guestSpeakerShift.getUtilisedTime());
        assertTrue(guestSpeakerShift.getUtilisedTime()==0);

    }

    @Test
    void updateUtilisedTimeTC1() {

        List<Workshop> workshopList=new ArrayList<Workshop>();
        Workshop workshop=new Workshop(1,"Harvard","HTML","NewYork","somewhere","4","20","Jun","303@qq.com","837291837","super workshop",2,3,3,4,LocalDate.of(2019,2,1), LocalTime.of(3,20),LocalTime.of(5,30));
        workshopList.add(workshop);
        guestSpeakerShift.setWorkshopList(workshopList);
        guestSpeakerShift.updateUtilisedTime();
        System.out.println(guestSpeakerShift.getUtilisedTime());
        assertTrue(guestSpeakerShift.getUtilisedTime()==130);

    }*/

}