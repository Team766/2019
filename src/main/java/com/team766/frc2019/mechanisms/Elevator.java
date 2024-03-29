package com.team766.frc2019.mechanisms;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team766.framework.Mechanism;
import com.team766.frc2019.Robot;
import com.team766.hal.CANSpeedController;
import com.team766.hal.DigitalInputReader;
import com.team766.hal.RobotProvider;
import com.team766.hal.CANSpeedController.ControlMode;



public class Elevator extends Mechanism {

    private CANSpeedController m_lowerElevatorMotor;
    private CANSpeedController m_upperElevatorMotor;
    private DigitalInputReader lowerElevatorMinLimitSwitch;
    private DigitalInputReader lowerElevatorMaxLimitSwitch;
    private DigitalInputReader upperElevatorMinLimitSwitch;
    private DigitalInputReader upperElevatorMaxLimitSwitch;
    public static double DIST_PER_PULSE = Robot.drive.DIST_PER_PULSE;
    private double targetPosition;

    public static int LVL1 = 5000;
    public static int LVL2 = 1115000;
    public static int LVL3 = 2130000;
    public static int MIN_LOWER_HEIGHT = 40000 * 2/3;
    public static int VERY_CLOSE_MIN_LOWER_HEIGHT = 80000 * 2/3;
	public static int NEAR_MIN_LOWER_HEIGHT = 400000 * 2/3;
    private static int NEAR_MAX_LOWER_HEIGHT = 950000;
    private static int MAX_LOWER_HEIGHT = 1250000;
	public static int MIN_UPPER_HEIGHT = 0;
    public static int NEAR_MIN_UPPER_HEIGHT = 200000 * 2/3;
    private static int NEAR_MAX_UPPER_HEIGHT = 880000;
    private static int MAX_UPPER_HEIGHT = 960000;
    private static int MID_HEIGHT_BIG = 1000000;
	private static int MAX_HEIGHT_BIG = 1930000;
	private static int MID_HEIGHT_SMALL = 500000;
    private static int MAX_HEIGHT_SMALL = 900000;//marie
    /*
    public static int LVL1 = 5000;
    public static int LVL2 = 1480000;
    public static int LVL3 = 3000000;
    public static int MIN_LOWER_HEIGHT = 40000;
    public static int VERY_CLOSE_MIN_LOWER_HEIGHT = 100000;
	public static int NEAR_MIN_LOWER_HEIGHT = 400000;
    private static int NEAR_MAX_LOWER_HEIGHT = 1800000;
    private static int MAX_LOWER_HEIGHT = 2130000;
	public static int MIN_UPPER_HEIGHT = 0;
    public static int NEAR_MIN_UPPER_HEIGHT = 200000;
    private static int NEAR_MAX_UPPER_HEIGHT = 880000;
    private static int MAX_UPPER_HEIGHT = 920000;
    private static int MID_HEIGHT_BIG = 1000000;
	private static int MAX_HEIGHT_BIG = 1930000;
	private static int MID_HEIGHT_SMALL = 500000;
    private static int MAX_HEIGHT_SMALL = 900000;//rev a
    */
    public static int MAX_COMBINED_HEIGHT = MAX_LOWER_HEIGHT + MAX_UPPER_HEIGHT;
    public boolean setLow = false;
    public double slowMultiplier;
    public int setPosUpperNear;
    public int setPosLowerNear;
    public double lowerDownSpeed;
    public double upperTarget;
    public double lowerTarget;
    public double currentTargetPosition;
    private int upperDirection;
    private int lowerDirection;
    private int currentUpperDirection;
    private int currentLowerDirection;
    private double upperDistance;
    private double lowerDistance;
    

    
    private boolean setPositionRunning = false;
    public boolean combinedStopTargeting = false;
    public boolean upperStopTargeting = false;
    public boolean lowerStopTargeting = false;
    public boolean hoverAtZero = false;
    private int index = 0;

