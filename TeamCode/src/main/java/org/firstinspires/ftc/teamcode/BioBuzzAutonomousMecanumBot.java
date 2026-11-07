package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * Starter autonomous for the BioBuzz StarterBot. Hardware setup and drive math come from
 * BioBuzzRobotBase, so there is nothing to redeclare here.
 *
 * Drive-only routine: drive forward for a fixed time, then drive backward for the same time,
 * then stop. The launcher, intake and transfer are never powered in auto, so nothing shoots.
 */
@Autonomous(name = "Biobuzz Autonomous", group = "StarterBot")
//@Disabled
public class BioBuzzAutonomousMecanumBot extends BioBuzzRobotBase {

    // Tune these on the field: how long each leg lasts and how fast the robot drives.
    private static final double DRIVE_TIME_SECONDS = 1.5;
    private static final double DRIVE_POWER = 0.5;

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
        double t = runtime.seconds();
        String step;
        if (t < DRIVE_TIME_SECONDS) {
            step = "Forward";
            mecanumDrive(DRIVE_POWER, 0, 0);
        } else if (t < 2 * DRIVE_TIME_SECONDS) {
            step = "Backward";
            mecanumDrive(-DRIVE_POWER, 0, 0);
        } else {
            step = "Done";
            mecanumDrive(0, 0, 0);
        }

        telemetry.addData("Step", step);
        telemetry.addData("Time", "%.1f s", runtime.seconds());
        telemetry.addData("Motors", "LF=%.2f RF=%.2f LB=%.2f RB=%.2f", leftFrontPower, rightFrontPower, leftBackPower, rightBackPower);
    }

    @Override
    public void stop() {
        mecanumDrive(0, 0, 0);
    }
}
