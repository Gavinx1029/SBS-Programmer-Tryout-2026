package org.firstinspires.ftc.teamcode.champion.controller;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class DepositClawController {
    public static String SERVO_NAME = "depClaw";
    public static double CLAW_OPEN_POSE = 0.6;
    public static double CLAW_CLOSE_POSE = 0.8;
    public static double CLAW_CLOSE_LOSE_POSE = 0.78;

    private final Servo servo;
    private boolean isOpen = false;

    public DepositClawController(LinearOpMode opMode) {
        // Initialization
        servo = opMode.hardwareMap.get(Servo.class, SERVO_NAME);
    }

    public void open() {
        servo.setPosition(CLAW_OPEN_POSE);
        isOpen = true;
    }

    public void close() {
        servo.setPosition(CLAW_CLOSE_POSE);
        isOpen = false;
    }

    public void closeLose() {
        servo.setPosition(CLAW_CLOSE_LOSE_POSE);
        isOpen = false;
    }

    @Deprecated
    public void setToPose(double pose) {
        servo.setPosition(pose);
    }

    public boolean isOpen() {
        return isOpen;
    }
}
