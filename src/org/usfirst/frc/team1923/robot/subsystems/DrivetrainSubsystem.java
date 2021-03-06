package org.usfirst.frc.team1923.robot.subsystems;

import org.usfirst.frc.team1923.robot.RobotMap;
import org.usfirst.frc.team1923.robot.commands.RawDriveCommand;
import org.usfirst.frc.team1923.robot.utils.DriveProfile;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Class that houses the motors and shifters
 */
public class DrivetrainSubsystem extends Subsystem {

	private static final double P_CONSTANT = 0; // TODO: Fill in these values
	private static final double I_CONSTANT = 0;
	private static final double D_CONSTANT = 0;
	private static final double F_CONSTANT = 0;
	private static final boolean LEFT_REVERSED = false; // Reverse the sensor or
														// the motor or both?
	private static final boolean RIGHT_REVERSED = false;
	private static final int MAX_SAFE_SHIFT_SPEED = 100; // RPM

	// Arrays of talons to group them together
	// The 0th element will always be the master Talon, the subsequent ones will
	// follow
	private CANTalon[] leftTalons, rightTalons;

	private DoubleSolenoid shifter;
	private DoubleSolenoid shiftOmnis;

	public DriveProfile dprofile = new DriveProfile(RobotMap.DRIVER_PROFILE);

	public DrivetrainSubsystem() {
		leftTalons = new CANTalon[RobotMap.LEFT_DRIVE_PORTS.length];
		rightTalons = new CANTalon[RobotMap.RIGHT_DRIVE_PORTS.length];

		for (int i = 0; i < RobotMap.LEFT_DRIVE_PORTS.length; i++) {
			leftTalons[i] = new CANTalon(RobotMap.LEFT_DRIVE_PORTS[i]);
		}

		for (int i = 0; i < RobotMap.RIGHT_DRIVE_PORTS.length; i++) {
			rightTalons[i] = new CANTalon(RobotMap.RIGHT_DRIVE_PORTS[i]);
		}

		shifter = new DoubleSolenoid(RobotMap.PCM_MODULE_NUM, RobotMap.SHIFT_FORWARD_PORT,
				RobotMap.SHIFT_BACKWARD_PORT);
		shiftOmnis = new DoubleSolenoid(RobotMap.PCM_MODULE_NUM, RobotMap.OMNI_FORWARD_PORT, RobotMap.OMNI_BACKWARD_PORT);
		setToFollow();
		configPID();
		drive(0, 0, TalonControlMode.PercentVbus);
	}
	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	private void setToFollow() {
		leftTalons[1].changeControlMode(TalonControlMode.Follower);
		leftTalons[1].set(leftTalons[0].getDeviceID());
		leftTalons[2].changeControlMode(TalonControlMode.Follower);
		leftTalons[2].set(leftTalons[0].getDeviceID());

		rightTalons[1].changeControlMode(TalonControlMode.Follower);
		rightTalons[1].set(rightTalons[0].getDeviceID());
		rightTalons[2].changeControlMode(TalonControlMode.Follower);
		rightTalons[2].set(rightTalons[0].getDeviceID());
	}

	/**
	 * Sets the two master talons to a certain mode
	 * 
	 * @param c
	 *            TalonControlMode to be used
	 * 
	 */
	private void setMasterToMode(TalonControlMode c) {
		// Speed, Position, Percent VBus etc.
		leftTalons[0].changeControlMode(c);
		rightTalons[0].changeControlMode(c);
	}

	/**
	 * Configures the PID values of the two master talons
	 * 
	 * Sets the nominal and peak output voltages and sets the sensor and output
	 * reversal flags
	 */
	private void configPID() {

		leftTalons[0].setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		leftTalons[0].reverseSensor(false); // Set to true if reverse rotation
		leftTalons[0].configNominalOutputVoltage(0, 0);
		leftTalons[0].configPeakOutputVoltage(12, -12);

		rightTalons[0].setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		rightTalons[0].reverseSensor(false); // Set to true if reverse rotation
		rightTalons[0].configNominalOutputVoltage(0, 0);
		rightTalons[0].configPeakOutputVoltage(12, -12);

		leftTalons[0].setProfile(0);
		leftTalons[0].setF(F_CONSTANT);
		leftTalons[0].setP(P_CONSTANT);
		leftTalons[0].setI(I_CONSTANT);
		leftTalons[0].setD(D_CONSTANT);

		rightTalons[0].setProfile(0);
		rightTalons[0].setF(F_CONSTANT);
		rightTalons[0].setP(P_CONSTANT);
		rightTalons[0].setI(I_CONSTANT);
		rightTalons[0].setD(D_CONSTANT);

		setMasterToMode(TalonControlMode.Speed);
		leftTalons[0].set(0.0);
		leftTalons[0].reverseOutput(LEFT_REVERSED);

		rightTalons[0].set(0.0);
		rightTalons[0].reverseOutput(RIGHT_REVERSED);
	}

	/**
	 * Directly sets the input value of the motors
	 * 
	 * Use with caution because it doesn't automatically check the control mode
	 * 
	 * @param left
	 *            Left power
	 * @param right
	 *            Right power
	 */
	public void set(double left, double right) {
		leftTalons[0].set(left);
		rightTalons[0].set(right);
	}

	/**
	 * Disables the closed-loop system and allows direct power setting
	 */
	public void disable() {
		setMasterToMode(TalonControlMode.Disabled);
	}

	/**
	 * Sets talon powers with a specific mode
	 * 
	 * @param left
	 *            Left power
	 * @param right
	 *            Right power
	 * @param m
	 *            TalonControlMode to be used
	 */
	public void drive(double left, double right, TalonControlMode m) {
		if (leftTalons[0].getControlMode() != m) {
			setMasterToMode(m);
		}
		set(left, right);
	}

	/**
	 * Resets current position of the encoders.
	 * 
	 * Since SRX Mag encoders are used in relative mode, this allows us to
	 * simplify autons by resetting the home position of the robot
	 */
	public void resetPosition() {
		leftTalons[0].setPosition(0);
		rightTalons[0].setPosition(0);
	}

	public void initDefaultCommand() {
		setDefaultCommand(new RawDriveCommand()); // Temp set to raw drive,
													// could change to speed
													// later on for more control
	}

	public void shiftUp() {
		shifter.set(Value.kForward);
	}

	public void shiftDown() {
		if (safeToShift()) {
			shifter.set(Value.kReverse);
		}
	}
	
	public void shiftUpOmnis(){
		this.shiftOmnis.set(Value.kForward);
	}
	
	public void shiftDownOmnis(){
		this.shiftOmnis.set(Value.kReverse);
	}

	private boolean safeToShift() {
		return Math.max(Math.abs(leftTalons[0].getEncVelocity()),
				Math.abs(rightTalons[0].getEncVelocity())) < MAX_SAFE_SHIFT_SPEED;
	}

	public void stop() {
		drive(0, 0, TalonControlMode.PercentVbus);
	}

}
