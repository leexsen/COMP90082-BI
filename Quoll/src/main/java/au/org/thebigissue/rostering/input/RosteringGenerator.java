package au.org.thebigissue.rostering.input;

import au.org.thebigissue.rostering.solver.entities.Workshop;
import au.org.thebigissue.rostering.solver.solution.Roster;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates the input data for the roster.
 */
public class RosteringGenerator {

    public Roster createRoster() {

        Roster roster = new Roster();
        roster.setId(0L);

        roster.setWorkshopList(generateWorkshops());

        ArrayList<String> facilitators = generateFacilitators();
        ArrayList<String> guestSpeakers = generateGuestSpeakers();

//        roster.setShiftList(facilitators, guestSpeakers);
        return roster;
    }

    private ArrayList<String> generateFacilitators() {
        ArrayList<String> facilitatorList = new ArrayList<>();/*
        facilitatorList.add(new Facilitator(0,"AA", "11111111110000"));
        facilitatorList.add(new Facilitator(1,"MN", "11111111110000"));*/
        facilitatorList.add("AA");
        facilitatorList.add("MN");
        return facilitatorList;
    }

    private ArrayList<String> generateGuestSpeakers() {
        ArrayList<String> guestSpeakerList = new ArrayList<>();/*
        guestSpeakerList.add(new GuestSpeaker(0,"Cheryl", "11111111110000"));
        guestSpeakerList.add(new GuestSpeaker(1,"Brian", "11111111110000"));*/
        guestSpeakerList.add("Cheryl");
        guestSpeakerList.add("Brian");
        guestSpeakerList.add("Loriner");
        return guestSpeakerList;
    }

    private List<Workshop> generateWorkshops() {
        List<Workshop> workshopList = new ArrayList<>();
        workshopList.add(new Workshop(0,"Penola Catholic College", "DHD", "Collins St", 2018, 6, 4, 9, 30, 60));
        workshopList.add(new Workshop(1,"Knox Park Primary School", "P456", "DWH", 2018, 6, 4, 11, 0, 60));
        workshopList.add(new Workshop(2,"Marist Sion College Warragul", "DHD", "Collins St", 2018, 6, 4, 11, 30, 60));
        workshopList.add(new Workshop(3,"Knox Park Primary School", "P456", "DWH", 2018, 6, 4, 12, 0, 60));
        workshopList.add(new Workshop(4,"City Cite - Lauriston Girls' School", "DHD", "Collins St", 2018, 6, 4, 14, 0, 60));
        workshopList.add(new Workshop(5,"Doxa Yarram St Mary's", "P456", "Collins St", 2018, 6, 4, 16, 0, 60));
        workshopList.add(new Workshop(7,"Penola Catholic College", "DHD", "Collins St", 2018, 6, 5, 10, 0, 60));
        workshopList.add(new Workshop(8,"City Cite - Lauriston Girls' School", "DHD", "Collins St", 2018, 6, 5, 11, 0, 60));
        workshopList.add(new Workshop(9,"City Cite - Lauriston Girls' School", "DHD", "Collins St", 2018, 6, 5, 13, 0, 60));
        workshopList.add(new Workshop(10,"Penola Catholic College", "DHD", "DWH", 2018, 6, 5, 13, 0, 60));
        workshopList.add(new Workshop(11,"Balwyn High School", "DHD", "Collins St", 2018, 6, 6, 9, 0, 60));
        workshopList.add(new Workshop(12,"Balwyn High School", "DHD", "DWH", 2018, 6, 6, 9, 0, 60));
        workshopList.add(new Workshop(13,"Penola Catholic College", "DHD", "Collins St", 2018, 6, 6, 10, 0, 60));
        workshopList.add(new Workshop(14,"City Cite - Lauriston Girls' School", "DHD", "Collins St", 2018, 6, 6, 11, 0, 60));
        workshopList.add(new Workshop(15,"Penola Catholic College", "DHD", "Collins St", 2018, 6, 6, 13, 0, 60));
        workshopList.add(new Workshop(16,"Balwyn High School", "DHD", "Collins St", 2018, 6, 6, 14, 0, 60));
        workshopList.add(new Workshop(17,"Balwyn High School", "DHD", "DWH", 2018, 6, 6, 14, 0, 60));
        workshopList.add(new Workshop(18,"Peninsula Grammar", "DHD", "Collins St", 2018, 6, 7, 10, 0, 60));
        workshopList.add(new Workshop(19,"OLMC Heidelberg", "DHD", "Collins St", 2018, 6, 7, 11, 0, 60));
        workshopList.add(new Workshop(20,"Peninsula Grammar", "DHD", "Collins St", 2018, 6, 7, 13, 0, 60));
        workshopList.add(new Workshop(21,"Penola Catholic College", "DHD", "DWH", 2018, 6, 7, 13, 0, 60));
        workshopList.add(new Workshop(22,"Mentone Grammar", "DHD", "Collins St", 2018, 6, 8, 9, 0, 60));
        workshopList.add(new Workshop(23,"Penola Catholic College", "DHD", "Collins St", 2018, 6, 8, 10, 0, 60));
        workshopList.add(new Workshop(24,"Point Cook College P-9", "DHD", "Collins St", 2018, 6, 8, 11, 0, 60));
        workshopList.add(new Workshop(25,"Penola Catholic College", "DHD", "Collins St", 2018, 6, 8, 13, 0, 60));
        return workshopList;
    }



}
