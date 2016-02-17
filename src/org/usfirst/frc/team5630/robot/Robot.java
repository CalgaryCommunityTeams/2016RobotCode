package org.usfirst.frc.team5630.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	RobotDrive myRobot;
	Joystick stick;
	Talon flyWheel, intake, arm;
	DigitalInput intakeBumper;
	final int maxIntakeTime = 1500, extraIntake = 7;
	double flySpeed, armSpeed, intakeSpeed = 0.5;
	int autoLoopCounter, flyToggle, direction, autoIntakeTimer;
	boolean buttonStartLast, buttonALast, autoIntake, buttonBackLast = false;
	boolean buttonA, buttonB, buttonX, buttonY, buttonLB, buttonRB, buttonBack, buttonStart;
	CameraServer Camera;
	// CANTalon flyWheel;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		myRobot = new RobotDrive(0, 1, 2, 3);
		flyWheel = new Talon(4);
		intake = new Talon(5);
		arm = new Talon(6);
		stick = new Joystick(0);
		intakeBumper = new DigitalInput(0);
		// flyWheel = new CANTalon(0); // Initialize the CanTalonSRX on device
		// 1.
		// flyWheel.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
		// flyWheel.changeControlMode(CANTalon.TalonControlMode.Speed);
		// flyWheel.reverseSensor(false);
		// flyWheel.configNominalOutputVoltage(+0.0f, -0.0f);
		// flyWheel.configPeakOutputVoltage(+12.0f, -0.0f);
		// flyWheel.setProfile(0);
		// flyWheel.setPID(0.22, 0.0, 0.0);
		// flyWheel.setF(0.1097);

		// the camera name (ex "cam0") can be found through the roboRIO web
		// interface

		// Basically copy-pasta'd the code from an example code.
	}

	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	public void autonomousInit() {
		autoLoopCounter = 0;
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
	}

	/**
	 * This function is called once each time the robot enters tele-operated
	 * mode
	 */
	public void teleopInit() {
		flyToggle = 0;
		flySpeed = 0.8;
		direction = 1;
		buttonALast = false;
		autoIntake = false;
		Camera = CameraServer.getInstance();
		Camera.setQuality(50);
		Camera.startAutomaticCapture("cam0");
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		buttonA = stick.getRawButton(1);
		buttonB = stick.getRawButton(2);
		buttonX = stick.getRawButton(3);
		buttonY = stick.getRawButton(4);
		buttonLB = stick.getRawButton(5);
		buttonRB = stick.getRawButton(6);
		buttonBack = stick.getRawButton(7);
		buttonStart = stick.getRawButton(8);

		armSpeed = stick.getRawAxis(3) - stick.getRawAxis(2);

		if (autoIntake == true) {
			intake.set(-intakeSpeed);
			autoIntakeTimer++;
			if (intakeBumper.get() == false && autoIntakeTimer < maxIntakeTime - extraIntake) {
				autoIntakeTimer = maxIntakeTime - extraIntake;
			}

			if (autoIntakeTimer > maxIntakeTime || (buttonBack && !buttonBackLast) || buttonLB || buttonRB) {
				autoIntake = false;
				intake.set(0);
			}
		} else if (buttonBack && !buttonBackLast) {
			autoIntake = true;
			autoIntakeTimer = 0;
		}
		if (buttonRB) {
			intake.set(-intakeSpeed);
		} else if (buttonLB) {
			intake.set(intakeSpeed);
		} else if (!autoIntake) {
			intake.set(0);
		}

		if (buttonX == true && buttonB == false && buttonY == false) {
			// flySpeed = 4000;//The motor doesn't reach 4000 RPM
			flySpeed = 1.0;
		} else if (buttonY == true && buttonB == false && buttonX == false) {
			// flySpeed = 1200; //Maxs out at 1200 RPM
			flySpeed = 0.8;
		} else if (buttonB == true && buttonX == false && buttonY == false) {
			// flySpeed = 600;
			flySpeed = 0.6;
		}

		if (buttonALast != buttonA) { // Enables Toggling
			buttonALast = buttonA;
			if (buttonALast == true)
				flyToggle = (flyToggle + 1) % 2; // If the counter is 0, it
													// becomes 1, if the counter
													// is 1, it becomes 0 -C.
													// Zheng 2016-1-28
		}
		if (buttonStartLast != buttonStart) {
			buttonStartLast = buttonStart;
			if (buttonStartLast == true)
				direction = -direction; // If the counter is 0, it becomes 1, if
										// the counter is 1, it becomes 0 -C.
										// Zheng 2016-1-28
		}
		if (flyToggle == 1) {
			flyWheel.set(flySpeed); // flyWheel Speed is changed here
			// This is 1.0 because we tested it at this value and nothing f*cked
		} else
			flyWheel.set(0);

		buttonBackLast = buttonBack;
		arm.set(armSpeed / 2);
		myRobot.arcadeDrive(direction * stick.getY(), -stick.getX());

	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
		LiveWindow.run();
	}

}
