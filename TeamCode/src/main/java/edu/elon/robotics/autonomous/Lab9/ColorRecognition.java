package edu.elon.robotics.autonomous.Lab9;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import edu.elon.robotics.JoseTools.DrivingInstruction;
import edu.elon.robotics.hardware.PIDController;

@Configurable
@Autonomous(name="Color recognition", group = "Lab9")
public class ColorRecognition extends DrivingInstruction {

    public static int RED_ID = 1;
    public static int BLUE_ID = 2;

    public ColorRecognition(){
        robot.huskyLens.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);
        tag = "[ColorRecognition]";

        for (HuskyLens.Block block : robot.huskyLens.blocks()) {
            if (block.id == RED_ID) {
                // You have a block of color 1,
                // which is RED if you trained red first as suggested
                // Even better, define a constant at the top
                // of the class where RED_ID = 1, and test
                // if (block.id == RED_ID)

                // Do something with the red block
                System.out.println("Seeing Red!!!");
                telemetry.addData("Color: Red", "Block information: ", robot.huskyLens.blocks());
            }
            // Repeat test for a blue block
        }

    }

}