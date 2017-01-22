package org.usfirst.frc.team3939.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
//import edu.wpi.first.wpilibj.DigitalInput;
//import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
//import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.USBCamera;
import edu.wpi.first.wpilibj.AnalogGyro;


public class Robot extends SampleRobot {
    Joystick stick, stick2;
    AnalogInput A0;
	Servo S0, S1, Kicker;
	CANJaguar LeftBackMotor, LeftFrontMotor, RightBackMotor, RightFrontMotor, ShooterPosMotor;
	Talon ShooterMotor; 
	RobotDrive myRobot;
	DigitalInput lSwitch;
    //DigitalInput di, di2;
	CANJaguar.ControlMode currControlMode;
    int maxOutputSpeed;
    int maxSpeedModeRPMs;    
    Relay cameraLight;
    double ShootRatio;
    double SpeedRatio;
    double hoffset;
    Compressor airpump;
    //Solenoid SingleSolenoid;
    DoubleSolenoid Solenoid30, Solenoid6 ,Solenoid12 ;
    //PneumaticsControlModule PCM;

    SendableChooser DriveType = new SendableChooser();
    SendableChooser MaxSpeed = new SendableChooser();
    SendableChooser shooterSpeed = new SendableChooser();
    SendableChooser HeightOffset = new SendableChooser();
    SendableChooser atomtype = new SendableChooser();
    
    Accelerometer accel;
    AnalogGyro gyro;
        
    int intake, intake2 = 0;
	
    
	public Robot() {
    	System.out.println("[th] Code is running now!");
    	stick = new Joystick(0);
    	stick2 = new Joystick(1);
    	A0 = new AnalogInput(0);
    	lSwitch = new DigitalInput(0);
        //S0 = new Servo(0);
        //S1 = new Servo(1);
        Kicker = new Servo(1);  //Kicker
        LeftBackMotor = new CANJaguar(1); //Left Back
        LeftFrontMotor = new CANJaguar(2); //Left Front
        RightBackMotor = new CANJaguar(3); //Right Back
        RightFrontMotor = new CANJaguar(4); //Right Front 
        ShooterPosMotor = new CANJaguar(5);
        cameraLight = new Relay (0); 
        ShooterMotor = new Talon(0); 
       	accel = new BuiltInAccelerometer(); 
        accel = new BuiltInAccelerometer(Accelerometer.Range.k4G); 
        
        airpump = new Compressor(0);
        //SingleSolenoid = new Solenoid(0);
        Solenoid30 = new DoubleSolenoid(1,0);
        Solenoid6 = new DoubleSolenoid(6,7);
        Solenoid12 = new DoubleSolenoid(3,2);
        
        gyro = new AnalogGyro(1);

            	
    	//S0.set(.5);
    	//S1.set(.5);
    	Kicker.set(.9);

    	myRobot = new RobotDrive(LeftFrontMotor, LeftBackMotor, RightFrontMotor, RightBackMotor);
    	cameraLight.set(Relay.Value.kOff);

    }
	public void autoraisearmup() {
        Solenoid12.set(DoubleSolenoid.Value.kForward);
        Timer.delay(2.25);
        Solenoid12.set(DoubleSolenoid.Value.kReverse);
        
	}
	
	public void raisearmup() {
        Solenoid12.set(DoubleSolenoid.Value.kForward);
        Timer.delay(4);
        Solenoid6.set(DoubleSolenoid.Value.kReverse);
        Timer.delay(2.5);
        Solenoid30.set(DoubleSolenoid.Value.kForward);
	}
	
	public void climb() {
		//Solenoid6.set(DoubleSolenoid.Value.kForward);
		Timer.delay(2.5);
		//Solenoid30.set(DoubleSolenoid.Value.kReverse);
		//Timer.delay(2.5);
		//Solenoid12.set(DoubleSolenoid.Value.kReverse); 
		//ShooterMotor.set(-.9);
		
	}
	
	
	
