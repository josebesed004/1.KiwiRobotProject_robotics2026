package edu.elon.robotics.autonomous.Lab5;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import edu.elon.robotics.JoseTools.DrivingInstruction;


@Configurable
@Autonomous(name="Imu Test turning", group = "Lab5")
public class IMUTurningTesting extends DrivingInstruction {
    public static double degreesIMU = 200;
    public IMUTurningTesting(){
        System.out.println("[Turning_IMULOG]: Instructions added\n");
        addInstruction(() -> turnIMU(degreesIMU,0.3));
        addInstruction(() -> sleep(2*1000));
        addInstruction(() -> turnIMU(-degreesIMU, 0.3));
    }
}
