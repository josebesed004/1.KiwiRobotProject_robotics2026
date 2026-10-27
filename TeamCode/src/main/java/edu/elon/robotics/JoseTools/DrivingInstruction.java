package edu.elon.robotics.JoseTools;

import java.util.LinkedList;
import java.util.Queue;

import edu.elon.robotics.autonomous.AutoCommon;

public class DrivingInstruction extends AutoCommon {
    private Queue<Runnable> instructions = new LinkedList<>();
    protected String tag = "[Instruction]";
    public DrivingInstruction(){
    }

    public void addInstruction(Runnable instruction){instructions.add(instruction);
    }

    public void resetInstructions(){
        instructions.clear();
    }

    @Override
    public void runOpMode() throws InterruptedException {

        // set up the robot for autonomous mode
        super.runOpMode();

        // any initialization should be done before waitForStart()

        // pause between initialization and run
        waitForStart();
        runInstructions(Pause.SHORT.getPause());

    }

    private void runInstructions(long pauseInBetween){
        for (Runnable instruction : instructions){
            instruction.run();
            if (!opModeIsActive()) return;
            sleep(pauseInBetween);
        }
    }

    protected static enum Speed {
        SLOW(0.3),
        MEDIUM(0.56),
        FAST(1.0);

        private final double value;

        Speed(double value) {
            this.value = value;
        }

        public double getSpeed() {
            return value;
        }
    }

    private enum Pause {
        SHORT(500),
        LONG(1000);

        private final long milliseconds;

        Pause(long milliseconds) {
            this.milliseconds = milliseconds;
        }

        public long getPause() {
            return milliseconds;
        }
    }

    protected void print(String toPrint){
        System.out.println(tag + ": " + toPrint);
    }

}
