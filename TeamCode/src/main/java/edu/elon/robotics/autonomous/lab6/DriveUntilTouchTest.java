package edu.elon.robotics.autonomous.lab6;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import edu.elon.robotics.JoseTools.DrivingInstruction;


@Configurable
@Autonomous(name="Drive Until Touch test", group = "Lab6")
public class DriveUntilTouchTest extends DrivingInstruction {
    public static double driveCM = 400;
    public static double power = 0.3;
    public DriveUntilTouchTest(){
        addInstruction(() -> driveUntilTouch(power));
        System.out.println("[TouchDriving_IMULOG]: Instructions added\n");
    }
}
