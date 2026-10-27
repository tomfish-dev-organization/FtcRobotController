package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * Shared hardware setup for the BioBuzz StarterBot. Both the teleop and the autonomous
 * OpModes extend this class, so the motor/servo declarations, the hardwareMap names, the
 * motor directions and the mecanum drive math only live in one place.
 *
 * Call initHardware() from init() in each OpMode.
 */
public abstract class BioBuzzRobotBase extends OpMode {

    /*
     * Direction constants: if a motor or servo spins the wrong way, flip its
     * direction here (FORWARD <-> REVERSE). No other code changes needed.
     */
    public static final DcMotorSimple.Direction LAUNCHER_DIRECTION = DcMotorSimple.Direction.FORWARD;
    public static final DcMotorSimple.Direction INTAKE_DIRECTION = DcMotorSimple.Direction.FORWARD;
    public static final DcMotorSimple.Direction LEFT_INTAKE_SERVO_DIRECTION = DcMotorSimple.Direction.FORWARD;
    public static final DcMotorSimple.Direction RIGHT_INTAKE_SERVO_DIRECTION = DcMotorSimple.Direction.REVERSE;

    // Declare OpMode members.
    protected DcMotor leftFrontDrive = null;
    protected DcMotor leftBackDrive = null;
    protected DcMotor rightFrontDrive = null;
    protected DcMotor rightBackDrive = null;
    protected DcMotorEx launcher = null;
    protected DcMotor intake = null;
    protected CRServo leftIntakeServo = null;
    protected CRServo rightIntakeServo = null;
    protected Servo transfer = null;

    /*
     * These two variables are used to control the velocity of the launcher motor.
     * They are both in encoder ticks per second. The motors we use in the FIRST Tech Challenge
     * have encoders with a resolution of 28 ticks per revolution. We can convert this to RPM
     * by dividing the value by 28, to get to revolutions per second, before multiplying by 60
     * to get revolutions per minute.
     */
    public static final int LAUNCHER_TARGET_VELOCITY = 1250; //2678 RPM
    public static final int LAUNCHER_MIN_VELOCITY = 1200; //2571 RPM

    public static final double TRANSFER_STOW_POSITION = 0.0;
    public static final double TRANSFER_TILT_POSITION = 0.167;

    // The power last sent to each drive motor, kept here so OpModes can show it in telemetry.
    protected double leftFrontPower;
    protected double rightFrontPower;
    protected double leftBackPower;
    protected double rightBackPower;

    protected void initHardware() {
        /*
         * Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step.
         */
        leftFrontDrive = hardwareMap.get(DcMotor.class, "left_front_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");
        // Mechanisms are optional: tryGet returns null if a device isn't in the robot config,
        // so the robot can drive with only the four wheel motors configured.
        intake = hardwareMap.tryGet(DcMotor.class, "intake");
        launcher = hardwareMap.tryGet(DcMotorEx.class, "launcher");
        leftIntakeServo = hardwareMap.tryGet(CRServo.class, "left_intake_servo");
        rightIntakeServo = hardwareMap.tryGet(CRServo.class, "right_intake_servo");

        /*
         * To drive forward, most robots need the motor on one side to be reversed,
         * because the axles point in opposite directions. Pushing the left stick forward
         * MUST make robot go forward. So adjust these two lines based on your first test drive.
         * Note: The settings here assume direct drive on left and right wheels. Gear
         * Reduction or 90 Deg drives may require direction flips
         */
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        /*
         * Setting zeroPowerBehavior to BRAKE enables a "brake mode". This causes the motor to
         * slow down much faster when it is coasting. This creates a much more controllable
         * drivetrain. As the robot stops much quicker.
         */
        leftFrontDrive.setZeroPowerBehavior(BRAKE);
        rightFrontDrive.setZeroPowerBehavior(BRAKE);
        leftBackDrive.setZeroPowerBehavior(BRAKE);
        rightBackDrive.setZeroPowerBehavior(BRAKE);

        if (intake != null) {
            intake.setZeroPowerBehavior(BRAKE);
            intake.setDirection(INTAKE_DIRECTION);
        }

        /*
         * Here we set our launcher to the RUN_USING_ENCODER runmode.
         * If you notice that you have no control over the velocity of the motor, it just jumps
         * right to a number much higher than your set point, make sure that your encoders are plugged
         * into the port right beside the motor itself. And that the motors polarity is consistent
         * through any wiring.
         */
        if (launcher != null) {
            launcher.setDirection(LAUNCHER_DIRECTION);
            launcher.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            launcher.setZeroPowerBehavior(FLOAT);
            launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(40, 0, 0, 12.5));
        }

        /*
         * Much like our drivetrain motors, the right intake servo is reversed so that both
         * servos work to pull elements into the intake. Setting power also initializes the
         * servo controller.
         */
        transfer = hardwareMap.tryGet(Servo.class, "transfer");
        if (transfer != null) {
            transfer.setPosition(TRANSFER_STOW_POSITION);
        }

        if (leftIntakeServo != null) {
            leftIntakeServo.setDirection(LEFT_INTAKE_SERVO_DIRECTION);
            leftIntakeServo.setPower(0);
        }
        if (rightIntakeServo != null) {
            rightIntakeServo.setDirection(RIGHT_INTAKE_SERVO_DIRECTION);
            rightIntakeServo.setPower(0);
        }
    }

    protected void mecanumDrive(double forward, double strafe, double rotate) {
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

        /*
         * Send calculated power to wheels
         */
        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);
    }
}
