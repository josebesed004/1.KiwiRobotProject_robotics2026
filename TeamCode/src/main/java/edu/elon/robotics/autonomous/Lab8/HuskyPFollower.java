package edu.elon.robotics.autonomous.Lab8;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.function.BooleanSupplier;

import edu.elon.robotics.JoseTools.DrivingInstruction;
import edu.elon.robotics.hardware.PIDController;

@Configurable
@Autonomous(name="HuskyLineFollower", group = "Lab8")
public class HuskyPFollower extends DrivingInstruction {

    public static final double Kp = 0.005;

    public static final double Ki = 0.0;

    public static final double Kd = 0.0;

    public PIDController controller = new PIDController(Kp, Ki, Kd);
    public HuskyPFollower(){
        tag = "[HuskyLineFollower]";

    }

}