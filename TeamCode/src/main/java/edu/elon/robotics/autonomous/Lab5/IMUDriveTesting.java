package edu.elon.robotics.autonomous.Lab5;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import edu.elon.robotics.JoseTools.DrivingInstruction;


@Configurable
@Autonomous(name="Imu Test Driving", group = "Lab5")
public class IMUDriveTesting extends DrivingInstruction {
    public static double driveCM = 400;
    public static double power = 0.3;
    public IMUDriveTesting(){
        addInstruction(() -> driveIMU(driveCM, power));
        addInstruction(() -> sleep(2*1000));
        addInstruction(() -> driveIMU(-driveCM, power));
        System.out.println("[Dribing_IMULOG]: Instructions added\n");
    }
}