	public void smartDashBoardBsetup() {
		DriveType.addObject("2 Joysticks", "Tank");
    	DriveType.addDefault("1 Joystick", "Arcade");
    	SmartDashboard.putData("Drive Type", DriveType);
		
    	MaxSpeed.addDefault("100%", "1");
    	MaxSpeed.addObject("90%", ".9");
    	MaxSpeed.addObject("80%", ".8");
    	MaxSpeed.addObject("70%", ".7");
    	MaxSpeed.addObject("60%", ".6");
    	MaxSpeed.addObject("50%", ".5");
    	MaxSpeed.addObject("40%", ".4");
    	MaxSpeed.addObject("30%", ".3");
    	MaxSpeed.addObject("20%", ".2");
    	SmartDashboard.putData("Max Speed %", MaxSpeed);
    	
    	atomtype.addObject("Portcullis", "Portcullis");
    	atomtype.addObject("Drawbridge", "Drawbridge");
    	atomtype.addObject("Ramparts", "Ramparts");
    	atomtype.addObject("RockWall", "RockWall");
    	atomtype.addObject("LowBar", "LowBar");
    	atomtype.addObject("ChevaldeFrise", "ChevaldeFrise");
    	atomtype.addObject("Moat", "Moat");
    	atomtype.addObject("Sallyport", "Sallyport");
    	atomtype.addDefault("RoughTerrain", "RoughTerrain");
    	SmartDashboard.putData("Autonomous Type", atomtype);
    	
    	
    	shooterSpeed.addObject("100%", "1");
    	shooterSpeed.addObject("90%", ".9");
    	shooterSpeed.addDefault("80%", ".8");
    	shooterSpeed.addObject("70%", ".7");
    	shooterSpeed.addObject("60%", ".6");
    	shooterSpeed.addObject("50%", ".5");
    	shooterSpeed.addObject("40%", ".4");
    	shooterSpeed.addObject("30%", ".3");
    	shooterSpeed.addObject("20%", ".2");
    	shooterSpeed.addObject("10%", ".1");
    	SmartDashboard.putData("Shooter Speed %", shooterSpeed);
    	/*
    	HeightOffset.addObject("160", "160");
    	HeightOffset.addObject("150", "150");
    	HeightOffset.addObject("140", "140");
    	HeightOffset.addObject("130", "130");
    	HeightOffset.addObject("120", "120");
    	HeightOffset.addDefault("110", "110");
    	HeightOffset.addObject("100", "100");
    	HeightOffset.addObject("90", "90");
    	HeightOffset.addObject("80", "80");
    	HeightOffset.addObject("70", "70");
    	HeightOffset.addObject("60", "60");
    	HeightOffset.addObject("50", "50");
    	HeightOffset.addObject("40", "40");
    	HeightOffset.addObject("30", "30");
    	HeightOffset.addObject("20", "20");
    	HeightOffset.addObject("10", "10");
    	SmartDashboard.putData("HeightOffset", HeightOffset);
    	*/
	}

	public void initShooterMoter() {
		ShooterPosMotor.configMaxOutputVoltage(12.0);
        ShooterPosMotor.configNeutralMode(CANJaguar.NeutralMode.Brake);
        //ShooterPosMotor.setPositionMode(CANJaguar.kQuadEncoder, 497, 75.0f, 0.015f, 0.015f);
        ShooterPosMotor.setPositionMode(CANJaguar.kQuadEncoder, 497, 600, 0, 0);
        //ShooterPosMotor.setPercentMode();
        ShooterPosMotor.enableControl(-0.5);
    
	}
	
	public static CameraServer wcam = CameraServer.getInstance();

    public static USBCamera webcam = new  USBCamera("cam2");
    
    public void robotInit() {
    	
    	smartDashBoardBsetup();
    	maxSpeedModeRPMs = (int)(2650.0/12.75);
        setMode( CANJaguar.JaguarControlMode.PercentVbus);
        initShooterMoter();
       	Kicker.set(.9);   // set kicker default position
       	CameraServer.getInstance().setSize(1);
       	wcam.startAutomaticCapture(webcam);
        
    }
    
    
    
    
    

