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
	Talon arm;
	Joystick stick;
	double intakeSpeed;
	double armSpeed;
	int autoLoopCounter;
	boolean flywheelRunLast;
	boolean runFlywheel;
	int counter;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		myRobot = new RobotDrive(0, 1, 2, 3);
		flywheel = new Talon(4);
		intake = new Talon(5);
		arm = new Talon(6);
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
		counter = 0;
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		runFlywheel = stick.getRawButton(1);// Set this to be the flywheel input (Bool)
		
		armSpeed = stick.getRawAxis(3) - stick.getRawAxis(2);// Set this to be the armSpeed input (double)
		
		intakeSpeed = stick.getRawAxis(5) / 2; // Set this to be the intakeSpeed input (double)
		//This is to reduce the speed of the intake motor
	
		if (flywheelRunLast != runFlywheel) { // Enables Togglling
			flywheelRunLast = runFlywheel;
			if (flywheelRunLast == true)
				counter = (counter + 1) % 2; // If the counter is 0, it becomes 1, if the counter is 1, it becomes 0 -C. Zheng 2016-1-28
		}

		if (counter == 1) {
			flywheel.set(1.0); // flywheel Speed is changed here 
			//This is 1.0 because we tested it at this value and nothing f*cked up -C. Zheng 2016-1-28
		
		} else {
			flywheel.set(0.0); // flywheel stops
		}
		//intakeSpeed = (intakeSpeed / 2) + 0.5;
		//armSpeed = (armSpeed / 2) + 0.5;
		//Commented because these were based off the Trigger buttons going from -1 to 1, with -1 being the "not pressed" position. This is not the case. The triggers go from 0 to 1.
		intake.set(intakeSpeed);
		arm.set(armSpeed);
		myRobot.arcadeDrive(stick);
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
		LiveWindow.run();
	}

}
