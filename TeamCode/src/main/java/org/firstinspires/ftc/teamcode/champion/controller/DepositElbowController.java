package org.firstinspires.ftc.teamcode.champion.controller;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class DepositElbowController {
    public static String LEFT_SERVO_NAME = "leftAxon";
    public static String RIGHT_SERVO_NAME = "rightAxon";

    double leftAxonManualStartPose, rightAxonManualStartPose;
    double leftAxonTargetPose, rightAxonTargetPose;

    public static double TRANSFER_POSE_DEG = 52.2;
    public static double REST_POSE_DEG = 90;
    public static double SPECIMEN_DEPOSIT_DEG = 97;
    public static double SPECIMEN_DEPOSIT_PREP_DEG = 93;
    public static double BASKET_DEPOSIT_DEG = 200;
    public static double SPECIMEN_PICKUP_DEG = -31;

    public double Elbow_Limit_Deg = 210;
    public static double MANUAL_POSITION_CHANGE_PER_SECOND = 0.3;

    public static double DELTA_C = 0.7;
    public static double CONSTANT_G = 0.1;

    public static double VERTICAL_UP_POSE = 0.74;
    public static double VERTICAL_DOWN_POSE = 0.192;

    private enum ControlMode {
        AUTO, MANUAL_UP, MANUAL_DOWN, HOLD
    }

    private final LinearOpMode opMode;
    private final Servo leftAxon, rightAxon;

    private double lastUpdateTime_s;
    private double autoTargetPose;
    private ControlMode controlMode = ControlMode.AUTO;
    private final double angleDegPerPose, poseOffset;
    private boolean hasReachedTarget = false;

    public DepositElbowController(LinearOpMode opMode) {
        this.opMode = opMode;

        // Initialization
        this.leftAxon = opMode.hardwareMap.get(Servo.class, LEFT_SERVO_NAME);
        this.rightAxon = opMode.hardwareMap.get(Servo.class, RIGHT_SERVO_NAME);

        holdCurrentPosition();

        angleDegPerPose = 180.0 / (VERTICAL_UP_POSE - VERTICAL_DOWN_POSE);
        poseOffset = VERTICAL_DOWN_POSE;

        leftAxonManualStartPose = leftAxon.getPosition();
        rightAxonManualStartPose = rightAxon.getPosition();
        leftAxonTargetPose = leftAxonManualStartPose;
        rightAxonTargetPose = rightAxonManualStartPose;
    }

    public void update() {
        double currentTime_s, deltaTime_s, currentPose;

        currentTime_s = opMode.getRuntime();
        currentPose = getCurrentPose();
        deltaTime_s = currentTime_s - lastUpdateTime_s;

        double intermediateTargetPose;

        // Calculate intermediateTargetPose
        switch (controlMode) {
            case AUTO:

                if (currentPose < autoTargetPose) {         // Increase current pose
                    double v = DELTA_C + CONSTANT_G * Math.sin(Math.toRadians(toAngle_deg(getCurrentPose())));
                    intermediateTargetPose = currentPose + v * deltaTime_s;

                    if (intermediateTargetPose > autoTargetPose) {
                        intermediateTargetPose = autoTargetPose;
                    }
                } else if (currentPose > autoTargetPose) {  // Decrease current pose
                    double v = -DELTA_C + CONSTANT_G * Math.sin(Math.toRadians(toAngle_deg(getCurrentPose())));
                    intermediateTargetPose = currentPose + v * deltaTime_s;

                    if (intermediateTargetPose < autoTargetPose) {
                        intermediateTargetPose = autoTargetPose;
                    }
                } else {
                    intermediateTargetPose = autoTargetPose;
                }

                break;
            case MANUAL_UP:
                if (toAngle_deg(currentPose) < Elbow_Limit_Deg) {
                    intermediateTargetPose = currentPose + deltaTime_s * MANUAL_POSITION_CHANGE_PER_SECOND;
                } else {
                    holdCurrentPosition();
                    intermediateTargetPose = currentPose;
                }
                break;
            case MANUAL_DOWN:
                intermediateTargetPose = currentPose - deltaTime_s * MANUAL_POSITION_CHANGE_PER_SECOND;
                break;
            default:
                intermediateTargetPose = currentPose;
        }

        lastUpdateTime_s = currentTime_s;
        leftAxon.setPosition(intermediateTargetPose);
        rightAxon.setPosition(intermediateTargetPose);

        hasReachedTarget = (intermediateTargetPose == autoTargetPose);
    }

    public void rest() {
        startAuto_deg(REST_POSE_DEG);
    }

    public void transfer() {
        startAuto_deg(TRANSFER_POSE_DEG);
    }

    public void basket() {
        startAuto_deg(BASKET_DEPOSIT_DEG);
    }

    public void pickUpSpecimen() {
        startAuto_deg(SPECIMEN_PICKUP_DEG);
    }

    public void depositSpecimen() {
        startAuto_deg(SPECIMEN_DEPOSIT_DEG);
    }

    public void prepareSpecimenDeposit() {
        startAuto_deg(SPECIMEN_DEPOSIT_PREP_DEG);
    }

    public boolean hasReachedTarget() {
        return hasReachedTarget;
    }

    public void startManualUp() {
        controlMode = ControlMode.MANUAL_UP;
    }

    public void startManualDown() {
        controlMode = ControlMode.MANUAL_DOWN;
    }

    public void startAuto_pose(double autoTargetPose) {
        this.autoTargetPose = autoTargetPose;
        controlMode = ControlMode.AUTO;
        hasReachedTarget = false;
    }

    public void startAuto_deg(double autoTargetDeg) {
        this.autoTargetPose = toPose(autoTargetDeg);
        controlMode = ControlMode.AUTO;
        hasReachedTarget = false;
    }

    public double toPose(double angle_deg) {
        return angle_deg / angleDegPerPose + poseOffset;
    }

    public double toAngle_deg(double servoPose) {
        return (servoPose - poseOffset) * angleDegPerPose;
    }

    public void holdCurrentPosition() {
        controlMode = ControlMode.HOLD;
    }

    public void setToAngle_deg(double angle_deg) {
        setToPose(toPose(angle_deg));
    }

    public void setToPose(double pose) {
        leftAxon.setPosition(pose);
        rightAxon.setPosition(pose);
    }

    public double getCurrentDeg() {
        return toAngle_deg(getCurrentPose());
    }

    /**
     * @return Average of the most recent set positions of Axons
     */
    public double getCurrentPose() {
        return leftAxon.getPosition();
    }

    public double getTargetPose() {
        return autoTargetPose;
    }
}