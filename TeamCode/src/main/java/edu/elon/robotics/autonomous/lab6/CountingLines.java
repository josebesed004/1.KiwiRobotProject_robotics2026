package edu.elon.robotics.autonomous.lab6;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import edu.elon.robotics.autonomous.AutoCommon;

@Configurable
@Autonomous(name="Counting Lines", group = "Lab6")
public class CountingLines extends AutoCommon {

    public static double threshold_modifier = 0.5;
    enum Linestate {ON_LINE, NOT_ON_LINE}

    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();

        waitForStart();
        driveToCalibrateLightSensor();

        double brightness_threshold = (robot.maxBrightness - robot.minBrightness) * threshold_modifier;
        brightness_threshold += robot.minBrightness;

        int LINE_COUNT = 0;
        Linestate state;

        if (robot.colorSensor.alpha() > brightness_threshold) {
            state = Linestate.ON_LINE;
        } else {
            state = Linestate.NOT_ON_LINE;
        }

        robot.resetDriveEncoders();
        robot.startMove(0.05, 0, 0);
        while (opModeIsActive() && robot.touchSensor.isPressed() == false) {
            if (robot.colorSensor.alpha() > brightness_threshold && state == Linestate.NOT_ON_LINE) {
                LINE_COUNT += 1;
                state = Linestate.ON_LINE;
                System.out.println("Linestate: " + state);
            } else if (robot.colorSensor.alpha() < brightness_threshold) {
                state = Linestate.NOT_ON_LINE;
                System.out.println("Linestate: " + state);
            }
            System.out.println("Linecount: " + LINE_COUNT);
        }
        robot.stopMove();

        int ticksDriven = Math.abs(robot.motorLeft.getCurrentPosition());
        double distanceCM = ticksDriven / robot.LR_TICKS_PER_FWD_CM + 20.0;

        driveDistance(-distanceCM, 0, 0.05);


    }
}
