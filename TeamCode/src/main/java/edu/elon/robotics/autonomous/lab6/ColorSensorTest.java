package edu.elon.robotics.autonomous.lab6;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import edu.elon.robotics.JoseTools.DrivingInstruction;


@Configurable
@Autonomous(name="Color Sensor Test", group = "Lab6")
public class ColorSensorTest extends DrivingInstruction {
    public static boolean enableLight = true;

    public ColorSensorTest(){
        addInstruction(()-> colorTesting(enableLight));
        System.out.println("[ColorTestingTest]: Is light on? " + enableLight);
    }
}
