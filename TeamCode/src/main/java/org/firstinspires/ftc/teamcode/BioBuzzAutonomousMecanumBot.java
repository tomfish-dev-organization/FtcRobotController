package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * Starter autonomous for the BioBuzz StarterBot. Hardware setup and drive math come from
 * BioBuzzRobotBase, so there is nothing to redeclare here.
 *
 * This is a placeholder routine: drive forward for a fixed time, then stop. Replace the
 * steps in loop() with your real autonomous.
 */
@Autonomous(name = "Biobuzz Autonomous", group = "StarterBot")
//@Disabled
public class BioBuzzAutonomousMecanumBot extends BioBuzzRobotBase {

    private final ElapsedTime runtime = new ElapsedTime();

    @Override
    public void init() {
        initHardware();
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void start() {
        runtime.reset();
    }

    @Override
    public void loop() {
        if (runtime.seconds() < 1.5) {
            mecanumDrive(0.5, 0, 0);
        } else {
            mecanumDrive(0, 0, 0);
        }

        telemetry.addData("Time", "%.1f s", runtime.seconds());
        telemetry.addData("Motors", "LF=%.2f RF=%.2f LB=%.2f RB=%.2f", leftFrontPower, rightFrontPower, leftBackPower, rightBackPower);
    }

    @Override
    public void stop() {
        mecanumDrive(0, 0, 0);
    }
}
