package org.firstinspires.ftc.teamcode.champion.official;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.champion.controller.DrivetrainController;

@Config
@TeleOp(name = "STAFF - Champion TeleOp", group = "Staff")
public class ChampionTeleOpNewControls extends ChampionLinearOpMode {
    public static boolean SHOW_TELEMETRY = true;

    private DrivetrainController drivetrainController;

    boolean isPressingX = false;
    boolean isPressingY = false;
    boolean isPressingA = false;
    boolean isPressingB = false;
    boolean isPressingY2 = false;
    boolean isPressingA2 = false;
    boolean isPressingX2 = false;
    boolean isPressingB2 = false;
    boolean isPressingDpadRight = false;
    boolean isPressingDpadLeft = false;
    boolean isPressingDpadUp = false;
    boolean isPressingDpadDown = false;
    boolean isPressingDpadUp2 = false;
    boolean isPressingDpadDown2 = false;
    boolean isPressingDpadRight2 = false;
    boolean isPressingRightTrigger = false;
    boolean isPressingLeftTrigger = false;
    boolean isPressingRightBumper = false;
    boolean isPressingLeftBumper = false;
    boolean isPressingRightBumper2 = false;
    boolean isPressingLeftBumper2 = false;
    boolean isPressingStart = false;
    boolean isPressingStart2 = false;
    boolean isPressingBack = false;
    boolean isPressingBack2 = false;

    boolean readyToLiftSpecimen = false;

    boolean isDepositingSpecimen = false;
    boolean isHanging = false;

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialization
        drivetrainController = new DrivetrainController(this);

