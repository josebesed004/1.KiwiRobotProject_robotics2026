package edu.elon.robotics.JoseTools;

import java.util.ArrayList;

import edu.elon.robotics.autonomous.AutoCommon;

public class DrivingInstruction extends AutoCommon {

    private ArrayList<Runnable> instructions = new ArrayList<>();

    private final long SHORT_PAUSE = 500;
    private final long LONG_PAUSE = 1000;

    public DrivingInstruction() {
    }

    public void addInstruction(Runnable instruction) {
        instructions.add(instruction);
    }

    @Override
    public void runOpMode() throws InterruptedException {

        // Set up the robot for autonomous mode
        super.runOpMode();

        waitForStart();

        for (Runnable instruction : instructions) {
            if (!opModeIsActive()) return;

            instruction.run();

            sleep(SHORT_PAUSE);
        }
    }

    public void performInstruction(Instruction instruction) {
        if (instruction.ROTATE != 0) {
            turnAngle(instruction.ROTATE, instruction.POWER);
        }

        if (instruction.DRIVE != 0 || instruction.STRAFE != 0) {
            driveDistance(
                    instruction.DRIVE,
                    instruction.STRAFE,
                    instruction.POWER
            );
        }
    }

    // Drive forward/backward/strafe
    public Runnable drive(double forward, double strafe, double power) {
        return () -> driveDistance(forward, strafe, power);
    }

    // Turn
    public Runnable turn(double degrees, double power) {
        return () -> turnAngle(degrees, power);
    }

    // General instruction
    public Runnable instruction(
            double drive,
            double strafe,
            double rotate,
            double power) {

        return () -> performInstruction(
                new Instruction(drive, strafe, rotate, power)
        );
    }

    public static class Instruction {
        public double DRIVE;
        public double STRAFE;
        public double ROTATE;
        public double POWER;

        private Instruction(
                double drive,
                double strafe,
                double rotate,
                double power) {

            DRIVE = drive;
            STRAFE = strafe;
            ROTATE = rotate;
            POWER = power;
        }
    }
}
