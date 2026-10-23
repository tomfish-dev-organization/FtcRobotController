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

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp(name = "Mec BioBuzz StarterBot Teleop", group = "StarterBot")
//@Disabled
public class BioBuzzStarterbotTeleopMecanum extends OpMode {

    /*
     * Direction constants: if a motor or servo spins the wrong way, flip its
     * direction here (FORWARD <-> REVERSE). No other code changes needed.
     */
    public static final DcMotorSimple.Direction LAUNCHER_DIRECTION = DcMotorSimple.Direction.FORWARD;
    public static final DcMotorSimple.Direction INTAKE_DIRECTION = DcMotorSimple.Direction.FORWARD;
    public static final DcMotorSimple.Direction LEFT_INTAKE_SERVO_DIRECTION = DcMotorSimple.Direction.FORWARD;
    public static final DcMotorSimple.Direction RIGHT_INTAKE_SERVO_DIRECTION = DcMotorSimple.Direction.REVERSE;
    public static final DcMotorSimple.Direction WINDMILL_DIRECTION = DcMotorSimple.Direction.REVERSE;

    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;
    private DcMotorEx launcher = null;
    private DcMotor intake = null;
    private CRServo leftIntakeServo = null;
    private CRServo rightIntakeServo = null;
    private CRServo windmillServo = null;

    public final int LAUNCHER_TARGET_VELOCITY = 1250; //2678 RPM
    public final int LAUNCHER_MIN_VELOCITY = 1200; //2571 RPM

    double leftFrontPower;
    double rightFrontPower;
    double leftBackPower;
    double rightBackPower;

    double intakePower;

    @Override
    public void init() {
        leftFrontDrive = hardwareMap.get(DcMotor.class, "left_front_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");

        intake = hardwareMap.tryGet(DcMotor.class, "intake");
        launcher = hardwareMap.tryGet(DcMotorEx.class, "launcher");
        windmillServo = hardwareMap.tryGet(CRServo.class, "windmillServo");
        leftIntakeServo = hardwareMap.tryGet(CRServo.class, "left_intake_servo");
        rightIntakeServo = hardwareMap.tryGet(CRServo.class, "right_intake_servo");

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        leftFrontDrive.setZeroPowerBehavior(BRAKE);
        rightFrontDrive.setZeroPowerBehavior(BRAKE);
        leftBackDrive.setZeroPowerBehavior(BRAKE);
        rightBackDrive.setZeroPowerBehavior(BRAKE);

        if (intake != null) {
            intake.setZeroPowerBehavior(BRAKE);
            intake.setDirection(INTAKE_DIRECTION);
        }

        if (launcher != null) {
            launcher.setDirection(LAUNCHER_DIRECTION);
            launcher.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            launcher.setZeroPowerBehavior(FLOAT);
            launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(40, 0, 0, 12.5));
        }

        if (leftIntakeServo != null) {
            leftIntakeServo.setDirection(LEFT_INTAKE_SERVO_DIRECTION);
            leftIntakeServo.setPower(0);
        }
        if (rightIntakeServo != null) {
            rightIntakeServo.setDirection(RIGHT_INTAKE_SERVO_DIRECTION);
            rightIntakeServo.setPower(0);
        }
        if (windmillServo != null) {
            windmillServo.setDirection(WINDMILL_DIRECTION);
            windmillServo.setPower(0);
        }

        telemetry.addData("Status", "Initialized");
        if (intake == null) telemetry.addData("WARNING", "intake not configured");
        if (launcher == null) telemetry.addData("WARNING", "launcher not configured");
        if (windmillServo == null) telemetry.addData("WARNING", "windmillServo not configured");
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
        telemetry.addData("Windmill", windmillServo != null ? "OK" : "NOT CONFIGURED");
    }

    @Override
    public void stop() {
    }

    void mecanumDrive(double forward, double strafe, double rotate) {
        leftFrontPower = forward + strafe + rotate;
        rightFrontPower = forward - strafe - rotate;
        leftBackPower = forward - strafe + rotate;
        rightBackPower = forward + strafe - rotate;

        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);
    }

    void launch() {
        if (launcher == null || windmillServo == null) {
            return;
        }

        if (gamepad1.x) {
            launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
        } else {
            launcher.setVelocity(0);
        }

        if (gamepad1.x && Math.abs(launcher.getVelocity()) >= LAUNCHER_MIN_VELOCITY) {
            windmillServo.setPower(1);
            intakePower += 0.5;
        } else {
            windmillServo.setPower(0);
        }
    }
}
