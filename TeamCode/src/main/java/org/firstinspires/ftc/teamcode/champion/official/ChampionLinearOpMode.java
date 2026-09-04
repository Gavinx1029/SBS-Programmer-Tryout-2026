package org.firstinspires.ftc.teamcode.champion.official;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.champion.controller.DepositClawController;
import org.firstinspires.ftc.teamcode.champion.controller.DepositElbowController;
import org.firstinspires.ftc.teamcode.champion.controller.DepositWristController;
import org.firstinspires.ftc.teamcode.champion.controller.HorizontalSlideController;
import org.firstinspires.ftc.teamcode.champion.controller.RollerIntakeController;
import org.firstinspires.ftc.teamcode.champion.controller.VerticalSlideController;

public abstract class ChampionLinearOpMode extends LinearOpMode {
    public static boolean isRed;
    protected boolean isCompressed = false;

    protected HorizontalSlideController horizontalSlideController;
    protected VerticalSlideController verticalSlideController;
    protected DepositElbowController depositElbowController;
    protected DepositWristController depositWristController;
    protected DepositClawController depositClaw;
    protected RollerIntakeController rollerIntakeController;

    protected Thread updateThread;
    protected Thread automationThread;

    protected void initializeCore(Runnable update) {
        horizontalSlideController = new HorizontalSlideController(this);
        verticalSlideController = new VerticalSlideController(this);
        depositElbowController = new DepositElbowController(this);
        depositWristController = new DepositWristController(this);
        depositClaw = new DepositClawController(this);
        rollerIntakeController = new RollerIntakeController(this, isRed);
        updateThread = new Thread(update);
        automationThread = new Thread();
    }

    /**
     * Start updateThread automatically after game starts.
     */
    @Override
    public void waitForStart() {
        super.waitForStart();

        updateThread.start();
    }

    public void restAsync() {
        automationThread.interrupt();
        automationThread = new Thread(this::rest);
        automationThread.start();
    }

    public void transferAsync() {
        automationThread.interrupt();
        automationThread = new Thread(this::transfer);
        automationThread.start();
    }

    public void pickUpSpecimenAsync() {
        automationThread.interrupt();
        automationThread = new Thread(this::pickUpSpecimen);
        automationThread.start();
    }

    public void prepareSpecimenDepositAsync() {
        automationThread.interrupt();
        automationThread = new Thread(this::prepareSpecimenDeposit);
        automationThread.start();
    }

    public void postSpecimenPickUpAsync() {
        automationThread.interrupt();
        automationThread = new Thread(this::postSpecimenPickUp);
        automationThread.start();
    }