    double sonarDist() {
    	return A0.getVoltage()*100.0;//*98.0;
    }
  
        
    public void aim() {
        
    	cameraLight.set(Relay.Value.kOn);
    	cameraLight.set(Relay.Value.kReverse);
    	
    	int gotit_x = 0;
    	int gotit_y = 0;
    	int gotit = 0;
    	
    	double Mup = ShooterPosMotor.getPosition();
    	
    	
    	while (gotit_x!=2 ) {
            SmartDashboard.putNumber( "getPosition", ShooterPosMotor.getPosition());
    		//System.out.println("[th] Aim -gotit_x " + gotit_x +" -gotit_y " + gotit_y);
    		         
    		double z = SmartDashboard.getNumber("SHAPE_SIZE");
    		double x = SmartDashboard.getNumber("SHAPE_X_COORD");//Center x of blob
    		double soffset = -20;  // - = left
    		
    		if(x < (85.0-soffset)) myRobot.tankDrive(-.38, .38);
    		else if(x > (95.0-soffset)) myRobot.tankDrive(.38, -.38);
    		else {
    			gotit_x = gotit_x + 1;
    			myRobot.stopMotor();
    		}
    		double Math = 5760 / z;
    		SmartDashboard.putNumber("Approx Dist in Inches",Math);
    		    		
    		Timer.delay(0.25);	
    	}
    	
    	while (gotit_y!=1 ) {
            SmartDashboard.putNumber( "getPosition", ShooterPosMotor.getPosition());
    		//System.out.println("[th] Aim -gotit_x " + gotit_x +" -gotit_y " + gotit_y);
    		         
    		double z = SmartDashboard.getNumber("SHAPE_SIZE");
    		double y = SmartDashboard.getNumber("SHAPE_Y_COORD");//Center y of blob
    		double soffset = -25;  // - = left
    		
    		
    		if (y > (170.0 - hoffset))Mup -= .001;
    		else if (y < (150.0 - hoffset))Mup += .001;
    		else gotit_y = 1;
    		
    		//Timer.delay(.5);
    		ShooterPosMotor.set(Mup);//Set Moter Position
    		
    		
    		double Math = 5760 / z;
    		SmartDashboard.putNumber("Approx Dist in Inches",Math);
    		    		
    		Timer.delay(0.025);	
    	}

  
    	cameraLight.set(Relay.Value.kOff);

    	
    	if(gotit_x + gotit_y == 3){
    	//System.out.println("[th] Aim -gotit_x " + gotit_x +" -gotit_y " + gotit_y);
    	System.out.println("[th] Aim - Got it!");
    	
    //	ShooterMotor.set(-ShootRatio); 
    	Timer.delay(3.0);
    	Kicker.set(0.5); //kick ball out
    	Timer.delay(0.5);
    	Kicker.set(.9);
   // 	ShooterMotor.stopMotor();
    	}
    	
    	
    	
		
    }

	public void aim2() {
        
		initaimdrive();
    	cameraLight.set(Relay.Value.kOn);
    	cameraLight.set(Relay.Value.kReverse);
    	
    	int gotit_x = 0;
    	int gotit_y = 0;
    	int gotit = 0;
    	
    	double Mup = ShooterPosMotor.getPosition();
    	
    	
    	while (gotit_x!=1 && !stick.getRawButton(2)) {
            SmartDashboard.putNumber( "getPosition", ShooterPosMotor.getPosition());
    		System.out.println("[th] Aim -gotit_x " + gotit_x +" -gotit_y " + gotit_y);
    		         
    		double z = SmartDashboard.getNumber("SHAPE_SIZE");
    		double x = SmartDashboard.getNumber("SHAPE_X_COORD");//Center x of blob
    		double soffset = -15;  // - = left
    		
    		if(x < (87.0-soffset)) {
    			LeftFrontMotor.set(LeftFrontMotor.getPosition()-.5);
    			RightFrontMotor.set(RightFrontMotor.getPosition()-.5);
    		}
    		else if(x > (93.0-soffset)) {
    			LeftFrontMotor.set(LeftFrontMotor.getPosition()+.5);
    			RightFrontMotor.set(RightFrontMotor.getPosition()+.5);
    		}
    		else {
    			gotit_x = gotit_x + 1;
    			myRobot.stopMotor();
    		}
    		double Math = 5760 / z;
    		SmartDashboard.putNumber("Approx Dist in Inches",Math);
    		    		
    		Timer.delay(0.25);	
    	}
    	
    	while (gotit_y!=1  && !stick.getRawButton(2)) {
            SmartDashboard.putNumber( "getPosition", ShooterPosMotor.getPosition());
    		System.out.println("[th] Aim -gotit_x " + gotit_x +" -gotit_y " + gotit_y);
    		         
    		double z = SmartDashboard.getNumber("SHAPE_SIZE");
    		double y = SmartDashboard.getNumber("SHAPE_Y_COORD");//Center y of blob
    		double soffset = -25;  // - = left
    		
    		double Math = 5760 / z;
    		SmartDashboard.putNumber("Approx Dist in Inches",Math);
    		
    		if (Math <= 110){
    			hoffset = 90;
    		} else if (Math <= 140){
    			hoffset = 100;
    		} else if (Math <= 170){
    			hoffset = 110;
    		} else if (Math > 170){
    			hoffset = 120;
    		} 
    		
    		if (y > (170.0 - hoffset))Mup -= .001;
    		else if (y < (150.0 - hoffset))Mup += .001;
    		else gotit_y = 1;
    		
    		//Timer.delay(.5);
    		ShooterPosMotor.set(Mup);//Set Moter Position
    		
    		
    		
    		    		
    		Timer.delay(0.025);	
    	}

  
    	cameraLight.set(Relay.Value.kOff);

    	
    	if(gotit_x + gotit_y == 2){
    	//System.out.println("[th] Aim -gotit_x " + gotit_x +" -gotit_y " + gotit_y);
    	System.out.println("[th] Aim - Got it!");
    	
    	ShooterMotor.set(-ShootRatio); 
    	Timer.delay(3.0); 
    	Kicker.set(0.5); //kick ball out
    	Timer.delay(0.5);
    	Kicker.set(.9);
    	ShooterMotor.stopMotor();
    	}
    	
    	
    	
    	setMode( CANJaguar.JaguarControlMode.PercentVbus);
        
    }
	
