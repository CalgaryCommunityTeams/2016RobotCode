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
	RobotDrive robotDrive1;
	Joystick joystickInput1;
	Talon flywheelDriver, intakeDriver, armDriver;
	DigitalInput intakeBumper;
	final int maxIntakeTime = 1500, extraIntake = 7;
	double flySpeed, armSpeed, intakeSpeed = 0.5;
	int autoLoopCounter, flywheelEnable, direction, autoIntakeTimer;
	boolean buttonStartLast, buttonALast, autoIntakeEnable, buttonBackLast = false;
	boolean buttonA, buttonB, buttonX, buttonY, buttonLB, buttonRB, buttonBack, buttonStart;
	CameraServer Camera;
	// CANTalon flyWheel;

	public void robotInit() {
		// This function is run when the robot is first started up and should be used for any initialization code.
		robotDrive1 = new RobotDrive(0, 1, 2, 3);
		flywheelDriver = new Talon(4);
		intakeDriver = new Talon(5);
		armDriver = new Talon(6);
		joystickInput1 = new Joystick(0);
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

	public void autonomousInit() {
		// This function is run once each time the robot enters autonomous mode
		autoLoopCounter = 0;
	}

	public void autonomousPeriodic() {
		// This function is called periodically during autonomous
	}

	public void teleopInit() { 
		// This function is called once each time the robot enters tele-operated mode
		flywheelEnable = 0;
		flySpeed = 0.8;
		direction = 1;
		buttonALast = false;
		autoIntakeEnable = false;
		Camera = CameraServer.getInstance();
		Camera.setQuality(50);
		Camera.startAutomaticCapture("cam0");
	}

	public void teleopPeriodic() {
		// This function is called periodically during operator control
		buttonA = joystickInput1.getRawButton(1);
		buttonB = joystickInput1.getRawButton(2);
		buttonX = joystickInput1.getRawButton(3);
		buttonY = joystickInput1.getRawButton(4);
		buttonLB = joystickInput1.getRawButton(5);
		buttonRB = joystickInput1.getRawButton(6);
		buttonBack = joystickInput1.getRawButton(7);
		buttonStart = joystickInput1.getRawButton(8);

		armSpeed = joystickInput1.getRawAxis(3) - joystickInput1.getRawAxis(2);

		if (autoIntakeEnable == true) {
			intakeDriver.set(-intakeSpeed);
			autoIntakeTimer++;
			if (intakeBumper.get() == false && autoIntakeTimer < maxIntakeTime - extraIntake) {
				// Bumper has been pressed
				
				// Set the autoIntakeTimer to maximum time (minus the extra run time to get ball off the ground)
				autoIntakeTimer = maxIntakeTime - extraIntake;
				
				// Enable flywheel
				flywheelEnable = 1;
			}

			if (autoIntakeTimer > maxIntakeTime || (buttonBack && !buttonBackLast) || buttonLB || buttonRB) {
				autoIntakeEnable = false;
				intakeDriver.set(0);
			}
		} else if (buttonBack && !buttonBackLast) {
			autoIntakeEnable = true;
			autoIntakeTimer = 0;
		}
		
		if (buttonRB) {
			intakeDriver.set(-intakeSpeed);
		} else if (buttonLB) {
			intakeDriver.set(intakeSpeed);
		} else if (!autoIntakeEnable) {
			intakeDriver.set(0);
		}

		if (buttonX == true && buttonB == false && buttonY == false) {
			// flySpeed = 4000; //The motor doesn't reach 4000 RPM
			flySpeed = 1.0;
		} else if (buttonY == true && buttonB == false && buttonX == false) {
			// flySpeed = 1200; //Maxs out at 1200 RPM
			flySpeed = 0.8;
		} else if (buttonB == true && buttonX == false && buttonY == false) {
			// flySpeed = 600;
			flySpeed = 0.6;
		}

		if (buttonALast != buttonA) { 
			// Enables Toggling of Flywheel
			buttonALast = buttonA;
			if (buttonALast == true)
				flywheelEnable = (flywheelEnable + 1) % 2; // Toggles Flywheel between 0 and 1
		}
		
		if (buttonStartLast != buttonStart) {
			buttonStartLast = buttonStart;
			if (buttonStartLast == true)
				direction = -direction; 
		}
		
		if (flywheelEnable == 1) {
			flywheelDriver.set(flySpeed);
		} else
			flywheelDriver.set(0);

		buttonBackLast = buttonBack;
		armDriver.set(armSpeed / 2);
		
		robotDrive1.arcadeDrive(direction * joystickInput1.getRawAxis(1), -joystickInput1.getRawAxis(4));
	}

	public void testPeriodic() { 
		// This function is called periodically during test mode
		LiveWindow.run();
	}

}
