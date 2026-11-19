package edu.elon.robotics.autonomous.Lab9;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import edu.elon.robotics.JoseTools.DrivingInstruction;

@Configurable
@Autonomous(name="Color recognition", group = "Lab9")
@TeleOp(name="Color recognition", group = "Lab9")
public class ColorRecognition extends DrivingInstruction {

    public static int RED_ID = 1;
    public static int BLUE_ID = 2;

    public ColorRecognition() {
        tag = "[ColorRecognition]";
        addInstruction(this::runColorRecognition);
    }

    private void runColorRecognition() {
        robot.huskyLens.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);

        while (opModeIsActive()){
            for (HuskyLens.Block block : robot.huskyLens.blocks()) {
                if (block.id == RED_ID) {
                    System.out.println("Seeing Red!!!");
                    telemetry.addData("Color: Red", "Block information: ", robot.huskyLens.blocks());
                    telemetry.addData("Position", "x: %d, y: %d", block.x, block.y);
                }
                else if (block.id == BLUE_ID) {
                    System.out.println("Seeing Blue");
                    telemetry.addData("Color: Blue", "Block information: ", robot.huskyLens.blocks());
                    telemetry.addData("Position", "x: %d, y: %d", block.x, block.y);
                }
            }
            telemetry.update();
        }
    }
}