	public void aimatom() {
        
		initaimdriveatom();
    	cameraLight.set(Relay.Value.kOn);
    	cameraLight.set(Relay.Value.kReverse);
    	
    	int gotit_x = 0;
    	int gotit_y = 0;
    	int gotit = 0;
    	
    	double Mup = ShooterPosMotor.getPosition();
    	
    	double x = SmartDashboard.getNumber("SHAPE_X_COORD");//Center x of blob
		while (x==0) {
			LeftFrontMotor.set(LeftFrontMotor.getPosition()-.5);
			x = SmartDashboard.getNumber("SHAPE_X_COORD");//Center x of blob
			Timer.delay(.2);
		}
    	
    	while (gotit_x!=1 && !stick.getRawButton(2)) {
            SmartDashboard.putNumber( "getPosition", ShooterPosMotor.getPosition());
    		System.out.println("[th] Aim -gotit_x " + gotit_x +" -gotit_y " + gotit_y);
    		         
    		double z = SmartDashboard.getNumber("SHAPE_SIZE");
    		x = SmartDashboard.getNumber("SHAPE_X_COORD");//Center x of blob
    		double soffset = -15;  // - = left
    		
    		if(x < (87.0-soffset)) {
    			LeftFrontMotor.set(LeftFrontMotor.getPosition()-.5);
    			RightFrontMotor.set(RightFrontMotor.getPosition()-.5);
    		}
    		else if(x > (93.0-soffset)) {
    			LeftFrontMotor.set(LeftFrontMotor.getPosition()+.5);
    			RightFrontMotor.set(RightFrontMotor.getPosition()+.5);
    		}
    		else {
    			gotit_x = gotit_x + 1;
    			myRobot.stopMotor();
    		}
    		double Math = 5760 / z;
    		SmartDashboard.putNumber("Approx Dist in Inches",Math);
    		    		
    		Timer.delay(0.5);	
    	}
    	
    	while (gotit_y!=1  && !stick.getRawButton(2)) {
            SmartDashboard.putNumber( "getPosition", ShooterPosMotor.getPosition());
    		System.out.println("[th] Aim -gotit_x " + gotit_x +" -gotit_y " + gotit_y);
    		         
    		double z = SmartDashboard.getNumber("SHAPE_SIZE");
    		double y = SmartDashboard.getNumber("SHAPE_Y_COORD");//Center y of blob
    		double soffset = -25;  // - = left
    		
    		
    		if (y > (170.0 - hoffset))Mup -= .001;
    		else if (y < (150.0 - hoffset))Mup += .001;
    		else gotit_y = 1;
    		
    		//Timer.delay(.5);
    		ShooterPosMotor.set(Mup);//Set Moter Position
    		
    		
    		double Math = 5760 / z;
    		SmartDashboard.putNumber("Approx Dist in Inches",Math);
    		    		
    		Timer.delay(0.025);	
    	}

  
    	cameraLight.set(Relay.Value.kOff);

    	
    	if(gotit_x + gotit_y == 2){
    	//System.out.println("[th] Aim -gotit_x " + gotit_x +" -gotit_y " + gotit_y);
    	System.out.println("[th] Aim - Got it!");
    	
    	ShooterMotor.set(-ShootRatio); 
    	Timer.delay(3.0);
    	Kicker.set(0.5); //kick ball out
    	Timer.delay(0.5);
    	Kicker.set(1.0);
    	ShooterMotor.stopMotor();
    	}
    	
    	
    	
    	setMode( CANJaguar.JaguarControlMode.PercentVbus);
        
    }
    
    
	public void initaimdrive() {
    	LeftFrontMotor.disableControl();
        LeftFrontMotor.configMaxOutputVoltage(12.0);
        LeftFrontMotor.configNeutralMode(CANJaguar.NeutralMode.Brake);
        LeftFrontMotor.setPositionMode(CANJaguar.kQuadEncoder, 4320, 75, .0, 0);
        LeftFrontMotor.enableControl();
        
        RightFrontMotor.disableControl();
        RightFrontMotor.configMaxOutputVoltage(12.0);
        RightFrontMotor.configNeutralMode(CANJaguar.NeutralMode.Brake);
        RightFrontMotor.setPositionMode(CANJaguar.kQuadEncoder, 4320, 75, .0, 0);
        RightFrontMotor.enableControl();
        
        LeftBackMotor.disableControl();
        LeftBackMotor.configMaxOutputVoltage(12.0);
        LeftBackMotor.configNeutralMode(CANJaguar.NeutralMode.Coast);
        LeftBackMotor.setPositionMode(CANJaguar.kQuadEncoder, 4320, 75, .0, 0);
        LeftBackMotor.enableControl();
        
        RightBackMotor.disableControl();
        RightBackMotor.configMaxOutputVoltage(12.0);
        RightBackMotor.configNeutralMode(CANJaguar.NeutralMode.Coast);
        RightBackMotor.setPositionMode(CANJaguar.kQuadEncoder, 4320, 75, .0, 0);
        RightBackMotor.enableControl();
        
        
        
        
        
    }
 
