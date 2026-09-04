# SBS Programmer Tryout 2026

Write two separate autonomous programs for the SBS four-motor mecanum robot: an IMU-based 90-degree turn and an encoder-based 24-inch forward movement. We care about how you test, debug, use measurements, and explain your decisions.

## Before the Tryout

- Install Android Studio Ladybug 2024.2 or a compatible newer version.
- Set the Gradle JDK to JDK 21 in Android Studio. Do not upgrade the project's Gradle or Android Gradle Plugin when prompted.
- Install Android SDK Platform 34 and Build Tools 34.0.0.
- Bring your laptop and charger.
- Extract the supplied ZIP into a normal folder, then open the project folder containing `settings.gradle` in Android Studio. Allow Gradle to sync and download dependencies while you have internet access.
- Build the `TeamCode` app before the timed portion. Setup and dependency-download time will not count against your tryout time.

Repository: https://github.com/Gavinx1029/SBS-Programmer-Tryout-2026

## Your Files

Programming environment: Java and FTC Robot Controller SDK 10.2.0.

Work in `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/Auton`:

- `TryoutPidTurn.java`
- `TryoutEncoderForward.java`

These files contain only a package declaration and imports. Add your own class, matching the filename, with `@Autonomous` and `LinearOpMode`, and implement the program yourself. You may add or remove FTC SDK and Java standard-library imports as needed. Put any helper methods inside these two files.

The untouched starter builds, but it deliberately contains no selectable team OpModes. Your programs will appear in the Driver Station after you implement and register them with `@Autonomous` and deploy the `TeamCode` app. Staff will help you connect to the robot, deploy, and select the appropriate program.

## Robot and Hardware

The physical front is the roller-intake end. Left and right are viewed from behind the robot, facing forward.

| Device | Configuration name | Motor direction |
| --- | --- | --- |
| Front-left drive motor | `lf` | REVERSE |
| Front-right drive motor | `rf` | FORWARD |
| Back-left drive motor | `lb` | REVERSE |
| Back-right drive motor | `rb` | FORWARD |
| IMU | `imu` | Not applicable |

- Use `BRAKE` for all four drive motors at zero power.
- Control Hub orientation: logo faces LEFT and USB faces UP relative to the robot.
- Wheel diameter: 4.094 inches.
- Encoder resolution: 384.5 ticks per wheel revolution.
- Drive gear ratio: 1:1.
- Use these specifications to convert encoder readings to distance. You may measure and calibrate on the robot.

Staff will provide the matching robot configuration. Ask staff if the hardware does not match these details.

## Task 1 PID Turning

Complete `TryoutPidTurn.java` as a separate autonomous program.

- Reset IMU yaw at Start, with the robot stationary.
- Turn to +90 degrees relative to that starting heading, using IMU feedback, then stop.
- Positive FTC yaw is counterclockwise when viewed from above.
- Use your own feedback-control implementation. The integral term is optional.
- Aim for an accurate, repeatable final heading and explain your tuning choices.

## Task 2 Precise Forward Movement

Complete `TryoutEncoderForward.java` as a separate autonomous program.

- Move 24 inches forward, toward the roller-intake end, from the starting position, then stop.
- Use drive-motor encoders to measure distance.
- You may use the IMU to maintain a straight heading, but distance must be measured with encoders.
- Aim for an accurate, repeatable final position. Choose and explain your control approach.

## Rules and Safe Testing

- Use only the FTC SDK and Java standard library. Do not add third-party control or localization libraries.
- AI, documentation, and existing source code may be used as references. You must understand, test, and explain any code you write or adapt.
- Keep your implementation inside the two files in `Auton`. Do not change the SDK, build files, or robot configuration.
- Wait for Start before commanding any movement.
- Every movement loop must check `opModeIsActive()` and have a timeout. Stop all four drive motors when a task finishes, times out, or is stopped.
- Start with low-power tests under staff supervision. Keep the test area clear; use the Driver Station Stop button if the robot behaves unexpectedly.
- Work in your local copy. Do not push solutions to GitHub. Show your two source files and explain them to staff at the end.

## Evaluation

You will have at least 120 minutes of programming and robot-testing time. Staff will coordinate access to the shared robot. Each task will be run three times from a marked starting position on the same surface.

Staff will measure final heading or forward distance after the robot stops, using the same robot reference points each time. We will observe physical accuracy, consistency, and safe stopping, along with your telemetry and explanations. There is no requirement for a perfect result.

Anna, Sarah, and Gavin will help and observe how you:

- Understand and break down the problem.
- Plan and carry out a safe first test.
- Use telemetry and physical measurements.
- Adjust your approach based on results.
- Debug unexpected behavior and ask useful questions.
- Explain your final design and its limitations.

## Source and License

Prepared from [LegedaryKing09/SBSChampion-2025](https://github.com/LegedaryKing09/SBSChampion-2025) at commit `4f7ae354bcf39a9fb34d00bad198ccf389530db3`, based on the FIRST Tech Challenge Robot Controller SDK. See `LICENSE` for the retained FIRST license terms.
