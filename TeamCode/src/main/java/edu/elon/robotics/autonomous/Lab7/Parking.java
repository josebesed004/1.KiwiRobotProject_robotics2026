package edu.elon.robotics.autonomous.Lab7;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import edu.elon.robotics.JoseTools.DrivingInstruction;

@Configurable
@Autonomous(name="Parking}", group = "Lab6")
public class Parking extends DrivingInstruction {

    public Parking(){
        addInstruction(this::reportTelemetry);
        //System.out.println("[ColorTestingTest]: Is light on? " + enableLight);
    }

    private void doNothing(){}

    public void reportTelemetry(){
        while (opModeIsActive()) {
            telemetry.addData("distance", robot.distanceSensor.getDistance(DistanceUnit.CM));
            telemetry.update();
        }
    }
}
