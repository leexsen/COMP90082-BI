package au.org.thebigissue.rostering.solver.entities;
/*
import au.org.thebigissue.rostering.solver.solution.Roster;
import au.org.thebigissue.rostering.solver.variables.Availability;
import au.org.thebigissue.rostering.solver.variables.Facilitator;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ShiftTest {

    @Test
    public void updateUtilisation(){

        String[] trained={"1"};
        FacilitatorShift garyShift=new FacilitatorShift(1,new Facilitator("Gary","",new Availability(),trained), LocalDate.of(2019,2,2));
        Workshop workshopOne=new Workshop(1,"default school","dc","dl","do","1","pax","jjj","303@qq.com","dp","dw",1,2,1,2,LocalDate.of(2019,1,2), LocalTime.of(9,0),LocalTime.of(10,0),true);
        List<Workshop> workshopList = new ArrayList<Workshop>();
        workshopList.add(workshopOne);

        Workshop workshopTwo=new Workshop(2,"default school","dc","dl","do","1","pax","jjj","303@qq.com","dp","dw",1,2,1,2,LocalDate.of(2019,1,2), LocalTime.of(11,0),LocalTime.of(12,0),true);
        workshopList.add(workshopTwo);
        garyShift.setWorkshopList(workshopList);

        double re=garyShift.getUtilisation();
        System.out.println(re);

    }

    @Test
    public void updateUtilisationTC1(){

        String[] trained={"1"};
        FacilitatorShift garyShift=new FacilitatorShift(1,new Facilitator("Gary","",new Availability(),trained), LocalDate.of(2019,2,2));
        Workshop workshopOne=new Workshop(1,"default school","dc","dl","do","1","pax","jjj","303@qq.com","dp","dw",1,2,1,2,LocalDate.of(2019,1,2), LocalTime.of(10,00),LocalTime.of(11,0),true);
        List<Workshop> workshopList = new ArrayList<Workshop>();
        workshopList.add(workshopOne);

        Workshop workshopTwo=new Workshop(2,"default school","dc","dl","do","1","pax","jjj","303@qq.com","dp","dw",1,2,1,2,LocalDate.of(2019,1,2), LocalTime.of(14,0),LocalTime.of(15,0),true);
        workshopList.add(workshopTwo);
        garyShift.setWorkshopList(workshopList);

        double re=garyShift.getUtilisation();
        System.out.println(re);

    }
    

}
 */