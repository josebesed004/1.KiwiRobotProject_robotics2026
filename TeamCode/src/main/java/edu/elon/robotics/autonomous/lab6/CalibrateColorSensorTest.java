package edu.elon.robotics.autonomous.lab6;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import edu.elon.robotics.JoseTools.DrivingInstruction;


@Configurable
@Autonomous(name="Color Calibration test", group = "Lab6")
public class CalibrateColorSensorTest extends DrivingInstruction {
    public static boolean enableLight = true;

    public CalibrateColorSensorTest(){
        addInstruction(this::driveToCalibrateLightSensor);

    }
}
