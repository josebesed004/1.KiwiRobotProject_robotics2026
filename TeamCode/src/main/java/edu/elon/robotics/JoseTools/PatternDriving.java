package edu.elon.robotics.JoseTools;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "PatternDriving", group = "Lab4")
public class PatternDriving extends DrivingInstruction {

    public PatternDriving() {

        addInstruction(drive(100, 0, 0.3));
        addInstruction(drive(-50, 0, 0.3));
        addInstruction(turn(-90, 0.3));
        addInstruction(drive(100, 0, 0.3));
        addInstruction(turn(70, 0.3));
        addInstruction(drive(-106.42, 0, 0.3));
        addInstruction(turn(-250, 0.3));
        addInstruction(drive(63.6, 0, 0.3));
        addInstruction(turn(-90, 0.3));
    }
}
