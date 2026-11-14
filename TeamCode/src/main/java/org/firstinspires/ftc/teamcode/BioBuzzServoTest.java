package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

/*
 * Servo tester: drive one servo at a time by hand to check that it moves and to find
 * the positions you want. Nothing else on the robot moves.
 *
 *   D-pad left / right : pick which servo to drive
 *   D-pad up / down    : nudge the position by 0.01
 *   Left stick Y       : move the position smoothly
 *   A / B / Y          : jump to 0.0 / 0.5 / 1.0
 *   X                  : switch PWM range between the FTC default (600-2400 us)
 *                        and the full goBILDA range (500-2500 us)
 *
 * The servo only gets a signal after you press a button, so it doesn't jump at init.
 */
@TeleOp(name = "BioBuzz Servo Test", group = "Test")
public class BioBuzzServoTest extends OpMode {

    private static final String[] NAMES = {"blocker", "collector", "transfer", "windmill"};
    private static final PwmControl.PwmRange WIDE_RANGE = new PwmControl.PwmRange(500, 2500);

    private final Servo[] servos = new Servo[NAMES.length];
    private final double[] positions = new double[NAMES.length];
    private final boolean[] commanded = new boolean[NAMES.length];
    private int selected = 0;
    private boolean wideRange = false;
    private boolean prevLeft, prevRight, prevUp, prevDown, prevX;

    @Override
    public void init() {
        for (int i = 0; i < NAMES.length; i++) {
            servos[i] = hardwareMap.tryGet(Servo.class, NAMES[i]);
            positions[i] = 0.5;
        }
        telemetry.addData("Status", "Initialized. Press START, then pick a servo.");
    }

    @Override
    public void loop() {
        if (gamepad1.dpad_left && !prevLeft) selected = (selected + NAMES.length - 1) % NAMES.length;
        if (gamepad1.dpad_right && !prevRight) selected = (selected + 1) % NAMES.length;

        boolean moved = false;
        double pos = positions[selected];
        if (gamepad1.dpad_up && !prevUp) { pos += 0.01; moved = true; }
        if (gamepad1.dpad_down && !prevDown) { pos -= 0.01; moved = true; }
        if (Math.abs(gamepad1.left_stick_y) > 0.1) { pos += -gamepad1.left_stick_y * 0.005; moved = true; }
        if (gamepad1.a) { pos = 0.0; moved = true; }
        if (gamepad1.b) { pos = 0.5; moved = true; }
        if (gamepad1.y) { pos = 1.0; moved = true; }
        positions[selected] = Math.max(0.0, Math.min(1.0, pos));

        if (gamepad1.x && !prevX) {
            wideRange = !wideRange;
            for (Servo s : servos) {
                if (s instanceof ServoImplEx) {
                    ((ServoImplEx) s).setPwmRange(wideRange ? WIDE_RANGE : PwmControl.PwmRange.defaultRange);
                }
            }
        }

        prevLeft = gamepad1.dpad_left;
        prevRight = gamepad1.dpad_right;
        prevUp = gamepad1.dpad_up;
        prevDown = gamepad1.dpad_down;
        prevX = gamepad1.x;

        Servo servo = servos[selected];
        if (servo != null && moved) commanded[selected] = true;
        if (servo != null && commanded[selected]) {
            if (servo instanceof ServoImplEx) ((ServoImplEx) servo).setPwmEnable();
            servo.setPosition(positions[selected]);
        }

        telemetry.addData("Selected", "%s  (d-pad left/right to change)", NAMES[selected]);
        if (servo == null) {
            telemetry.addData("Servo", "NOT CONFIGURED");
        } else {
            telemetry.addData("Position", "%.2f %s", positions[selected], commanded[selected] ? "" : "(press a button to send)");
            telemetry.addData("Port", "%s port %d",
                    servo.getController().getConnectionInfo(), servo.getPortNumber());
            telemetry.addData("Direction", servo.getDirection());
        }
        telemetry.addData("PWM range", wideRange ? "500-2500 us (goBILDA)" : "600-2400 us (FTC default)");
    }
}