    public void initaimdriveatom() {
    	LeftFrontMotor.disableControl();
        LeftFrontMotor.configMaxOutputVoltage(12.0);
        LeftFrontMotor.configNeutralMode(CANJaguar.NeutralMode.Brake);
        LeftFrontMotor.setPositionMode(CANJaguar.kQuadEncoder, 4320, 75, .0, 0);
        LeftFrontMotor.enableControl();
        
        RightFrontMotor.disableControl();
        RightFrontMotor.configMaxOutputVoltage(12.0);
        RightFrontMotor.configNeutralMode(CANJaguar.NeutralMode.Brake);
        RightFrontMotor.setPositionMode(CANJaguar.kQuadEncoder, 4320, 75, .0, 0);
        RightFrontMotor.enableControl();
        
        LeftBackMotor.disableControl();
        LeftBackMotor.configMaxOutputVoltage(12.0);
        LeftBackMotor.configNeutralMode(CANJaguar.NeutralMode.Coast);
        LeftBackMotor.setPositionMode(CANJaguar.kQuadEncoder, 4320, 75, .0, 0);
        LeftBackMotor.enableControl();
        
        RightBackMotor.disableControl();
        RightBackMotor.configMaxOutputVoltage(12.0);
        RightBackMotor.configNeutralMode(CANJaguar.NeutralMode.Coast);
        RightBackMotor.setPositionMode(CANJaguar.kQuadEncoder, 4320, 75, .0, 0);
        RightBackMotor.enableControl();
        
        
        
        
        
    }
 
