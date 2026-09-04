package org.firstinspires.ftc.teamcode.champion.controller;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.geometry.Translation2d;
import com.arcrobotics.ftclib.kinematics.wpilibkinematics.ChassisSpeeds;
import com.arcrobotics.ftclib.kinematics.wpilibkinematics.MecanumDriveKinematics;
import com.arcrobotics.ftclib.kinematics.wpilibkinematics.MecanumDriveWheelSpeeds;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Config
public class DrivetrainController {
    public static String LF_NAME = "lf";
    public static String RF_NAME = "rf";
    public static String LB_NAME = "lb";
    public static String RB_NAME = "rb";

    // Kinematics
    private final static Translation2d lfLocation = new Translation2d(0.095, 0.095);
    private final static Translation2d rfLocation = new Translation2d(0.095, -0.095);
    private final static Translation2d lbLocation = new Translation2d(-0.095, 0.095);
    private final static Translation2d rbLocation = new Translation2d(-0.095, -0.095);
    private final static MecanumDriveKinematics MECANUM_KINEMATICS = new MecanumDriveKinematics(lfLocation, rfLocation, lbLocation, rbLocation);

    public static double MIN_POWER_OFFSET = 0.1;

    public static double FAST_SPEED_MULTIPLIER = 2;
    public static double FAST_TURN_MULTIPLIER = 4;
    public static double SLOW_SPEED_MULTIPLIER = 0.8;
    public static double SLOW_TURN_MULTIPLIER = 3.5;

    private boolean isFastSpeedMode = false;

    private final DcMotor lf, rf, lb, rb;

    public DrivetrainController(LinearOpMode opMode) {
        // Initialize motors
        lf = opMode.hardwareMap.get(DcMotor.class, LF_NAME);
        rf = opMode.hardwareMap.get(DcMotor.class, RF_NAME);
        lb = opMode.hardwareMap.get(DcMotor.class, LB_NAME);
        rb = opMode.hardwareMap.get(DcMotor.class, RB_NAME);

        // Configure motor directions
        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.FORWARD);
        lb.setDirection(DcMotorSimple.Direction.REVERSE);
        rb.setDirection(DcMotorSimple.Direction.FORWARD);

        // Configure motor modes
        lf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Configure zero power behavior
        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    /** Drive the mecanum chassis using forward, strafe, and rotation power. */
    public void normalizedDrive(double xPower, double yPower, double rPower) {
        ChassisSpeeds speeds = new ChassisSpeeds(xPower, yPower, rPower);
        MecanumDriveWheelSpeeds mecanumWheelSpeeds = MECANUM_KINEMATICS.toWheelSpeeds(speeds);

        double[] wheelSpeeds = new double[4];
        wheelSpeeds[0] = mecanumWheelSpeeds.frontLeftMetersPerSecond;
        wheelSpeeds[1] = mecanumWheelSpeeds.frontRightMetersPerSecond;
        wheelSpeeds[2] = mecanumWheelSpeeds.rearLeftMetersPerSecond;
        wheelSpeeds[3] = mecanumWheelSpeeds.rearRightMetersPerSecond;
        adjustedWheelSpeeds(wheelSpeeds);

        requestPowers(
                wheelSpeeds[0],
                wheelSpeeds[1],
                wheelSpeeds[2],
                wheelSpeeds[3]
        );
    }

    private void adjustedWheelSpeeds(double[] wheelSpeeds) {
        double maxAbsolute = wheelSpeeds[0];
        for (int i = 1; i < 4; i++) {
            maxAbsolute = Math.max(maxAbsolute, Math.abs(wheelSpeeds[i]));
        }
        if (maxAbsolute > 1.0) {
            for (int i = 0; i < 4; i++) {
                wheelSpeeds[i] = wheelSpeeds[i] / maxAbsolute;
            }
        }
    }

    private void requestPowers(double lfPower, double rfPower, double lbPower, double rbPower) {
        lf.setPower(adjustMotorPower(lfPower));
        rf.setPower(adjustMotorPower(rfPower));
        lb.setPower(adjustMotorPower(lbPower));
        rb.setPower(adjustMotorPower(rbPower));
    }

    private double adjustMotorPower(double p) {
        if (p == 0)
            return 0;
        else if (p > 0)
            return MIN_POWER_OFFSET + (1 - MIN_POWER_OFFSET) * p;
        else
            return -MIN_POWER_OFFSET + (1 - MIN_POWER_OFFSET) * p;
    }

    public void setFastSpeed() {
        isFastSpeedMode = true;
    }

    public void setSlowSpeed() {
        isFastSpeedMode = false;
    }

    public boolean isFastSpeedMode() {
        return isFastSpeedMode;
    }

}