    public Elevator() {
        m_lowerElevatorMotor = RobotProvider.instance.getTalonCANMotor("elevator.elevatorMotor");
        m_upperElevatorMotor = RobotProvider.instance.getTalonCANMotor("elevator.actuatorMotor");
        lowerElevatorMinLimitSwitch = RobotProvider.instance.getDigitalInput("elevator.lowerMinLimitSwitch");
        lowerElevatorMaxLimitSwitch = RobotProvider.instance.getDigitalInput("elevator.lowerMaxLimitSwitch");
        upperElevatorMinLimitSwitch = RobotProvider.instance.getDigitalInput("elevator.upperMinLimitSwitch");
        upperElevatorMaxLimitSwitch = RobotProvider.instance.getDigitalInput("elevator.upperMaxLimitSwitch");
        m_lowerElevatorMotor.configFactoryDefault();
        m_upperElevatorMotor.configFactoryDefault();
        m_upperElevatorMotor.setInverted(true);
        m_lowerElevatorMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        m_upperElevatorMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        m_lowerElevatorMotor.setSensorPhase(true);
        m_upperElevatorMotor.setSensorPhase(false);
        m_lowerElevatorMotor.configNominalOutputForward(0.0);
        m_lowerElevatorMotor.configNominalOutputReverse(0.0);
        m_lowerElevatorMotor.configPeakOutputForward(1.0);
        m_lowerElevatorMotor.configPeakOutputReverse(-1.0);
        m_upperElevatorMotor.configNominalOutputForward(0.0);
        m_upperElevatorMotor.configNominalOutputReverse(0.0);
        m_upperElevatorMotor.configPeakOutputForward(1.0);
        m_upperElevatorMotor.configPeakOutputReverse(-1.0);
        m_lowerElevatorMotor.config_kP(0, 0.01, 0);
        m_lowerElevatorMotor.config_kI(0, 0.0, 0);
        m_lowerElevatorMotor.config_kD(0, 0.01, 0);
        m_lowerElevatorMotor.config_kF(0, 0.0, 0);
        m_upperElevatorMotor.config_kP(0, 0.01, 0);
        m_upperElevatorMotor.config_kI(0, 0.0, 0);
        m_upperElevatorMotor.config_kD(0, 0.01, 0);
        m_upperElevatorMotor.config_kF(0, 0.0, 0);
        m_lowerElevatorMotor.setNeutralMode(NeutralMode.Brake);
        m_upperElevatorMotor.setNeutralMode(NeutralMode.Brake);
        m_upperElevatorMotor.setPosition(0);
        m_lowerElevatorMotor.setPosition(0);
    }

    // Limit switch code
    
    public void hover() {
        Robot.elevator.setLowerPosition(Math.max(Robot.elevator.getLowerHeight(),0.0));
    }

    public boolean getLowerMinLimitSwitch() {
        return lowerElevatorMinLimitSwitch.get();
    }

    public boolean getLowerMaxLimitSwitch() {
        return lowerElevatorMaxLimitSwitch.get();
    }

    public boolean getUpperMinLimitSwitch() {
        return upperElevatorMinLimitSwitch.get();
    }

    public boolean getUpperMaxLimitSwitch() {
        return upperElevatorMaxLimitSwitch.get();
    }


    public void resetUpperEncoder() {
        m_upperElevatorMotor.setPosition(0);
    }

    public void resetLowerEncoder() {
        m_lowerElevatorMotor.setPosition(0);
    }

    public void setLowerPower(double elevatorPower) {
        //System.out.println("*** setting lower power to " + elevatorPower + " ***");
        m_lowerElevatorMotor.set(ControlMode.PercentOutput, elevatorPower);
    }

    public void setUpperPower(double actuatorPower) {
        //System.out.println("*** setting upper power to " + actuatorPower + " ***");
        m_upperElevatorMotor.set(ControlMode.PercentOutput, actuatorPower);
    }

    public void setLowerPosition(double position) {
        System.out.println("*** setting lower position to " + position + " ***");
        m_lowerElevatorMotor.set(ControlMode.Position, position);
    }
    
    public void setUpperPosition(double position) {
        System.out.println("*** setting upper position to " + position + " ***");
        m_upperElevatorMotor.set(ControlMode.Position, position);
    }

