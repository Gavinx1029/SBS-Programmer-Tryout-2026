package org.firstinspires.ftc.teamcode.champion.controller;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class DepositWristController {
    public static String SERVO_NAME = "depWrist";
    public static double DEPOSIT_POSE = 0.02;
    public static double PRE_DEPOSIT_POSE = 0.01;
    public static double PICKUP_POSE = 0.16;
    public static double TRANSFER_POSE = 0.07;
    public static double BASKET_POSE = 0.07; //change so claw dont fling
    public static double REST_POSE = 0.08;

    private final Servo servo;

    public DepositWristController(LinearOpMode opMode) {
        servo = opMode.hardwareMap.get(Servo.class, SERVO_NAME);
    }

    public void transfer() {
        servo.setPosition(TRANSFER_POSE);
    }

    public void rest() {
        servo.setPosition(REST_POSE);
    }

    public void basket() {
        servo.setPosition(BASKET_POSE);
    }

    public void pickUpSpecimen() {
        servo.setPosition(PICKUP_POSE);
    }

    public void preDepositSpecimen() {
        servo.setPosition(PRE_DEPOSIT_POSE);
    }

    public void depositSpecimen() {
        servo.setPosition(DEPOSIT_POSE);
    }

    public void manualWristUp() {
        servo.setPosition(servo.getPosition() + 0.1);
    }

    public void manualWristDown() {
        servo.setPosition(servo.getPosition() - 0.1);
    }

    @Deprecated
    public void setToPose(double pose) {
        servo.setPosition(pose);
    }
}
