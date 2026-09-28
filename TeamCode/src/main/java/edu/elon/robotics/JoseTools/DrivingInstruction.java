package edu.elon.robotics.JoseTools;

import java.util.ArrayList;

import edu.elon.robotics.autonomous.AutoCommon;

public class DrivingInstruction extends AutoCommon {
    private ArrayList<Runnable> instructions = new ArrayList<Runnable>();
    private final double   SLOW_SPEED = 0.3;
    private final double MEDIUM_SPEED = 0.65;
    private final double   FAST_SPEED = 1.0;
    private final long    SHORT_PAUSE = 500;
    private final long     LONG_PAUSE = 1000;

    public DrivingInstruction(){
    }

//    public void addInstruction(double drive, double strafe, double rotate, double power){
//        instructions.add(new Instruction(drive, strafe, rotate, power));
//    }

    public void addInstruction(Runnable instruction){
        instructions.add(instruction);
    }



    @Override
    public void runOpMode() throws InterruptedException {

        // set up the robot for autonomous mode
        super.runOpMode();

        // any initialization should be done before waitForStart()

        // pause between initialization and run
        waitForStart();

        for (Runnable instruction : instructions){
            performInstruction(instruction);
            if (!opModeIsActive()) return;
            sleep(SHORT_PAUSE);
        }
    }

//    public void performInstruction(Instruction instruction){
//        turnAngle(instruction.ROTATE,instruction.POWER);
//        driveDistance(instruction.DRIVE, instruction.STRAFE, instruction.POWER);
//    }

    public void performInstruction(Runnable instruction){
        instruction.run();
    }

//    public static class Instruction{
//        public double DRIVE;
//        public double STRAFE;
//        public double ROTATE;
//        public double POWER;
//        private Instruction(double drive, double strafe, double rotate, double power){
//            DRIVE = drive;
//            STRAFE = strafe;
//            ROTATE = rotate;
//            POWER = power;
//        }
// }
}
