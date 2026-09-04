# SBS Programmer Tryout 2026

This repository is the starter project for the SBS Robotics programmer tryout. The tryout focuses on your problem-solving process: how you understand a control problem, test an idea, use measurements, debug, tune, ask for help, and explain your decisions.

## Before the Tryout

- Install Android Studio Ladybug 2024.2 or later.
- In Android Studio, use the bundled JDK 21 as the Gradle JDK.
- Install Android SDK Platform 34 and Build Tools 34.
- Bring your laptop and charger.
- Setup and dependency-download time will not count against your tryout time. The timed portion begins after this project builds successfully.

## Robot and Code Context

- Programming environment: Android Studio, Java, FTC Robot Controller SDK 10.2.0.
- Code location: `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/tryout`.
- Your two files are `TryoutPidTurn.java` and `TryoutEncoderForward.java`.
- Each program must be an `@Autonomous` OpMode extending `LinearOpMode`.
- The robot uses a four-motor mecanum drivetrain.
- The physical front is the roller-intake end of the robot.

### Hardware configuration

| Device | Configuration name | Direction |
| --- | --- | --- |
| Front-left drive motor | `lf` | REVERSE |
| Front-right drive motor | `rf` | FORWARD |
| Back-left drive motor | `lb` | REVERSE |
| Back-right drive motor | `rb` | FORWARD |
| IMU | `imu` | — |

- Set all four drive motors to `BRAKE` at zero power.
- Control Hub orientation: logo faces LEFT and USB faces UP.
- Wheel diameter: 4.094 inches.
- Encoder resolution: 384.5 ticks per wheel revolution.
- Drive gear ratio: 1:1.
- Nominal conversion: approximately 29.9 ticks per inch. Real-world calibration is allowed and encouraged.

## Rules and Resources

- You may use AI, documentation, and existing source code as references.
- You must understand, test, and explain any code you write or adapt.
- Your implementation must stay inside the two files in the `tryout` package.
- Do not import, instantiate, or call anything from `org.firstinspires.ftc.teamcode.champion`.
- Do not use `AdvancedPidController`, Road Runner, FTCLib, Pinpoint, or other third-party control/localization libraries.
- Your tryout code may use the FTC SDK and the Java standard library.
- Every movement loop must check `opModeIsActive()`, include a timeout, and stop all four motors when it finishes or times out.
- Work only in your local copy. Do not push the code to GitHub.

## Task 1: PID Turning

Complete `TryoutPidTurn.java`.

1. Initialize the drivetrain and IMU.
2. At Start, reset the IMU yaw to zero.
3. Turn to **+90 degrees of FTC yaw** and stop as accurately as possible.
4. Use IMU heading as the feedback measurement.

The integral term is optional. Remember to handle angular error correctly near the `-180/+180` boundary.

```text
integral += error * dt
derivative = (error - previousError) / dt
output = kP * error + kI * integral + kD * derivative
```

## Task 2: Precise Forward Movement

Complete `TryoutEncoderForward.java`.

1. Move **24 inches forward**, toward the roller-intake end.
2. Use drive-motor encoders as the distance measurement.
3. Stop as accurately and consistently as possible.
4. You may also use the IMU to hold a straight heading, but not to replace encoder-based distance measurement.

## Evaluation

The tryout provides at least 120 minutes of programming and robot-testing time. Each task will be run three times to observe accuracy and consistency. Evaluation is qualitative; a perfect result matters less than a strong process.

Anna, Sarah, and Gavin will be available to help and observe how you:

- Understand and break down the problem.
- Build a safe first test.
- Use telemetry and measurements.
- Change one thing at a time and explain why.
- Debug unexpected behavior and ask useful questions.
- Explain the final design and its limitations.

## Staff Note

`STAFF - Champion TeleOp` exists only so staff can operate and verify the complete robot. Candidates must not edit or call its classes.

## Source and License

Prepared from [LegedaryKing09/SBSChampion-2025](https://github.com/LegedaryKing09/SBSChampion-2025) at commit `4f7ae354bcf39a9fb34d00bad198ccf389530db3`, based on the FIRST Tech Challenge Robot Controller SDK. See `LICENSE` for the retained FIRST license terms.
