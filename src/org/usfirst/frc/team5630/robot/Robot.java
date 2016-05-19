package org.usfirst.frc.team5630.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
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
	String FILE_NAME = "/ForwardLimit.dat";
	RobotDrive robotDrive1;
	Joystick joystickInput1;
	Talon intakeDriver;
	DigitalInput intakeBumper;
	Relay spike;
	boolean lightToggle; // For turning on and off light (Robert)
	final int maxIntakeTime = 1500, extraIntake = 2;
	double flySpeed, armTargetPosition, armSpeed, intakeSpeed = 1;
	int autoLoopCounter, flywheelEnable, direction, autoIntakeTimer;
	boolean autoIntakeEnable, autoShooter;
	boolean buttonA, buttonB, buttonX, buttonY, buttonLB, buttonRB, buttonBack, buttonStart, buttonLStick, buttonRStick;
	boolean buttonALast, buttonBLast, buttonXLast, buttonYLast, buttonLBLast, buttonRBLast, buttonBackLast,
			buttonStartLast, buttonLStickLast, buttonRStickLast;
	int buttonPOV;
	// Timer timer;
	double holdPosition;
	int buttonPOVLast;
	int shootTimer = 0;
	boolean autoEnableFlywheel = false; // Set options for features
	CameraServer Camera;
	CANTalon flyWheel, arm;
	double reverseLimit;
	double forwardLimit;

	public void robotInit() {

		forwardLimit = -0.93 - 0.01;
		// IMPORTANT
		// This should be the arm position when arm is upright subtracted by
		// 0.04.
		// If this is not done after taking the arm motor off, the autonomous
		// code may break the arm

		reverseLimit = forwardLimit - 0.25;
		// This function is run when the robot is first started up and should be
		// used for any initialization code.
		robotDrive1 = new RobotDrive(0, 1, 2, 3);
		intakeDriver = new Talon(4);
		joystickInput1 = new Joystick(0);
		intakeBumper = new DigitalInput(0);
		flyWheel = new CANTalon(1); // Initialize the CanTalonSRX on device
		// 1.

//		spike = new Relay(0); // Initializes spike relay
//		lightToggle = true;
		// spike.set(Relay.Value.kForward); //Power flows Positive to Negative,
		// light green
		// spike.set(Relay.Value.kOff); //No power flows, light orange

		flyWheel.reset();
		flyWheel.enable();
		flyWheel.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		flyWheel.changeControlMode(CANTalon.TalonControlMode.Speed);
		flyWheel.configEncoderCodesPerRev(1024);
		flyWheel.reverseSensor(false);
		flyWheel.reverseOutput(true);
		flyWheel.configNominalOutputVoltage(+0.0f, -0.0f);
		flyWheel.configPeakOutputVoltage(0.0f, -13.0f);
		flyWheel.setProfile(0);
		flyWheel.setPID(0.31, 0.0006, 0.0000);
		flyWheel.setIZone(5000);
		// The IZone is the area which the I will accumulate. Note, this is in
		// native I units, which I have no idea what they mean. But this works.
		flyWheel.setF(0.0);
		flyWheel.set(0);

		arm = new CANTalon(2); // Initialize the CanTalonSRX on device
		// arm.setEncPosition(arm.getPulseWidthPosition() & 0xFFF);
		arm.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Absolute);
		arm.configEncoderCodesPerRev(1024);
		arm.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		arm.reverseOutput(false);
		arm.reverseSensor(true);
		arm.configNominalOutputVoltage(+0.0f, -0.0f);
		arm.configPeakOutputVoltage(12.0f, -12.0f);
		arm.setAllowableClosedLoopErr(0);
		arm.setProfile(0);
		arm.setPID(1, 0.0003, 0.00003);
		arm.setReverseSoftLimit(reverseLimit);
		arm.enableReverseSoftLimit(true);
		arm.setForwardSoftLimit(forwardLimit);
		arm.enableForwardSoftLimit(true);

		Camera = CameraServer.getInstance();
		Camera.setQuality(30);
		Camera.startAutomaticCapture("cam0");
		// the camera name (ex "cam0") can be found through the roboRIO web
		// interface
		holdPosition = arm.getPosition();
		// Basically copy-pasta'd the code from an example code.
	}

	public void autonomousInit() {
		// This function is run once each time the robot enters autonomous mode
		autoLoopCounter = 0;
		// timer.start();
		arm.changeControlMode(CANTalon.TalonControlMode.Position);
	}

	public void autonomousPeriodic() {
		// This function is called periodically during autonomous

		if (autoLoopCounter < 60) {
			arm.set(forwardLimit - 0.15);
			robotDrive1.arcadeDrive(-0.75, 0.2);
		} else if (autoLoopCounter < 120) {
			robotDrive1.arcadeDrive(-1, 0.2);
		} else if (autoLoopCounter < 160) {
			robotDrive1.arcadeDrive(-0.8, 0.2);
		} else if (autoLoopCounter < 185) {
			robotDrive1.arcadeDrive(-0.8, -0.5);
		} else if (autoLoopCounter < 210) {
			robotDrive1.arcadeDrive(-0.8, 0.5);
		} else if (autoLoopCounter < 235) {
			robotDrive1.arcadeDrive(-0.8, -0.5);
		} else if (autoLoopCounter < 260)
			robotDrive1.arcadeDrive(-0.8, 0.5);
		autoLoopCounter++;
		// First 1.2 seconds: drives forward @ 75% Speed
		// 1.2s to 2.4s: drives foward@100% speed
		// 2.4s to 5.2s: alternates which side of wheels run (To try to get out
		// when stuck on things, should have no impact when not stuck)
	}

	int outputCounter;

	public void teleopInit() {
		// This function is called once each time the robot enters tele-operated
		// mode
		flywheelEnable = 0;
		flySpeed = 4700;
		direction = 1;
		buttonALast = false;
		autoIntakeEnable = false;
		outputCounter = 0;
		autoShooter = false;
		arm.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
	}

	public void teleopPeriodic() {
		// This function is called periodically during operator control
		outputCounter = (outputCounter + 1) % 10;
		buttonA = joystickInput1.getRawButton(1);
		buttonB = joystickInput1.getRawButton(2);
		buttonX = joystickInput1.getRawButton(3);
		buttonY = joystickInput1.getRawButton(4);
		buttonLB = joystickInput1.getRawButton(5);
		buttonRB = joystickInput1.getRawButton(6);
		buttonBack = joystickInput1.getRawButton(7);
		buttonStart = joystickInput1.getRawButton(8);
		buttonLStick = joystickInput1.getRawButton(9);
		buttonRStick = joystickInput1.getRawButton(10);
		buttonPOV = joystickInput1.getPOV();
		// armTargetPosition = arm.getPosition() + (joystickInput1.getRawAxis(3)
		// - joystickInput1.getRawAxis(2)) / 6.5;
		
//		if (joystickInput1.getRawAxis(3) < 0.075 && joystickInput1.getRawAxis(2) < 0.075){
//			if (armSpeed != 0) {
//				holdPosition = arm.getPosition();
//				armSpeed = 0;
//				arm.changeControlMode(CANTalon.TalonControlMode.Position);
//			}
//			arm.set(holdPosition);
//		} else
//		{
//			arm.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
			armSpeed = (joystickInput1.getRawAxis(3) - joystickInput1.getRawAxis(2)) * 0.35;
			arm.set(armSpeed);
//		}
		// I divide by 2.3 so that the arm doesn't move as fast

		if (buttonLStick != buttonLStickLast) {
			if (buttonLStick) {
				arm.enableForwardSoftLimit(false);
				arm.enableReverseSoftLimit(false);
				// } else {
				// forwardLimit = arm.getPosition() - 0.03;
				// reverseLimit = forwardLimit - 0.33;
				//
				// arm.setReverseSoftLimit(reverseLimit);
				// arm.setForwardSoftLimit(forwardLimit);
				// arm.enableReverseSoftLimit(true);
				// arm.enableForwardSoftLimit(true);
				// // PrintWriter writer = null;
				// // // File outFile = new File(FILE_NAME);
				// // try {
				// // writer = new PrintWriter(FILE_NAME, "UTF-8");
				// // } catch (FileNotFoundException e) {
				// // // TODO Auto-generated catch block
				// // e.printStackTrace();
				// // } catch (UnsupportedEncodingException e) {
				// //
				// // }
				// // writer.println(forwardLimit);

				// The not commented out code disables the soft limits.
				// PERNAMENTLY, IF CLICKED DURING MATCH, SOFT LIMITS WILL NOT BE
				// BACK UNTIL ENTIRE CODE IS RESTARTED.
			}
		}

		if (autoIntakeEnable) {
			intakeDriver.set(-intakeSpeed);
			autoIntakeTimer++;
			if (intakeBumper.get() == false && autoIntakeTimer < maxIntakeTime - extraIntake) {
				// Bumper has been pressed

				// Set the autoIntakeTimer to maximum time (minus the extra run
				// time to get ball off the ground)
				autoIntakeTimer = maxIntakeTime - extraIntake;

				// Enable flywheel
				if (autoEnableFlywheel)
					flywheelEnable = 1;
			}

			if (autoIntakeTimer > maxIntakeTime || (buttonBack && !buttonBackLast) || buttonLB || buttonRB) {
				// If something else that controls the flyWheel is pressed, do
				// not start the autointake function
				autoIntakeEnable = false;
				intakeDriver.set(0);
			}
		} else if (buttonBack && !buttonBackLast) {
			autoIntakeEnable = true;
			autoIntakeTimer = 0;
		}

		/*
		 * Spike code
		 */
//		if (buttonRStick != buttonRStickLast && buttonRStick) {
//			if (lightToggle) {
//				spike.set(Relay.Value.kForward);
//				lightToggle = false;
//			} else {
//				spike.set(Relay.Value.kOff);
//				lightToggle = true;
//			}
//
//		}
		// Spike code for light (above)

		if (buttonRB) {
			intakeDriver.set(-intakeSpeed);
		} else if (buttonLB) {
			intakeDriver.set(intakeSpeed / 2);
			// Out speed is halved so the ball doesn't get ejected by driver as
			// easily
		} else if (!autoIntakeEnable) {
			intakeDriver.set(0);
		}

		if (!buttonXLast && buttonX && flySpeed > 0) {
			flySpeed = flySpeed - 50;
		} else if (!buttonBLast && buttonB && flySpeed < 5500) {
			flySpeed = flySpeed + 50;
		}

		// if (buttonPOV == 0) {
		// arm.set(forwardLimit);
		// } else if (buttonPOV == 180) {
		// arm.set(reverseLimit);
		// } else if (Math.abs(armSpeed) > 0.1) {

		// }
		//
		// if (buttonPOV == 90 && buttonPOVLast != buttonPOV) {
		// arm.setI(arm.getI() + 0.0001);
		// } else if (buttonPOV == 270 && buttonPOVLast != buttonPOV) {
		// arm.setI(arm.getI() - 0.0001);
		// }
		if (buttonALast != buttonA) {
			// Enables Toggling of Flywheel
			buttonALast = buttonA;
			if (buttonALast == true)
				flywheelEnable = (flywheelEnable + 1) % 2; // Toggles Flywheel
															// // between 0 and
															// 1
		}
		if (flywheelEnable == 1) {
			flyWheel.changeControlMode(CANTalon.TalonControlMode.Speed);
			flyWheel.set(flySpeed);
		} else {
			flyWheel.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
			flyWheel.set(0);
		}

		if (buttonStartLast != buttonStart) {
			buttonStartLast = buttonStart;
			if (buttonStartLast == true)
				direction = -direction;
		}

		if (buttonY != buttonYLast && buttonY && flywheelEnable == 1) {
			autoShooter = true;
			// THe autoShooter boolean tells the program to wait for the
			// flyWheel to get within 2 of target speed
		}
		if (autoShooter && Math.abs(flyWheel.getSpeed() - flySpeed) < 2) {
			{
				// Once within 2 of target speed, autoShooter is no longer
				// needed, so it is false.
				// It started a timer for the intake to push the ball forwards
				// for 0.8s
				shootTimer = 40;
				autoShooter = false;
			}
		}
		if (shootTimer > 1) {
			intakeDriver.set(-intakeSpeed);
			shootTimer--;
		} else if (shootTimer == 1) {
			shootTimer--;
			flywheelEnable = 0;
			// Disables the flyWheel after the shot
		}

		robotDrive1.arcadeDrive(direction * joystickInput1.getRawAxis(1), -joystickInput1.getRawAxis(4));

		if (outputCounter == 0) {
			if (Math.abs(flyWheel.getSpeed() - flySpeed) < 10) {
				System.out.println("\n\n\n\n\n\nYOU'RE GOOD TO SHOOT\nYOU'RE GOOD TO SHOOT\n\n\tArm Position:   "
						+ arm.getPosition() + "\tArm TargetPos:" + (arm.getPulseWidthPosition() & 0xFFF)
						+ "\n\nTargetFlySpeed:" + flySpeed + "\nWheel Speed:   " + flyWheel.getSpeed()
						+ "\nAutoshooter: " + autoShooter);
			} else {
				System.out.println("\n\n\n\n\n\n\n\n\n\tArm Position:   " + arm.getPosition() + "\n\nTargetFlySpeed:"
						+ flySpeed + "\nWheel Speed:   " + flyWheel.getSpeed());
			}
		}
		buttonALast = buttonA;
		buttonBLast = buttonB;
		buttonXLast = buttonX;
		buttonYLast = buttonY;
		buttonLBLast = buttonLB;
		buttonRBLast = buttonRB;
		buttonBackLast = buttonBack;
		buttonStartLast = buttonStart;
		buttonPOVLast = buttonPOV;
		buttonLStickLast = buttonLStick;
		buttonRStickLast = buttonRStick;

	}

	public void testPeriodic() {
		// This function is called periodically during test mode
		LiveWindow.run();
	}

}
