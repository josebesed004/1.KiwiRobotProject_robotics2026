package edu.elon.robotics.autonomous.Lab5;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import edu.elon.robotics.autonomous.AutoCommon;


@Autonomous(name="Imu Test", group = "Labs")
public class IMUTest extends AutoCommon {
    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();
        waitForStart();
        int timesCalled = 0;

        robot.imu.resetYaw();
        while (opModeIsActive()) {

            System.out.print(
                    "[JoseTest]:And now we are inside the loop, so we should be looping\n"
            );

            // You need to call this to fill the robotOrientation object
            // This needs to be done each time you want a new reading
            // which is why it is called inside the loop.
            // It will not update by itself
            YawPitchRollAngles robotOrientation = robot.imu.getRobotYawPitchRollAngles();

            // Then use these methods to extract each angle that you want
            // from the robotOrientation object.
            // This needs to be done each time you want a new reading and after
            //    robotOrientation = robot.imu.getRobotYawPitchRollAngles();
            // They will not update by themselves
            double yaw   = robot.getHeading();

            double pitch = robotOrientation.getPitch(AngleUnit.DEGREES);
            double roll  = robotOrientation.getRoll(AngleUnit.DEGREES);

            telemetry.addData("roll", roll);
            telemetry.addData("pitch", pitch);
            telemetry.addData("yaw", yaw);
            telemetry.update();

            System.out.printf(
                    "[ANGLES]:yaw=%f.2, pitch=%f.2, roll=%f.2, called=%d",
                    yaw, pitch, roll, timesCalled++
            );
            System.out.printf(
                    "[ANGLES]:RobotYAWPitchRoll: %s",
                    robotOrientation.toString()
            );

//            turnAngle(10,0.5);
//            sleep(1000);
        }
    }
}