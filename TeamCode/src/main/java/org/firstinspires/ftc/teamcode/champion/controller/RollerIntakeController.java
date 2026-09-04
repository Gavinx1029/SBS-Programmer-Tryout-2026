package org.firstinspires.ftc.teamcode.champion.controller;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class RollerIntakeController {
    public static String ROLLER_SERVO_NAME = "roller";
    public static String TILT_SERVO_NAME = "inWrist";
    public static double ROLLER_INTAKE_POWER = 0;
    public static double ROLLER_EJECT_POWER = 1;
    public static double ROLLER_REST_POWER = 0.5;
    public static double TILT_INTAKE_POSE = 0.38;
    public static double TILT_REST_POSE = 0.6;
    public static double TILT_UP_POSE = 0.63;
    public static double TILT_TRUE_TRANSFER_POSE = 0.58;
    public static double TILT_TRANSFER_DOWN_POSE = 0.52;
    public static double TILT_TRANSFER_UP_POSE = 0.61;
    public static double TILT_EJECT_POSE = 0.45;

    private boolean tiltUp = false;


    private enum RollerIntakeMode {
        INTAKE, EJECT, REST, TRANSFER
    }

    private Servo rollerServo, tiltServo;
    //private ColorSensorController colorSensorController;
    private RollerIntakeMode rollerIntakeMode = RollerIntakeMode.REST;

    public RollerIntakeController(LinearOpMode opMode, boolean isRed) {
        rollerServo = opMode.hardwareMap.get(Servo.class, ROLLER_SERVO_NAME);
        tiltServo = opMode.hardwareMap.get(Servo.class, TILT_SERVO_NAME);
        //colorSensorController = new ColorSensorController(opMode, isRed);
    }

    public void update() {
        if (isDoingIntake()) {
            if (!intakeIsValid()) {
                tempEject();
            } else {
                intake();
            }
        }
    }

    public void rest() {
        rollerServo.setPosition(ROLLER_REST_POWER);
        tiltServo.setPosition(TILT_REST_POSE);
        rollerIntakeMode = RollerIntakeMode.REST;
    }

    public void intake() {
        rollerServo.setPosition(ROLLER_INTAKE_POWER);
        tiltServo.setPosition(TILT_INTAKE_POSE);
        rollerIntakeMode = RollerIntakeMode.INTAKE;

    }

    public void eject() {
        rollerServo.setPosition(ROLLER_EJECT_POWER);
        tiltServo.setPosition(TILT_EJECT_POSE);
        rollerIntakeMode = RollerIntakeMode.EJECT;
    }

    public void tempEject(){
        rollerServo.setPosition(ROLLER_EJECT_POWER);
        tiltServo.setPosition(TILT_EJECT_POSE);
        rollerIntakeMode = RollerIntakeMode.INTAKE;
    }

    public void transferUp() {
        rollerServo.setPosition(ROLLER_REST_POWER);
        tiltServo.setPosition(TILT_TRANSFER_UP_POSE);
        rollerIntakeMode = RollerIntakeMode.TRANSFER;
    }

    public void transferClear() {
        rollerServo.setPosition(ROLLER_REST_POWER);
        tiltServo.setPosition(TILT_TRANSFER_DOWN_POSE);
        rollerIntakeMode = RollerIntakeMode.TRANSFER;
    }

    public void sampleSlide() {
        tiltServo.setPosition(TILT_UP_POSE);
    }

    public void trueTransferPose() {
        tiltServo.setPosition(TILT_TRUE_TRANSFER_POSE);
    }

    public void manualtiltUp() {
        tiltServo.setPosition(tiltServo.getPosition() + 0.01);
    }

    public void manualtiltDown() {
        tiltServo.setPosition(tiltServo.getPosition() - 0.01);
    }

    public void tiltUp() {
        tiltServo.setPosition(TILT_REST_POSE);
        tiltUp = true;
    }

    public void tiltDown() {
        tiltServo.setPosition(TILT_TRANSFER_DOWN_POSE);
        tiltUp = false;
    }

    public boolean isTiltUp() {
        return tiltUp;
    }

    public boolean isDoingIntake() {
        return rollerIntakeMode == RollerIntakeMode.INTAKE;
    }

    public boolean intakeIsValid() {
//        return colorSensorController.intakeIsValid();
        return true;
    }

    public void setRollerPower(double power) {
        rollerServo.setPosition(power);
    }

    public void setTiltPosition(double position) {
        tiltServo.setPosition(position);
    }
}
