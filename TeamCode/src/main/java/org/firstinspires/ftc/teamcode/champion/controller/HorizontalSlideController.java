package org.firstinspires.ftc.teamcode.champion.controller;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.champion.utils.AdvancedPidController;

@Config
public class HorizontalSlideController {
    public static double TRANSFER_POSE_IN = 3;
    public static double REST_POST_IN = 0;
    public static double TARGET_TOLERANCE = 1;
    public static double TRANSFER_CLEARANCE_IN = 8.5;

    public static String MOTOR_NAME = "intake";
    public static double MOTOR_MAX_CURRENT_AMPS = Double.MAX_VALUE;
    public static double ENCODER_TICKS_PER_INCH = 90.15;
    public static double EXTENSION_POWER = 1.0;
    public static double RETRACTION_POWER = -1.0;
    public static double MAX_EXTENSION_IN = 20;

    public static double P = 0.5;
    public static double I = 0.1;
    public static double D = 0;
    public static double MAX_I = 0.1;

    private enum SlideAction {
        EXTEND, RETRACT, MANUAL, AUTO
    }

    private final DcMotorEx motor;
    private SlideAction slideAction;
    private AdvancedPidController pidController;
    private double autoTargetPose_in;
    private double manualTargetPower = 0;
    private int tickOffset = 0;

    public HorizontalSlideController(LinearOpMode opMode) {
        // Initialization
        motor = opMode.hardwareMap.get(DcMotorEx.class, MOTOR_NAME);
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        pidController = new AdvancedPidController(P, I, D, MAX_I, "HorizontalSlidePID");

        holdCurrentPose();
    }

    public void update() {
        double currentPose_in = getCurrentPose_in();
        switch (slideAction) {
            case AUTO:
                motor.setPower(getAutoControlPower());
                break;
            case EXTEND:
                if (currentPose_in > MAX_EXTENSION_IN) {
                    setAutoTargetPose(MAX_EXTENSION_IN);
                } else {
                    motor.setPower(EXTENSION_POWER);
                }
                break;
            case RETRACT:
                if (currentPose_in > 0) {
                    motor.setPower(RETRACTION_POWER);
                } else {
                    setAutoTargetPose(0);
                }
                break;
            default:    // Manual control
                motor.setPower(manualTargetPower);
        }
    }

    public void setAutoTargetPose(double targetPose_in) {
        autoTargetPose_in = targetPose_in;
        slideAction = SlideAction.AUTO;
    }

    public void retract() {
        slideAction = SlideAction.RETRACT;
    }

    public void extend() {
        slideAction = SlideAction.EXTEND;
    }

    public void rest() {
        setAutoTargetPose(REST_POST_IN);
    }

    public void holdCurrentPose() {
        setAutoTargetPose(getCurrentPose_in());
    }

    @Deprecated
    public void setManualTargetPower(double power) {
        manualTargetPower = power;
        slideAction = SlideAction.MANUAL;
    }

    public boolean hasReachedTarget() {
        return Math.abs(getCurrentPose_in() - autoTargetPose_in) < TARGET_TOLERANCE;
    }

    private double getAutoControlPower() {
        double currentPose_in = getCurrentPose_in();
        return pidController.calculate(currentPose_in, autoTargetPose_in);
    }

    public double getCurrentPose_in() {
        return (double) getCurrentPose_ticks() / ENCODER_TICKS_PER_INCH;
    }

    public double getAutoTargetPose_in() {
        return autoTargetPose_in;
    }

    private int getCurrentPose_ticks() {
        int currentTicks = motor.getCurrentPosition() + tickOffset;
        if (currentTicks < 0) {
            tickOffset -= currentTicks;
            return 0;
        }
        return currentTicks;
    }


    @Deprecated
    public void resetPid() {
        pidController = new AdvancedPidController(P, I, D, MAX_I, "HorizontalSlidePID");
    }
}
