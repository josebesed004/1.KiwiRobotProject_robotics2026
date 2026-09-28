package edu.elon.robotics.JoseTools;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="PatternDriving", group="Lab4")
public class PatternDriving extends DrivingInstruction{
    public PatternDriving(){
        double speed = Speed.SLOW.getSpeed();

        addInstruction(() -> driveDistance(100, 0, speed));
        addInstruction(() -> driveDistance(-50, 0, speed));
        addInstruction(() -> turnAngle(-90, speed));
        addInstruction(() -> driveDistance(100, 0, speed));
        addInstruction(() -> turnAngle(70, speed));
        addInstruction(() -> driveDistance(-106.42, 0, speed));
        addInstruction(() -> driveDistance(63.6, 0, speed));
        addInstruction(() -> turnAngle(-90, speed));
    }
}