        initializeCore(() -> {
            while (opModeIsActive() && !Thread.currentThread().isInterrupted()) {
                if (SHOW_TELEMETRY) {
                    telemetry.addData("VerticalSlidePose(in)", verticalSlideController.getCurrentPose_in());
                    telemetry.addData("HorizontalSlidePose(in)", horizontalSlideController.getCurrentPose_in());
                    telemetry.addData("VerticalSlideTargetPose: ", verticalSlideController.getAutoTargetPose_in());
                    telemetry.addData("HorizontalSlideTargetPose: ", horizontalSlideController.getAutoTargetPose_in());
                    telemetry.addData("Is Red: ", isRed);
                    telemetry.update();
                }

                if (!drivetrainController.isFastSpeedMode()) {
                    drivetrainController.normalizedDrive(
                            -gamepad1.left_stick_y * DrivetrainController.SLOW_SPEED_MULTIPLIER,
                            -gamepad1.left_stick_x * DrivetrainController.SLOW_SPEED_MULTIPLIER,
                            -gamepad1.right_stick_x * DrivetrainController.SLOW_TURN_MULTIPLIER
                    );
                } else {
                    drivetrainController.normalizedDrive(
                            -gamepad1.left_stick_y * DrivetrainController.FAST_SPEED_MULTIPLIER,
                            -gamepad1.left_stick_x * DrivetrainController.FAST_SPEED_MULTIPLIER,
                            -gamepad1.right_stick_x * DrivetrainController.FAST_TURN_MULTIPLIER
                    );
                }

                horizontalSlideController.update();
                depositElbowController.update();
                verticalSlideController.update();

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        depositClaw.close();
        mainThreadSleep(400);
        verticalSlideController.rest();

        telemetry.addLine("READY");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) {
            return;
        }

        depositElbowController.rest();
        restAsync();

        while (opModeIsActive()) {

            // Transfer
            if (gamepad1.dpad_right && !isPressingDpadRight) {
                isPressingDpadRight = true;
                transferAsync();
            } else if (!gamepad1.dpad_right && isPressingDpadRight) {
                isPressingDpadRight = false;
            }

            // Deposit Claw
            if (gamepad1.dpad_left && !isPressingDpadLeft) {
                isPressingDpadLeft = true;
                if (depositClaw.isOpen()) {
                    depositClaw.close();
                } else {
                    depositClaw.open();
                }
                isDepositingSpecimen = false;
            } else if (!gamepad1.dpad_left && isPressingDpadLeft) {
                isPressingDpadLeft = false;
            }

            // Manual Elbow Up
            if (gamepad1.dpad_up && !isPressingDpadUp) {
                isPressingDpadUp = true;
                depositElbowController.startManualUp();
            } else if (!gamepad1.dpad_up && isPressingDpadUp) {
                isPressingDpadUp = false;
                depositElbowController.holdCurrentPosition();
            }

            // Manual Elbow Down
            if (gamepad1.dpad_down && !isPressingDpadDown) {
                isPressingDpadDown = true;
                depositElbowController.startManualDown();
            } else if (!gamepad1.dpad_down && isPressingDpadDown) {
                isPressingDpadDown = false;
                depositElbowController.holdCurrentPosition();
            }

            // Roller Eject
            if (gamepad2.dpad_right && !isPressingDpadRight2) {
                isPressingDpadRight2 = true;
                rollerIntakeEjectAndIntake();
            } else if (!gamepad2.dpad_right && isPressingDpadRight2) {
                isPressingDpadRight2 = false;
            }

            // Roller Intake
            if (gamepad1.x && !isPressingX) {
                isPressingX = true;
                if (rollerIntakeController.isDoingIntake()) {
                    rollerIntakeController.rest();
                } else {
                    rollerIntakeController.intake();
                }
            } else if (!gamepad1.x && isPressingX) {
                isPressingX = false;
            }

            // Roller Tilt
            if (gamepad1.b && !isPressingB) {
                isPressingB = true;
                if (!rollerIntakeController.isTiltUp()) {
                    rollerIntakeController.tiltUp();
                } else {
                    rollerIntakeController.tiltDown();
                }
            } else if (!gamepad1.b && isPressingB) {
                isPressingB = false;
            }

            // Pickup Specimen
            if (gamepad1.back && !isPressingBack) {
                isPressingBack = true;
                if (readyToLiftSpecimen) {
                    postSpecimenPickUpAsync();
                    isDepositingSpecimen = true;
                    readyToLiftSpecimen = false;
                    isHanging = true;
                } else {
                    pickUpSpecimenAsync();
                    readyToLiftSpecimen = true;
                }
            } else if (!gamepad1.back && isPressingBack) {
                isPressingBack = false;
            }

            // Specimen Deposit
            if (gamepad1.a && !isPressingA) {
                isPressingA = true;
                if (isDepositingSpecimen) {
                    if (isHanging) {
                        prepareSpecimenDepositAsync();
                        isHanging = false;
                    } else {
                        depositSpecimenAsync();
                        isHanging = true;
                    }
                } else {
                    if (isCompressed) {
                        depositSpecimenAsync();
                    } else {
                        restAsync();
                    }
                }
            } else if (!gamepad1.a && isPressingA) {
                isPressingA = false;
            }

            // Basket Deposit
            if (gamepad1.y && !isPressingY) {
                isPressingY = true;
                basketDepositAsync();
            } else if (!gamepad1.y && isPressingY) {
                isPressingY = false;
            }

            // Roller Manual Tilt Up
            if (gamepad2.y && !isPressingY2) {
                isPressingY2 = true;
                rollerIntakeController.manualtiltUp();
            } else if (!gamepad2.y && isPressingY2) {
                isPressingY2 = false;
            }

            // Roller Manual Tilt Down
            if (gamepad2.a && !isPressingA2) {
                isPressingA2 = true;
                rollerIntakeController.manualtiltDown();
            } else if (!gamepad2.a && isPressingA2) {
                isPressingA2 = false;
            }

            // Wrist Manual Up
            if (gamepad2.x && !isPressingX2) {
                isPressingX2 = true;
                depositWristController.manualWristUp();
            } else if (!gamepad2.x && isPressingX2) {
                isPressingX2 = false;
            }

            // Wrist Manual Down
            if (gamepad2.b && !isPressingB2) {
                isPressingB2 = true;
                depositWristController.manualWristDown();
            } else if (!gamepad2.b && isPressingB2) {
                isPressingB2 = false;
            }

            // Prepare Specimen Deposit
            if (gamepad2.back && !isPressingBack2) {
                isPressingBack2 = true;
                prepareSpecimenDepositAsync();
            } else if (!gamepad2.back && isPressingBack2) {
                isPressingBack2 = false;
            }

            // Extend Horizontal Slide
            if (gamepad1.left_trigger > 0.9 && !isPressingLeftTrigger) {
                isPressingLeftTrigger = true;
                horizontalSlideController.extend();
            } else if (!(gamepad1.left_trigger > 0.9) && isPressingLeftTrigger) {
                isPressingLeftTrigger = false;
                horizontalSlideController.holdCurrentPose();
            }

            // Retract Horizontal Slide
            if (gamepad1.right_trigger > 0.9 && !isPressingRightTrigger) {
                isPressingRightTrigger = true;
                horizontalSlideController.retract();
            } else if (!(gamepad1.right_trigger > 0.9) && isPressingRightTrigger) {
                isPressingRightTrigger = false;
                horizontalSlideController.holdCurrentPose();
            }

            // Drivetrain Power Control
            if (gamepad1.left_bumper && !isPressingLeftBumper) {
                isPressingLeftBumper = true;
                if (drivetrainController.isFastSpeedMode()) {
                    drivetrainController.setSlowSpeed();
                } else {
                    drivetrainController.setFastSpeed();
                }
            } else if (!gamepad1.left_bumper && isPressingLeftBumper) {
                isPressingLeftBumper = false;
            }

            if (gamepad2.right_bumper && !isPressingRightBumper2) {
                isPressingRightBumper2 = true;
            } else if (!gamepad2.right_bumper && isPressingRightBumper2) {
                isPressingRightBumper2 = false;
            }

            if (gamepad1.start && !isPressingStart) {
                isPressingStart = true;
                restAsync();
            } else if (!gamepad1.start && isPressingStart) {
                isPressingStart = false;
            }

            mainThreadSleep(50);
        }

        updateThread.interrupt();
        automationThread.interrupt();
    }
}
