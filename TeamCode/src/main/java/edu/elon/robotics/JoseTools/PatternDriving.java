package edu.elon.robotics.JoseTools;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="PatternDriving", group="Lab4")
public class PatternDriving extends DrivingInstruction{
    public PatternDriving(){

//        addInstruction(150,0,0,0.3);
//        addInstruction(-50,0,0,0.3);
////        addInstruction(0,0,-90,0.3);
//        addInstruction(100,0,0,0.3);
////        addInstruction(0,0,70,0);
//        addInstruction(-106.42,0,0,0.3);
////        addInstruction(0,0,-250,0.3);
//        addInstruction(63.6,0,0,0.3);
////        addInstruction(0,0,-90,0.3);

        addInstruction(() -> driveDistance(100, 0, 0.3));
        addInstruction(() -> driveDistance(-50, 0, 0.3));
        addInstruction(() -> turnAngle(-90, 0.3));
        addInstruction(() -> driveDistance(100, 0, 0.3));
        addInstruction(() -> turnAngle(70, 0.3));
        addInstruction(() -> driveDistance(-106.42, 0, 0.3));
        addInstruction(() -> driveDistance(63.6, 0, 0.3));
        addInstruction(() -> turnAngle(-90, 0.3));
    }
}
