package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * Starter autonomous for the BioBuzz StarterBot. Hardware setup and drive math come from
 * BioBuzzRobotBase, so there is nothing to redeclare here.
 *
 * Routine: drive forward for a fixed time, then run the same shoot sequence as the teleop
 * (spin up the launcher, feed, tilt the transfer and fire), then stop. Replace the drive
 * step with your real positioning.
 */
@Autonomous(name = "Biobuzz Autonomous", group = "StarterBot")
//@Disabled
public class BioBuzzAutonomousMecanumBot extends BioBuzzRobotBase {

    enum AutoState { DRIVING, SPINNING_UP, FEEDING, FIRING, DONE }

    static final double DRIVE_DURATION = 1.5;

    private final ElapsedTime runtime = new ElapsedTime();
    private final ElapsedTime stateTimer = new ElapsedTime();
    private AutoState state = AutoState.DRIVING;

    @Override
    public void init() {
        initHardware();
        telemetry.addData("Status", "Initialized");
        if (launcher == null) telemetry.addData("WARNING", "launcher not configured");
    }

    @Override
    public void start() {
        runtime.reset();
        stateTimer.reset();
    }

    @Override
    public void loop() {
        double intakePower = 0;
        boolean windmillShooting = false;

        switch (state) {
            case DRIVING:
                mecanumDrive(0.5, 0, 0);
                if (stateTimer.seconds() >= DRIVE_DURATION) {
                    mecanumDrive(0, 0, 0);
                    // Skip the shot if there is no launcher to shoot with.
                    changeState(launcher != null ? AutoState.SPINNING_UP : AutoState.DONE);
                    if (launcher != null) launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
                }
                break;

            case SPINNING_UP:
                if (Math.abs(launcher.getVelocity()) >= LAUNCHER_MIN_VELOCITY) {
                    changeState(AutoState.FEEDING);
                }
                break;

            case FEEDING:
                intakePower = 1.0;
                windmillShooting = stateTimer.seconds() >= FEED_DURATION - WINDMILL_LEAD_TIME;
                if (stateTimer.seconds() >= FEED_DURATION) {
                    changeState(AutoState.FIRING);
                    if (transfer != null) transfer.setPosition(TRANSFER_TILT_POSITION);
                }
                break;

            case FIRING:
                intakePower = 1.0;
                windmillShooting = true;
                if (stateTimer.seconds() >= FIRE_DURATION) {
                    changeState(AutoState.DONE);
                    launcher.setVelocity(0);
                    if (transfer != null) transfer.setPosition(TRANSFER_STOW_POSITION);
                    intakePower = 0;
                    windmillShooting = false;
                }
                break;

            case DONE:
                break;
        }

        // The intake motor and servos only run to feed the launcher during the shoot sequence.
        if (intake != null) intake.setPower(intakePower);
        if (leftIntakeServo != null) leftIntakeServo.setPower(intakePower);
        if (rightIntakeServo != null) rightIntakeServo.setPower(intakePower);
        if (windmill != null) {
            windmill.setPosition(windmillShooting ? WINDMILL_SHOOTING_POSITION : WINDMILL_GUARDING_POSITION);
        }

        telemetry.addData("State", state);
        telemetry.addData("Time", "%.1f s", runtime.seconds());
        telemetry.addData("Motors", "LF=%.2f RF=%.2f LB=%.2f RB=%.2f", leftFrontPower, rightFrontPower, leftBackPower, rightBackPower);
        if (launcher != null) {
            telemetry.addData("Launcher", "%.0f / %d", Math.abs(launcher.getVelocity()), LAUNCHER_TARGET_VELOCITY);
        }
    }

    private void changeState(AutoState next) {
        state = next;
        stateTimer.reset();
    }

    @Override
    public void stop() {
        mecanumDrive(0, 0, 0);
        if (launcher != null) launcher.setVelocity(0);
        if (intake != null) intake.setPower(0);
        if (leftIntakeServo != null) leftIntakeServo.setPower(0);
        if (rightIntakeServo != null) rightIntakeServo.setPower(0);
        if (transfer != null) transfer.setPosition(TRANSFER_STOW_POSITION);
        if (windmill != null) windmill.setPosition(WINDMILL_GUARDING_POSITION);
    }
}
