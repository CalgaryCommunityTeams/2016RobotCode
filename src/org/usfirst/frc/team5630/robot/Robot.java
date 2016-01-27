package org.usfirst.frc.team5630.robot;

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
	Talon flywheel;
	Talon intake;
	Joystick stick;
	double intakeSpeed;
	int autoLoopCounter;
	boolean flywheelRunLast;
	boolean runFlywheel;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		flywheel = new Talon(4); // Example, change 4 to real number later
		myRobot = new RobotDrive(0, 1, 2, 3);
		stick = new Joystick(0);
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
		if (autoLoopCounter < 100) // Check if we've completed 100 loops
									// (approximately 2 seconds)
		{
			myRobot.drive(-0.5, 0.0); // drive forwards half speed
			autoLoopCounter++;
		} else {
			myRobot.drive(0.0, 0.0); // stop robot
		}
	}

	/**
	 * This function is called once each time the robot enters tele-operated
	 * mode
	 */
	public void teleopInit() {
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		runFlywheel = stick.getRawButton(0);// Set this to be the flywheel imput
											// (Bool)
		intakeSpeed = stick.getZ(); // Left Trigger

		if (flywheelRunLast != runFlywheel) { // Enables Togglling
			flywheelRunLast = runFlywheel;
		}
		if (runFlywheel) {
			flywheel.set(0.5); // Flywheel Speed is changed here
		} else {
			flywheel.set(0.0);
		}

		intakeSpeed = (intakeSpeed / 2) + 0.5;
		intake.set(intakeSpeed);

		myRobot.arcadeDrive(stick);
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
		LiveWindow.run();
	}

}
