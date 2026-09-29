package edu.elon.robotics.autonomous.Lab4;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import edu.elon.robotics.JoseTools.DrivingInstruction;

@Autonomous(name="PatternDriving", group="Lab4")
public class PatternDriving extends DrivingInstruction {
    public PatternDriving(){
        double speed = Speed.MEDIUM.getSpeed();

        addInstruction(() -> driveDistance(150, 0, speed));
        addInstruction(() -> driveDistance(-50, 0, speed));
        addInstruction(() -> turnAngle(-90, speed));
        addInstruction(() -> driveDistance(100, 0, speed));
        addInstruction(() -> turnAngle(70, speed));
        addInstruction(() -> driveDistance(-106.42, 0, speed));
        addInstruction(() -> turnAngle(-250, speed));
        addInstruction(() -> driveDistance(63.6, 0, speed));
        addInstruction(() -> turnAngle(-90, speed));
    }
}
