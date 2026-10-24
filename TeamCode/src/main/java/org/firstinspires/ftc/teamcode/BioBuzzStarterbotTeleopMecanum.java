/*   MIT License
 *   Copyright (c) [2026] [Base 10 Assets, LLC]
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:

 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.

 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Mec BioBuzz StarterBot Teleop", group = "StarterBot")
//@Disabled
public class BioBuzzStarterbotTeleopMecanum extends BioBuzzRobotBase {

    double intakePower;

    @Override
    public void init() {
        // Hardware declarations and setup are shared with the autonomous in BioBuzzRobotBase.
        initHardware();

        telemetry.addData("Status", "Initialized");
        if (intake == null) telemetry.addData("WARNING", "intake not configured");
        if (launcher == null) telemetry.addData("WARNING", "launcher not configured");
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
    }

    @Override
    public void loop() {
        mecanumDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        if (gamepad1.right_bumper) {
            intakePower = 1.0;
        } else if (gamepad1.left_bumper) {
            intakePower = -1.0;
        } else {
            intakePower = 0.0;
        }

        launch();

        if (intake != null) intake.setPower(intakePower);
        if (leftIntakeServo != null) leftIntakeServo.setPower(intakePower);
        if (rightIntakeServo != null) rightIntakeServo.setPower(intakePower);

        telemetry.addData("Stick", "fwd=%.2f strafe=%.2f turn=%.2f", -gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        telemetry.addData("Motors", "LF=%.2f RF=%.2f LB=%.2f RB=%.2f", leftFrontPower, rightFrontPower, leftBackPower, rightBackPower);
        telemetry.addData("Buttons", "RB=%b LB=%b X=%b", gamepad1.right_bumper, gamepad1.left_bumper, gamepad1.x);
        telemetry.addData("Intake", "power=%.1f | %s", intakePower, intake != null ? "OK" : "NOT CONFIGURED");
        if (launcher != null) {
            double speed = Math.abs(launcher.getVelocity());
            telemetry.addData("Launcher", "%.0f / %d %s", speed, LAUNCHER_TARGET_VELOCITY, speed >= LAUNCHER_MIN_VELOCITY ? "READY" : "");
        } else {
            telemetry.addData("Launcher", "NOT CONFIGURED");
        }
    }

    @Override
    public void stop() {
    }

    void launch() {
        if (launcher == null) {
            return;
        }

        if (gamepad1.x) {
            launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
        } else {
            launcher.setVelocity(0);
        }

        if (gamepad1.x && Math.abs(launcher.getVelocity()) >= LAUNCHER_MIN_VELOCITY) {
            intakePower += 0.5;
        }
    }
}