    public double getTargetPosition() {
        return currentTargetPosition;
    }

    
    public void setCombinedPosition(double position) {
        currentTargetPosition = position;
        if (currentTargetPosition <= 100000) {
            setLow = true;
            setPosUpperNear = NEAR_MIN_UPPER_HEIGHT;
            setPosLowerNear = NEAR_MIN_LOWER_HEIGHT;
            slowMultiplier = 0.3;
            lowerDownSpeed = 0.9;
        } else {
            setPosUpperNear = 150000;
            setPosLowerNear = 100000;
            slowMultiplier = 0.6;
            lowerDownSpeed = 1.0;

        }
        if (position < 0) {
            return;
        }
        upperTarget = 3*position/7;
        lowerTarget = 4*position/7;
        /*
        upperTarget = 92*position/305;
        lowerTarget = 213*position/305;
        */
        if (lowerTarget > getLowerHeight()) {
            lowerDirection = 1;
        } else {
            lowerDirection = -1;
        }

        if (upperTarget > getUpperHeight()) {
            upperDirection = 1;
        } else {
            upperDirection = -1;
        }
    }

    public void movePosition() {
        if (currentTargetPosition < 0 || currentTargetPosition > (double)(MAX_LOWER_HEIGHT + MAX_UPPER_HEIGHT)) {
            return; 
        }
        setPositionRunning = true;
        combinedStopTargeting = false;

      //  System.out.println("Setting lower position to: " + lowerTarget + ", target (U, L): " +upperTarget + ", " + lowerTarget);
                if ( upperTarget > 0 ) {
                    upperDistance = Math.abs(upperTarget - getUpperHeight());
                    if (upperDistance < setPosUpperNear) {
                        if (upperTarget > getUpperHeight()) {
                            currentUpperDirection = 1;
                        } else {
                            currentUpperDirection = -1;
                        }
                        setUpperPower(slowMultiplier * upperDirection);
                        if ((upperDirection != currentUpperDirection || !getUpperMinLimitSwitch())) {
                            System.out.println("Stop upper");
                            setUpperPower(0.0);
                            upperTarget = -1;
                        }
                    } else {
                        setUpperPower(1.0 * upperDirection);
                    }
                }
                
                
                if ( lowerTarget > 0 ) {
                lowerDistance  = Math.abs(lowerTarget - getLowerHeight());
                if (lowerDistance < setPosLowerNear) {
                    setLowerPower(slowMultiplier * lowerDirection);
                    if (lowerTarget > getLowerHeight()) {
                        currentLowerDirection = 1;
                    } else {
                        currentLowerDirection = -1;
                    }
                    if ((lowerDirection != currentLowerDirection || !getUpperMinLimitSwitch())) {
                        setLowerPower(0.0);
                       hover();
                        lowerTarget = -1;
                    }
                } else {
                    setLowerPower(lowerDownSpeed * lowerDirection);
                }
        } else {
           // System.out.println("Cannot reach target position");
        }
        setPositionRunning = false;
    }



    public double getUpperHeight() {
       return m_upperElevatorMotor.getSensorPosition();
    }

    public double getLowerHeight() {
       return m_lowerElevatorMotor.getSensorPosition();
    }


    // less complicated method of setting elevator to destination
    public void setLowerHeight(double position, double power) {
        while (getLowerHeight() != position) {
            if (getLowerHeight() > position) {
                setLowerPower(-power);
            } else {
                setLowerPower(power);
            }
        }
    }

    public void setUpperHeight(double position, double power) {
        while (getUpperHeight() != position) {
            if (getUpperHeight() > position) {
                setUpperPower(-power);
            } else {
                setUpperPower(power);
            }
        }
    }

    public void elevatorUp() {
        combinedStopTargeting = true;
        if (index++ % 2000 == 0 && Robot.drive.isEnabled()) {
            //System.out.println("LH: " + Robot.elevator.getLowerHeight() + " UH: " + Robot.elevator.getUpperHeight());
        }
        if (Robot.elevator.getLowerHeight() > NEAR_MAX_LOWER_HEIGHT) {
            if (Robot.elevator.getLowerHeight() >= MAX_LOWER_HEIGHT) {
                Robot.elevator.setLowerPower(0.0);
                hover();
            } else {
                Robot.elevator.setLowerPower(0.6);
            }
            if (Robot.elevator.getUpperHeight() >= MAX_UPPER_HEIGHT) {
                Robot.elevator.setUpperPower(0.0);
            } else if (Robot.elevator.getUpperHeight() > NEAR_MAX_UPPER_HEIGHT) {
                Robot.elevator.setUpperPower(0.6);
            } else {
                Robot.elevator.setUpperPower(1.0);
            }
        } else {
            Robot.elevator.setLowerPower(1.0);
        }
    }