    void initMotor( CANJaguar motor ) {
        try {
            if ( currControlMode == CANJaguar.JaguarControlMode.Position )
            {
                motor.configMaxOutputVoltage(12.0);
                motor.configNeutralMode(CANJaguar.NeutralMode.Brake);
                motor.setPositionMode(CANJaguar.kQuadEncoder, 4320, 0, .0, 0);
            }
            else
            {
            	motor.configNeutralMode(CANJaguar.NeutralMode.Brake);
                motor.setPercentMode();
            }
            motor.enableControl();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    
    
    void setMode( CANJaguar.ControlMode controlMode ) {
        
        currControlMode = controlMode;

        if ( currControlMode == CANJaguar.JaguarControlMode.Position )
        {
        
        }
        else // kPercentVbus
        {
                maxOutputSpeed = 1;
        }
        
        initMotor(LeftBackMotor);
        initMotor(LeftFrontMotor);
        initMotor(RightBackMotor);
        initMotor(RightFrontMotor);    
        //initMotor(Jag_08);
    }    
    
    void checkForRestartedMotor( CANJaguar motor, String strDescription )
    {
        if ( currControlMode != CANJaguar.JaguarControlMode.Speed )   // kSpeed is the default
        {
            try {
            	if ( !motor.isAlive() )
                {
                    Timer.delay(0.10); // Wait 100 ms
                    initMotor( motor );
                    String error = "\n\n>>>>" + strDescription + "Jaguar Power Cycled - re-initializing";
                    System.out.println(error);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }    

    public void autonomousPeriodic() {
     }
    
    public void autonomous() {
    	System.out.println("[th] Autonomus Run!");
    	myRobot.setSafetyEnabled(false);
    	//gyro.reset();
    	//double Kp = 0.03;
    	
    	autoraisearmup();
    	Timer.delay(3);
    	//int x = 0;
    	
    	//while (x < 200) {
    	//	updateDashBoard();
         //   double angle = gyro.getAngle(); // get current heading
        //    myRobot.drive(.3, -angle*Kp); // drive towards heading 0
        //    Timer.delay(0.004);
        //    x ++;
       // }
    	
    	
    	
    	if (atomtype.getSelected() == "Portcullis") {
    		
    	}else if (atomtype.getSelected() == "Drawbridge") {
    		
    	}else if (atomtype.getSelected() == "Ramparts") {
    		ShooterPosMotor.set(-.5);
        	myRobot.drive(.80, 0);
        	ShooterPosMotor.set(-.5);
        	Timer.delay(2.5);
        	ShooterPosMotor.set(-.5);
            myRobot.drive(0.0, 0.0);
        
    	}else if (atomtype.getSelected() == "RockWall") {
    		ShooterPosMotor.set(-.5);
        	myRobot.drive(.80, 0);
        	ShooterPosMotor.set(-.5);
        	Timer.delay(2.5);
        	ShooterPosMotor.set(-.5);
            myRobot.drive(0.0, 0.0);
        
    	}else if (atomtype.getSelected() == "LowBar") {
    		ShooterPosMotor.set(-.2);
        	myRobot.drive(.40, 0);
        	ShooterPosMotor.set(-.2);
        	Timer.delay(3.5);
        	ShooterPosMotor.set(-.2);
            myRobot.drive(0.0, 0.0);
       	
    	}else if (atomtype.getSelected() == "ChevaldeFrise") {
    		
    	}else if (atomtype.getSelected() == "Moat") {
    		ShooterPosMotor.set(-.5);
        	myRobot.drive(.95, 0);
        	ShooterPosMotor.set(-.5);
        	Timer.delay(2);
        	ShooterPosMotor.set(-.5);
            myRobot.drive(0.0, 0.0);
        
    	}else if (atomtype.getSelected() == "Sallyport") {
    		
    	}else if (atomtype.getSelected() == "RoughTerrain") {
    		ShooterPosMotor.set(-.5);
        	myRobot.drive(.5, 0);
        	ShooterPosMotor.set(-.5);
        	Timer.delay(3.0);
        	ShooterPosMotor.set(-.5);
            myRobot.drive(0.0, 0.0);
        }
    	
    	
    	//   ShooterPosMotor.set(-.3);
        //aimatom();
     
        //Go forward at 45% for 1.5s
    	
        
    	//myRobot.arcadeDrive(-0.45, -0.123);
    	//Timer.delay(0.5);
    	//myRobot.stopMotor();	
    	
    	//Set arm up for 1s
    	//myArm.set(1.0);
    	//Timer.delay(1.0);
    	//myArm.stopMotor();
    	
    	//Go backwards for 5s
    	//myRobot.arcadeDrive(0.6,-0.2);
    	//Timer.delay(3.0);
    	
    	//Stop
    	//myRobot.stopMotor();
    	//myArm.stopMotor();
	}
    

    public void updateDashBoard() {
    	//SmartDashboard.putDouble("stick2y", stick2.getY());
    	//SmartDashboard.putDouble("SpeedRatio",SpeedRatio);
        //SmartDashboard.putDouble("ShootRatio",ShootRatio);
        //SmartDashboard.putDouble("Speed", stick.getThrottle());
        
        //SmartDashboard.putNumber( "SpeedOut_LeftBackMotor", LeftBackMotor.get());
        //SmartDashboard.putNumber( "SpeedOut_LeftFrontMotor", LeftFrontMotor.get());
        //SmartDashboard.putNumber( "SpeedOut_RightBackMotor", RightBackMotor.get());
        //SmartDashboard.putNumber( "SpeedOut_RightFrontMotor", RightFrontMotor.get());
        //SmartDashboard.putNumber( "SpeedOut_ShooterPosMotor", ShooterPosMotor.get());
        
        //SmartDashboard.putNumber( "Speed_LeftBackMotor", LeftBackMotor.getSpeed());
        //SmartDashboard.putNumber( "Speed_LeftFrontMotor", LeftFrontMotor.getSpeed());
        //SmartDashboard.putNumber( "Speed_RightBackMotor", RightBackMotor.getSpeed());
        //SmartDashboard.putNumber( "Speed_RightFrontMotor", RightFrontMotor.getSpeed());
        //SmartDashboard.putNumber( "Speed_ShooterPosMotor", ShooterPosMotor.getSpeed());

        //SmartDashboard.putInt("POV", stick.getPOV());
        
        SmartDashboard.putNumber( "getPosition", ShooterPosMotor.getPosition());    
        
        //SmartDashboard.putNumber( "count", sampleEncoder.get());
        //SmartDashboard.putNumber( "distance", sampleEncoder.getRaw());
        //SmartDashboard.putNumber( "distance2", sampleEncoder.getDistance());
        //SmartDashboard.putNumber( "rate", sampleEncoder.getRate());
        //SmartDashboard.putBoolean( "direction", sampleEncoder.getDirection());
        //SmartDashboard.putBoolean( "stopped", sampleEncoder.getStopped());

        //SmartDashboard.putNumber( "accel_x", accel.getX());
        //SmartDashboard.putNumber( "accel_y", accel.getY());
        //SmartDashboard.putNumber( "accel_z", accel.getZ());
        
        SmartDashboard.putBoolean( "limit", lSwitch.get());
        
        //SmartDashboard.putNumber("Sonar Dist",sonarDist());
        //SmartDashboard.putNumber("Gyro",gyro.getAngle());
        
    	
        
    }
    
    public void getDashboardOptions() {
    
    	Object SpeedRatioget = MaxSpeed.getSelected();
    	if (SpeedRatioget =="1") {
    		SpeedRatio=1;
    	}else if (SpeedRatioget ==".9") {
    		SpeedRatio=.9;
    	}else if (SpeedRatioget ==".8") {
    		SpeedRatio=.8;
    	}else if (SpeedRatioget ==".7") {
    		SpeedRatio=.7;
    	}else if (SpeedRatioget ==".6") {
    		SpeedRatio=.6;
    	}else if (SpeedRatioget ==".5") {
    		SpeedRatio=.5;
    	}else if (SpeedRatioget ==".4") {
    		SpeedRatio=.4;
    	}else if (SpeedRatioget ==".3") {
    		SpeedRatio=.3;
    	}else {
    		SpeedRatio=.2;
    	}

    	
    	
    	
    	
    	
    	Object ShooterRatioget = shooterSpeed.getSelected();
        if (ShooterRatioget =="1") {
        	ShootRatio = 1;
        }else if (ShooterRatioget ==".9") {
        	ShootRatio=.9;
        }else if (ShooterRatioget ==".8") {
        		ShootRatio=.8;
        }else if (ShooterRatioget ==".7") {
        	ShootRatio=.7;
        }else if (ShooterRatioget ==".6") {
        	ShootRatio=.6;
        }else if (ShooterRatioget ==".5") {
        	ShootRatio=.5;
        }else if (ShooterRatioget ==".4") {
        	ShootRatio=.4;
        }else if (ShooterRatioget ==".3") {
        	ShootRatio=.3;
        }else if (ShooterRatioget ==".2") {
        	ShootRatio=.2;
        }else if (ShooterRatioget ==".1") {
        	ShootRatio=.1;
        }
        

        /*
        Object HeightOffget = HeightOffset.getSelected();
        
        if (HeightOffget =="170") {
        	hoffset = 170;
        }else  if (HeightOffget =="160") {
        	hoffset = 160;
        }else if (HeightOffget =="150") {
        	hoffset = 150;
        }else if (HeightOffget =="140") {
        	hoffset=140;
        }else if (HeightOffget =="130") {
        	hoffset=130;
        }else if (HeightOffget =="120") {
        	hoffset = 120;
        }else  if (HeightOffget =="110") {
        	hoffset = 110;
        }else if (HeightOffget =="100") {
        	hoffset = 100;
        }else if (HeightOffget =="90") {
        	hoffset=90;
        }else if (HeightOffget =="80") {
        	hoffset=80;
        }else if (HeightOffget =="70") {
        	hoffset=70;
        }else if (HeightOffget =="60") {
        	hoffset=60;
        }else if (HeightOffget =="50") {
        	hoffset=50;
        }else if (HeightOffget =="40") {
        	hoffset=40;
        }else if (HeightOffget =="30") {
        	hoffset=30;
        }else if (HeightOffget =="20") {
        	hoffset=20;
        }else if (HeightOffget =="10") {
        	hoffset=10;
        }
        */
    }
    
    public void resetShooterloc() {
    	while (!lSwitch.get()) {
       		ShooterPosMotor.set(ShooterPosMotor.getPosition() - .2);
    	}
    	ShooterPosMotor.disableControl();
    	ShooterPosMotor.enableControl(-.5);
    }
    
    public void operatorControl() { //Teleop!
    	System.out.println("[th] Operator Controle!");
        myRobot.setSafetyEnabled(true);
     	
    	while (isOperatorControl() && isEnabled()) { //While Loop for Teleop!
    	    	
    		getDashboardOptions();
    		updateDashBoard();
    		 
        	double speed = (stick.getThrottle()+1)/0.5*SpeedRatio;
        	            	
        	if (DriveType.getSelected() == "Tank") { // drive
        		myRobot.tankDrive(-stick2.getY()*speed, -stick.getY()*speed,true);
        	} else {
        		myRobot.arcadeDrive(-stick.getY()*speed, (stick.getX()*-speed)/3);	
        	}
        	
            if(stick.getRawButton(7)) { //autofire
            	ShooterPosMotor.set(-0.3);
            	Timer.delay(0.5);
                aim2();
            } 
                   
            
            if(stick.getRawButton(8)) { //reset shooter to -.5
            	resetShooterloc();            	
            } 
    /*        
            if (stick.getRawButton(9)){Solenoid12.set(DoubleSolenoid.Value.kForward);}
            
            if (stick.getRawButton(10)){Solenoid12.set(DoubleSolenoid.Value.kReverse);}
            
            if (stick.getRawButton(11)){Solenoid30.set(DoubleSolenoid.Value.kForward);}
            
            if (stick.getRawButton(12)){Solenoid30.set(DoubleSolenoid.Value.kReverse);}
            
            if (stick.getRawButton(5)){Solenoid6.set(DoubleSolenoid.Value.kForward);}
            
            if (stick.getRawButton(6)){Solenoid6.set(DoubleSolenoid.Value.kReverse);}
      */      
           if(stick.getRawButton(1)) { // shoot button
            	//ShooterMotor.set(-ShootRatio); 
            	Timer.delay(3.0);
            	Kicker.set(0.5); //kick ball out
            	Timer.delay(0.5);
            	Kicker.set(1.0);
            //	ShooterMotor.stopMotor();
            	}
           
           
            if (stick.getRawButton(3)){ //intake button ShooterMotor
            	SmartDashboard.putNumber( "intake", intake);
                
            		ShooterPosMotor.set(-0.06);
            	//	ShooterMotor.set(0.4);
            	
            }    
            if (stick.getRawButton(4)){ //intake button ShooterMotor
            	SmartDashboard.putNumber( "intake", intake);
            
            	ShooterPosMotor.set(-0.3);
            	//ShooterMotor.stopMotor();
            }
            
            if (stick.getRawButton(5)){ //shooter down
            	ShooterPosMotor.set(ShooterPosMotor.getPosition()+.05);
            	
            }
            
            if (stick.getRawButton(6)){ //shooter up
            	if (!lSwitch.get()) {
            		ShooterPosMotor.set(ShooterPosMotor.getPosition()-.07);
            	}
            }
            
  /*          
            if (stick.getPOV()==0){
            	ShooterPosMotor.set(ShooterPosMotor.getPosition() - 0.02);
            } else if (stick.getPOV()==180 ){
            	ShooterPosMotor.set(ShooterPosMotor.getPosition() + 0.02);
            }
    */        
            //if (stick.getRawButton(11)){autoraisearmup();}

            if (stick.getRawButton(9)){raisearmup();}
            if (stick.getRawButton(10)){climb();}

/*        
 *
            c.setClosedLoopControl(true); //When closed loop control is enabled the PCM will automatically turn the compressor on when the pressure switch is closed (below the pressure threshold) and turn it off when the pressure switch is open (~120PSI). When closed loop control is disabled the compressor will not be turned on.
            
            c.setClosedLoopControl(false);
            
            boolean enabled = c.enabled();
            boolean pressureSwitch = c.getPressureSwitchValue();
            float current = c.getCompressorCurrent();
            
            SingleSolenoid.set(true); //To set the value of the solenoid call set(true) to enable or set(false) to disable the solenoid output.
            SingleSolenoid.set(false);
            
            DblSolenoid.set(DoubleSolenoid.Value.kOff); //The state of the valve can then be set to kOff (neither output activated), kForward (forward channel enabled) or kReverse (reverse channel enabled).
            DblSolenoid.set(DoubleSolenoid.Value.kForward);
            DblSolenoid.set(DoubleSolenoid.Value.kReverse);
            
            double pcurrent = pdp.getCurrent(2);
            

 */
            
            
            	
            
        }
    }
}
