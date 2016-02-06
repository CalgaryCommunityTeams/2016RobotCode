package org.usfirst.frc.team5630.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
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
	double flySpeed, armSpeed, intakeSpeed;
	int autoLoopCounter, flyToggle, direction;
	boolean directionToggle, directionToggleLast, runFlywheel, flywheelRunLast;
	boolean rawButton1, rawButton2, rawButton3, rawButton4, rawButton5, rawButton6, rawButton7, rawButton8;
	// CANTalon flyWheel;
	CameraServer Camera;

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
		Camera = CameraServer.getInstance();
		Camera.setQuality(50);
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
		flyToggle = 0;
		flySpeed = 0.8;
		direction = 1;
		flywheelRunLast = false;
		Camera.startAutomaticCapture("cam0");
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		rawButton1 = stick.getRawButton(1);//Used to have a bunch of stick.getRawButton(x) in code
		rawButton2 = stick.getRawButton(2);//It's good practice to put these in variables
		rawButton3 = stick.getRawButton(3);
		rawButton4 = stick.getRawButton(4);
		rawButton5 = stick.getRawButton(5);
		rawButton6 = stick.getRawButton(6);
		rawButton7 = stick.getRawButton(7);//rawButton7 is unused, but solves code OCD -Alexander
		rawButton8 = stick.getRawButton(8);
		
		runFlywheel = getRawButton(1);// Set this to be the flyWheel input
											// (Boolean)
		directionToggle = rawButton8;
		armSpeed = stick.getRawAxis(3) - stick.getRawAxis(2);// Set this to be
																// the armSpeed
																// input
																// (double)

		if (rawButton5 != rawButton6) {
			if (rawButton5 == true)
				intakeSpeed = 0.5;
			else
				intakeSpeed = -1;
		} else
			intakeSpeed = 0;
		// Set this to be the intakeSpeed input (double)
		// This is to reduce the speed of the intake motor{

		if (rawButton3 == true && rawButton2 == false
				&& rawButton4 == false) {
			// flySpeed = 4000;//The motor doesn't reach 4000 RPM
			flySpeed = 1.0;
		} else if (rawButton4 == true
				&& rawButton2 == false
				&& rawButton3 == false) {
			// flySpeed = 1200; //Maxs out at 1200 RPM
			flySpeed = 0.8;
		} else if (rawButton2 == true
				&& rawButton3 == false
				&& rawButton4 == false) {
			// flySpeed = 600;
			flySpeed = 0.6;
		}

		if (flywheelRunLast != runFlywheel) { // Enables Togglling
			flywheelRunLast = runFlywheel;
			if (flywheelRunLast == true)
				flyToggle = (flyToggle + 1) % 2; // If the counter is 0, it
													// becomes 1, if the counter
													// is 1, it becomes 0 -C.
													// Zheng 2016-1-28
		}
		if (directionToggleLast != directionToggle) {
			directionToggleLast = directionToggle;
			if (directionToggleLast == true)
				direction = -direction; // If the counter is 0, it becomes 1, if
										// the counter is 1, it becomes 0 -C.
										// Zheng 2016-1-28
		}
		if (flyToggle == 1) {
			flyWheel.set(flySpeed); // flyWheel Speed is changed here
			// This is 1.0 because we tested it at this value and nothing f*cked
			// up -C. Zheng 2016-1-28
			// Changed to -1.0 because it was reversed. -C. Zheng 2016-1-29
			// Now sets the Talon SRX to the set speed
		} else
			flyWheel.set(0); // flywheel stops
		// intakeSpeed = (intakeSpeed / 2) + 0.5;
		// armSpeed = (armSpeed / 2) + 0.5;
		// Commented because these were based off the Trigger buttons going from
		// -1 to 1, with -1 being the "not pressed" position. This is not the
		// case. The triggers go from 0 to 1.
		intake.set(intakeSpeed);
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
