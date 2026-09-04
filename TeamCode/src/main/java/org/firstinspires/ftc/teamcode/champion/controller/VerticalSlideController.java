package org.firstinspires.ftc.teamcode.champion.controller;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.champion.utils.AdvancedPidController;

@Config
public class VerticalSlideController {
    public static String LEFT_MOTOR_NAME = "leftVert";
    public static String RIGHT_MOTOR_NAME = "rightVert";
    public static double ENCODER_TICKS_PER_INCH = 51.7;   //TODO: Measure ticks per inch of the slide
    public static double TARGET_TOLERANCE_INCH = 1.5;

    public static double PICK_UP_ELEMENT_POSE_INCH = 0;
    public static double TRANSFER_POSE_INCH = 0;
    public static double SPECIMEN_DEPOSIT_INCH = 11;
    public static double SPECIMEN_DEPOSIT_PREP_INCH = 7.0;
    public static double SPECIMEN_PICKUP_INCH = 10;
    public static double SPECIMEN_POST_PICKUP_INCH = SPECIMEN_PICKUP_INCH + 5;
    public static double ELBOW_CLEARANCE = 13;
    public static double BASKET_POSE_INCH = 25;

    public static double REST_POSE_INCH = 0;

    public static double P = 0.3;
    public static double I = 1.1;
    public static double D = 0.01;
    public static double MAX_I = 0.25;
    public static double FEEDFORWARD_CONSTANT = 0.2;  // Anti-gravity

    private static int MOTOR_TICKS_OFFSET = 0;

    private final DcMotor leftMotor, rightMotor;
    private AdvancedPidController pidController;
    private double autoTargetPose_in = 0;

    public VerticalSlideController(LinearOpMode opMode) {
        // Initialization
        leftMotor = opMode.hardwareMap.get(DcMotor.class, LEFT_MOTOR_NAME);
        rightMotor = opMode.hardwareMap.get(DcMotor.class, RIGHT_MOTOR_NAME);

        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        pidController = new AdvancedPidController(P, I, D, MAX_I, "VerticalSlideControllerPID");

        setAutoTargetPose_in(getCurrentPose_in());
    }

    public void update() {
        updateMotorTicksOffset();

        double targetPower = getAutoControlPower();
        leftMotor.setPower(targetPower);
        rightMotor.setPower(targetPower);
    }

    private double getAutoControlPower() {
        double currentPose_in = getCurrentPose_in();
        double pidOutput = pidController.calculate(currentPose_in, autoTargetPose_in);
        return pidOutput + FEEDFORWARD_CONSTANT;
    }

    public double getAutoTargetPose_in() {
        return autoTargetPose_in;
    }

    public void transfer() {
        setAutoTargetPose_in(TRANSFER_POSE_INCH);
    }

    public void rest() {
        setAutoTargetPose_in(REST_POSE_INCH);
    }

    public void basket() {
        setAutoTargetPose_in(BASKET_POSE_INCH);
    }

    public void pickUpSpecimen() {
        setAutoTargetPose_in(SPECIMEN_PICKUP_INCH);
    }

    public void postSpecimenPickUp() {
        setAutoTargetPose_in(SPECIMEN_POST_PICKUP_INCH);
    }

    public void pickUpElement() {
        setAutoTargetPose_in(PICK_UP_ELEMENT_POSE_INCH);
    }

    public void prepareSpecimenDeposit() {
        setAutoTargetPose_in(SPECIMEN_DEPOSIT_PREP_INCH);
    }

    public void depositSpecimen() {
        setAutoTargetPose_in(SPECIMEN_DEPOSIT_INCH);
    }

    public void setAutoTargetPose_in(double autoTargetPose_in) {
        this.autoTargetPose_in = autoTargetPose_in;
    }

    public boolean hasReachedTargetPose() {
        return Math.abs(getCurrentPose_in() - autoTargetPose_in) < TARGET_TOLERANCE_INCH;
    }

    private void updateMotorTicksOffset() {
        int currentPose_ticks = getCurrentPose_ticks();
        if (currentPose_ticks < 0) {
            MOTOR_TICKS_OFFSET = MOTOR_TICKS_OFFSET - currentPose_ticks;
        }
    }

    public void setManualPower(double power) {
        leftMotor.setPower(power);
        rightMotor.setPower(power);
    }

    public void manualUp() {
        setAutoTargetPose_in(getCurrentPose_in() + 0.1);
    }

    public void manualDown() {
        setAutoTargetPose_in(getCurrentPose_in() - 0.1);
    }


    public double getCurrentPose_in() {
        return (double) getCurrentPose_ticks() / ENCODER_TICKS_PER_INCH;
    }

    private int getCurrentPose_ticks() {
        return (leftMotor.getCurrentPosition() + rightMotor.getCurrentPosition()) / 2 + MOTOR_TICKS_OFFSET;
    }
}
