package edu.elon.robotics.JoseTools;

import java.util.LinkedList;
import java.util.Queue;

import edu.elon.robotics.autonomous.AutoCommon;

public class DrivingInstruction extends AutoCommon {
    private Queue<Runnable> instructions = new LinkedList<>();
    protected long instructionsPause = Pause.SHORTEST.pause;
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
        System.out.println("[DEBUG] runOpMode");
        super.runOpMode();

        // any initialization should be done before waitForStart()

        // pause between initialization and run
        waitForStart();
        runInstructions(instructionsPause);

    }

    private void runInstructions(long pauseInBetween){
        for (Runnable instruction : instructions){
            instruction.run();
            if (!opModeIsActive()) return;
            sleep(pauseInBetween);
        }
    }

    protected static enum Speed {
        MINIMUM_SPEED(0.1),
        SLOWEST(0.25),
        SLOW(0.3),
        MEDIUM(0.4),
        FAST(0.6),
        MAX_SPEED(1.0);

        // Making this public and final means it can be accessed directly but never changed
        public final double speed;

        Speed(double speed) {
            this.speed = speed;
        }
    }

    private enum Pause {
        NO_PAUSE(0),
        SHORTEST(100),
        SHORT(500),
        LONG(1000);

        public final long pause;

        Pause(long milliseconds) {
            this.pause = milliseconds;
        }

    }

    protected void print(String toPrint){
        System.out.println(tag + ": " + toPrint);
    }

}
