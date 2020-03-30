package au.org.thebigissue.rostering.solver.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class WorkshopTest {

    Workshop workshop;
    Workshop otherWorkshop;

    /*@BeforeEach
    void setup(){
        workshop=new Workshop(1,"Harvard","HTML","NewYork","somewhere","4","20","Jun","303@qq.com","837291837","super workshop",2,3,3,4,LocalDate.of(2019,2,1), LocalTime.of(3,20),LocalTime.of(5,30));
        otherWorkshop=new Workshop(2,"Harvard2222","HTML","NewYork","somewhere","4","20","Jun","303@qq.com","837291837","super workshop",2,3,3,4,LocalDate.of(2019,2,1), LocalTime.of(4,20),LocalTime.of(5,30));
    }

    @Test
    void printWorkshop(){
        workshop.printWorkshop();
    }

    @Test
    void getWorkshopData(){
        ArrayList<String> dataList=workshop.getWorkshopData();
        for(String workshopStr:dataList){
            System.out.println(workshopStr);
        }
    }

    @Test
    void hasOverlap(){
        boolean re=workshop.hasOverlap(otherWorkshop);
        assertTrue(re);
    }

    @Test
    void getDay(){
        String re=workshop.getDay();
        System.out.println(re);
    }*/

}