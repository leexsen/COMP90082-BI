package au.org.thebigissue.rostering.solver.entities;

import au.org.thebigissue.rostering.solver.variables.Availability;
import au.org.thebigissue.rostering.solver.variables.Facilitator;
import au.org.thebigissue.rostering.solver.variables.Staff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FacilitatorShiftTest {

    FacilitatorShift facilitatorShift;

    /*@BeforeEach
    void setup(){

        facilitatorShift=new FacilitatorShift(1,new Facilitator("Jones","William",new Availability(),true), LocalDate.of(2019,2,3));

    }

    //java.lang.ClassCastException: class au.org.thebigissue.rostering.solver.variables.Staff cannot be cast to class au.org.thebigissue.rostering.solver.variables.Facilitator (au.org.thebigissue.rostering.solver.variables.Staff and au.org.thebigissue.rostering.solver.variables.Facilitator are in unnamed module of loader 'app')
    @Test
    void getStaff() {

        Facilitator staff=facilitatorShift.getStaff();
        String name=staff.getName();
        System.out.println(name);

    }

    @Test
    void getWorkshopList() {

        List<Workshop> list= facilitatorShift.getWorkshopList();
        for (Workshop ws:list){
            ws.printWorkshop();
        }

    }*/
}