    public void elevatorDown() {
        if (index++ % 2000 == 0 && Robot.drive.isEnabled()) {
          // System.out.println("LH: " + Robot.elevator.getLowerHeight() + " UH: " + Robot.elevator.getUpperHeight());
        }
        combinedStopTargeting = true;
        if (index++ % 2000 == 0 && Robot.drive.isEnabled()) {
            //System.out.println("LH: " + Robot.elevator.getLowerHeight() + " UH: " + Robot.elevator.getUpperHeight());
        }
        boolean upperDone = false;
        boolean lowerDone = false;
        if ((Robot.elevator.getUpperHeight() <= MIN_UPPER_HEIGHT) || !getUpperMinLimitSwitch()) {
            Robot.elevator.setUpperPower(0.0);
            upperDone = true;
        } else if (Robot.elevator.getUpperHeight() <= NEAR_MIN_UPPER_HEIGHT) {
                Robot.elevator.setUpperPower(-0.3);
        } else {
            Robot.elevator.setUpperPower(-1.0);
        }

        if ((Robot.elevator.getLowerHeight() <= MIN_LOWER_HEIGHT) || !getLowerMinLimitSwitch()) {
            Robot.elevator.setLowerPower(0.0);
            lowerDone = true;
            hover();
        } else if (Robot.elevator.getLowerHeight() < NEAR_MIN_LOWER_HEIGHT) {
//            System.out.println("Nearing Bottom");
            Robot.elevator.setLowerPower(-0.3);
        }  else {
            Robot.elevator.setLowerPower(-0.8);
        }
    }

    public void elevatorNeutral() {
        if (combinedStopTargeting == true) {
         //   System.out.println("Setting elevator to neutral");
            Robot.elevator.setUpperPower(0.0);
            Robot.elevator.setLowerPower(0.0);
            hover();
            targetPosition = getLowerHeight() + getUpperHeight();
            combinedStopTargeting = false;
        }
    }

    // Replacement for LVL1, same function as COMBINED DOWN method, but moves both elevators instead of upper, then lower
    public void bothElevatorsDown() {
        if(getUpperHeight() <= MIN_UPPER_HEIGHT) {
            setUpperPower(0.0); 
        } else if (getUpperHeight() < NEAR_MIN_UPPER_HEIGHT) {
            setUpperPower(-0.3);
        } else {
            setUpperPower(-1.0);
        }
        if (getLowerHeight() <= MIN_LOWER_HEIGHT) {
            setLowerPower(0.0);
            hover();
        } else if (getLowerHeight() < VERY_CLOSE_MIN_LOWER_HEIGHT) {
            setLowerPower(-0.1);
        } else if (getLowerHeight() < NEAR_MIN_LOWER_HEIGHT) {
            
        } else {
            setLowerPower(-1.0);
        }
    }


    public void upperNeutral() {
        if (upperStopTargeting == true) {
       //     System.out.println("Setting upper to neutral");
            Robot.elevator.setUpperPower(0.0);
            targetPosition = getUpperHeight() + getLowerHeight();
            upperStopTargeting = false;
        }
    }

    public void lowerNeutral() {
        if (lowerStopTargeting == true) {
         //   System.out.println("Setting lower to neutral");
            Robot.elevator.setLowerPower(0.0);
            targetPosition = getUpperHeight() + getLowerHeight();
            lowerStopTargeting = false;
        }
    }

    /**
    * Move the elevator motor a set number of inches.
    **/
    public void moveLowerDistance(double distance) {
        m_lowerElevatorMotor.set(ControlMode.Position, distance / DIST_PER_PULSE);
    }

    /**
    * Move the gripper motor a set number of inches.
    **/
    public void moveUpperDistance(double distance) {
        m_upperElevatorMotor.set(ControlMode.Position, distance / DIST_PER_PULSE);
    }
}