    public void rollerIntakeEjectAndIntake() {
        automationThread.interrupt();
        automationThread = new Thread(() -> {
            try {
                rollerIntakeController.eject();
                Thread.sleep(600);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            rollerIntakeController.intake();
        });
        automationThread.start();
    }

    /**
     * postSpecimenPickUp() and rest().
     */
    protected void postPickUpAndRestAsync() {
        automationThread.interrupt();
        automationThread = new Thread(() -> {
            try {
                postSpecimenPickUp();
                Thread.sleep(500);
                rest();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        automationThread.start();
    }

    public void depositSpecimenAsync() {
        automationThread.interrupt();
        automationThread = new Thread(this::depositSpecimen);
        automationThread.start();
    }

    public void basketDepositAsync() {
        automationThread.interrupt();
        automationThread = new Thread(this::basketDeposit);
        automationThread.start();
    }

    public void basketDeposit() {
        try {
            isCompressed = false;
            depositWristController.basket();
            verticalSlideController.basket();
            depositElbowController.startAuto_deg(90);

            while (!(verticalSlideController.hasReachedTargetPose())) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                Thread.sleep(50);
            }

            depositElbowController.basket();

            while (!depositElbowController.hasReachedTarget()) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void rest() {
        try {
            isCompressed = true;
            if (depositElbowController.toAngle_deg(depositElbowController.getCurrentPose()) < DepositElbowController.REST_POSE_DEG) {
                verticalSlideController.setAutoTargetPose_in(VerticalSlideController.ELBOW_CLEARANCE);
                while (!verticalSlideController.hasReachedTargetPose()) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    Thread.sleep(100);
                }
            }

            horizontalSlideController.rest();
            depositWristController.rest();
            depositClaw.close();
            rollerIntakeController.rest();
            depositElbowController.rest();
            while (!depositElbowController.hasReachedTarget()) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                Thread.sleep(100);
            }

            verticalSlideController.rest();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void transfer() {
        try {
            if (horizontalSlideController.getCurrentPose_in() < HorizontalSlideController.TRANSFER_CLEARANCE_IN) {
                horizontalSlideController.setAutoTargetPose(HorizontalSlideController.TRANSFER_CLEARANCE_IN);
                while (!horizontalSlideController.hasReachedTarget()) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    Thread.sleep(100);
                }
            }

            rollerIntakeController.transferClear();
            depositElbowController.transfer();
            depositWristController.transfer();
            depositClaw.open();
            Thread.sleep(200);
            while (!depositElbowController.hasReachedTarget()) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                Thread.sleep(50);
            }

            verticalSlideController.transfer();
            while (!verticalSlideController.hasReachedTargetPose()) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                Thread.sleep(50);
            }

            horizontalSlideController.setAutoTargetPose(HorizontalSlideController.TRANSFER_POSE_IN);
            while (!horizontalSlideController.hasReachedTarget()) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                Thread.sleep(50);
            }

            rollerIntakeController.transferUp();
            Thread.sleep(200);
            rollerIntakeController.trueTransferPose();
            Thread.sleep(200);
            depositClaw.close();

            Thread.sleep(300);

            // Give room for elbow to rotate up
            horizontalSlideController.setAutoTargetPose(HorizontalSlideController.TRANSFER_CLEARANCE_IN);
            while (!horizontalSlideController.hasReachedTarget()) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                Thread.sleep(50);
            }

            depositWristController.rest();
            depositElbowController.rest();
            while (!depositElbowController.hasReachedTarget()) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                Thread.sleep(50);
            }

            horizontalSlideController.setAutoTargetPose(0);
            depositClaw.close();
            rollerIntakeController.rest();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void pickUpSpecimen() {
        try {
            isCompressed = true;

            if (depositElbowController.toAngle_deg(depositElbowController.getCurrentPose()) > -10) {
                verticalSlideController.setAutoTargetPose_in(VerticalSlideController.ELBOW_CLEARANCE);
                while (!verticalSlideController.hasReachedTargetPose()) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    Thread.sleep(100);
                }
            }

            depositWristController.pickUpSpecimen();
            depositElbowController.pickUpSpecimen();
            while (!depositElbowController.hasReachedTarget()) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                Thread.sleep(50);
            }

            verticalSlideController.pickUpSpecimen();
            depositClaw.open();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void postSpecimenPickUp() {
        try {
            isCompressed = true;
            depositClaw.close();
            verticalSlideController.postSpecimenPickUp();
            depositElbowController.pickUpSpecimen();

            while (!(verticalSlideController.hasReachedTargetPose() && depositElbowController.hasReachedTarget())) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }

                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void prepareSpecimenDeposit() {
        try {


            depositClaw.closeLose();
            depositElbowController.prepareSpecimenDeposit();
            depositWristController.preDepositSpecimen();

            while (!depositElbowController.hasReachedTarget()) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                Thread.sleep(100);
            }

            verticalSlideController.prepareSpecimenDeposit();

            while (!verticalSlideController.hasReachedTargetPose()) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void depositSpecimen() {
        try {
            isCompressed = false;
            depositClaw.close();
            depositWristController.depositSpecimen();
            depositElbowController.depositSpecimen();
            while (!depositElbowController.hasReachedTarget()) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                Thread.sleep(50);
            }

            verticalSlideController.depositSpecimen();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void waitForIdle() {
        try {
            while (automationThread.isAlive() && opModeIsActive()) {
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void mainThreadSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            automationThread.interrupt();
            Thread.currentThread().interrupt();
        }
    }
}
