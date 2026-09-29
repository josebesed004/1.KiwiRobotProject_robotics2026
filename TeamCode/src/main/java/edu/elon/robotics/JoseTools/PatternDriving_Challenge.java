package edu.elon.robotics.JoseTools;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="PatternDriving_challenge", group="Lab4")
public class PatternDriving_Challenge extends DrivingInstruction{
    public PatternDriving_Challenge(){
        double speed = Speed.SLOW.getSpeed();

        addInstruction(() -> driveHeading(150, 0, speed));
        addInstruction(() -> driveHeading(50, 180, speed));
        addInstruction(() -> driveHeading(100, 90, speed));
        addInstruction(() -> driveHeading(106.42, 200, speed));
        addInstruction(() -> driveHeading(63.6, -90, speed));

    }
}
