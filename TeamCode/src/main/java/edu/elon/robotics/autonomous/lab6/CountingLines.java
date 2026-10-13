package edu.elon.robotics.autonomous.lab6;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import edu.elon.robotics.autonomous.AutoCommon;

@Configurable
@Autonomous(name="Counting Lines", group = "Lab6")
public class CountingLines extends AutoCommon {

    enum Linestate {ON_LINE, NOT_ON_LINE}

    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();

        waitForStart();
        driveToCalibrateLightSensor();

        double brightness_threshold = (robot.minBrightness + robot.maxBrightness) / 2.0;

        int LINE_COUNT = 0;
        Linestate state;

        if (robot.colorSensor.alpha() > brightness_threshold) {
            state = Linestate.ON_LINE;
        } else {
            state = Linestate.NOT_ON_LINE;
        }

        robot.resetDriveEncoders();
        robot.startMove(0.3, 0, 0);

        while (opModeIsActive() && robot.touchSensor.isPressed() == false) {
            if (robot.colorSensor.alpha() > brightness_threshold && state == Linestate.NOT_ON_LINE) {
                LINE_COUNT += 1;
                state = Linestate.ON_LINE;
            } else if (robot.colorSensor.alpha() < brightness_threshold) {
                state = Linestate.NOT_ON_LINE;
            }
        }
        robot.stopMove();

        int ticksDriven = Math.abs(robot.motorLeft.getCurrentPosition());
        double distanceCM = ticksDriven / robot.LR_TICKS_PER_FWD_CM + 20.0;

        driveDistance(-distanceCM, 0, 0.3);

        telemetry.addData("Lines Counted", LINE_COUNT);
        telemetry.addData("Distance Traveled (cm)", distanceCM);
        telemetry.update();

        while (opModeIsActive()) {
            // Keep telemetry displayed until OpMode stops
        }
    }
}
