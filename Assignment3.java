/* Imports the neccessary libraries */
import java.awt.Color;////////////////////
import ShefRobot.*;

/** 
* This program makes a lego robot find and follow a trail
* to a red circle where a ping pong ball is located.
* the robot will then attempt to pick up the ping pong
* ball and then follow the trail back to deliver the ball
* at the yellow circle.
*
* @author Megan Maton
*
* @author Daniel Whiteman
*
* @author Harshil Dodhia
*/
public class Assignment3 {
	
	/**
	* This is the main method which contains the main algorithm for the
	* robot to follow.
	*
	* @param args Unused.
	*
	* @author Megan Maton
	* @author Daniel Whiteman
	* @author Harshil Dodhia
	*/
	public static void main(String[] args) {
		
		/* This code creates an instance of the robot, it's motors
		   and it's color sensor
		*/
		Robot ant = new Robot("dia-lego-e8");
		Motor leftMotor = ant.getLargeMotor(Motor.Port.B);
		Motor rightMotor = ant.getLargeMotor(Motor.Port.C);
		Motor armMotor = ant.getLargeMotor(Motor.Port.A);
		ColorSensor colorSensor = ant.getColorSensor(Sensor.Port.S4);



		/* This code creates an instance of the speaker and sets
           its volume to the maximum volume.		
		*/
		Speaker speaker = ant.getSpeaker();
		final int VOLUME_MAX = 100;
		speaker.setVolume(VOLUME_MAX);



		/* This code plays a sound from the speaker so we know when
		   the robot is about to set off
		*/
		final int INITIAL_PITCH = 500;
		final int INITIAL_DURATION = 200;
		speaker.playTone(INITIAL_PITCH, INITIAL_DURATION);



		String detectedColour = null;
		String currentColour = null;

		
		
		/* This code repeatedly moves forward and checks the colour
		   from the colour sensor. If the colour is black then the
		   line has been found so the loop is exited.
		*/
		boolean lineFound = false;
		while (!lineFound) {
			goForward(100, 1, ant, leftMotor, rightMotor);
			currentColour = String.valueOf(colorSensor.getColor());
			System.out.println(detectedColour);
			if (currentColour.equals("BLACK")) {
				lineFound = true;
				System.out.println("Line Found");
			}
		}



		/* This code reverses the robot and makes it turn left so it
		   can begin to follow the line.
		*/
		reverse(40, 2, ant, leftMotor, rightMotor);
		turnLeft(120, 10, ant, leftMotor, rightMotor);




		/* This code declares boolean variables to keep track of the
		   ping pong ball's status.
		*/
		boolean ballDelivered = false;
		boolean ballPickedUp = false;
		
		/* This is the main loop so the robot follows the black line
		   and attempts to eventually deliver the ping pong ball.
		   Each iteration involves getting a reading from the the colour sensor
		   and dealing with it in the appropriate way.
		*/
		while (!ballDelivered) {
			
			/* Getting the colour from the colour sensor. */
			currentColour = String.valueOf(colorSensor.getColor());
			
			/* If the sensor returns the colour red ad the ball hasn't already been picked up
			   then the robot will attempt to pick the ping pong ball up.
			*/
			if (currentColour == "RED" && !ballPickedUp) {
				
				/* Because the colour sensor is not reliable the robot must see see red 3 times
				   before it knows that is is definately on the red circle. We use a counter in a for
				   loop so keep checking the colour.
				*/
				int redCount = 0;
				for (int red = 0; red < 3; red++) {
					currentColour = String.valueOf(colorSensor.getColor());
					if (currentColour == "RED") {
						redCount += 1;
					}
					turnRight(5, 1, ant, leftMotor, rightMotor);
				}
				/* If the robot sees red 3 times in a row then the robot will attempt to pick up the ball. */
				if (redCount == 3) {
					speak(speaker);
					turnLeft(20, 2, ant, leftMotor, rightMotor);
					ballPickedUp = locateBall(ant, armMotor, leftMotor, rightMotor);
				}
			
			
			/* If the sensor returns the colour red and the ball has been picked up then the robot will
			   turn around and find the black line so it can begin to retrace its steps.
			*/
			} else if (currentColour == "RED" && ballPickedUp) {
					turnRight(300, 10, ant, leftMotor, rightMotor);

					/* 	Due to the colour sensor confusing black and blue, the loop checks for either when
						looking for the line
					*/
					while (currentColour != "BLACK" && currentColour != "BLUE") {
						goForward(75, 1, ant, leftMotor, rightMotor);
						turnRight(90, 1, ant, leftMotor, rightMotor);
						currentColour = String.valueOf(colorSensor.getColor());
						System.out.println("Finding black line: " + currentColour);
					}
				
			/* If the sensor returns the colour yellow then the robot will drop the ping pong
			   ball then it will do a celebratory dance
			*/
			} else if (currentColour == "YELLOW" && ballPickedUp) {
				speaker.playTone(500, 200);
				goForward(75, 2, ant, leftMotor, rightMotor);
				dropBall(ant, armMotor);
				celebratoryDance(ant, leftMotor, rightMotor, armMotor, speaker);
				ballDelivered = true;

			
			/* If the colour reading is not red or yellow then the the robot needs to
			   continue following the line. It does this by turning left until its sees white
			   then turning right until its sees black so the robot follow the edge of the line.
			*/
			} else {
				while (!String.valueOf(colorSensor.getColor()).equals("WHITE")) {
					turnLeft(200, 1, ant, leftMotor, rightMotor);
				}
				while (String.valueOf(colorSensor.getColor()).equals("WHITE")) {
					turnRight(230, 1, ant, leftMotor, rightMotor);
				}
			}
		}
	}




	/** 
	* This method makes the robot move forward by moving both motors forward.
	*
	* @param speed the speed the motors will be set to 
	* @param timePeriod the amount of time the motors will run for
	* @param ant the instance of the Robot class
	* @param left the instance of the Motor class for the left motor
	* @param right the instance of the Motor class for the right motor
	*
	* @author Megan Maton
	* @author Daniel Whiteman
	* @author Harshil Dodhia
	*/
	public static void goForward(int speed, int timePeriod, Robot ant, Motor left, Motor right) {
		left.setSpeed(speed);
		right.setSpeed(speed);
		left.forward();
		right.forward();
		ant.sleep(timePeriod * 300);
		left.stop();
		right.stop();
	}
	
	
	/** 
	* This method makes the robot turn left by moving the right motor forward and left motor backwards.
	*
	* @param speed the speed the motors will be set to 
	* @param timePeriod the amount of time the motors will run for
	* @param ant the instance of the Robot class
	* @param left the instance of the Motor class for the left motor
	* @param right the instance of the Motor class for the right motor
	*
	* @author Megan Maton
	* @author Daniel Whiteman
	* @author Harshil Dodhia
	*/
	public static void turnLeft(int speed, int timePeriod, Robot ant, Motor left, Motor right) {
		left.setSpeed(speed / 8);
		right.setSpeed(speed);
		left.backward();
		right.forward();
		ant.sleep(timePeriod * 200);
		left.stop();
		right.stop();
	}


	/** This method makes the robot turn right by moving the left motor forward and right motor backwards. 
	*
	* @param speed the speed the motors will be set to 
	* @param timePeriod the amount of time the motors will run for
	* @param ant the instance of the Robot class
	* @param left the instance of the Motor class for the left motor
	* @param right the instance of the Motor class for the right motor
	*
	* @author Megan Maton
	* @author Daniel Whiteman
	* @author Harshil Dodhia
	*/
	public static void turnRight(int speed, int timePeriod, Robot ant, Motor left, Motor right) {
		left.setSpeed(speed);
		right.setSpeed(speed / 6);
		left.forward();
		right.backward();
		ant.sleep(timePeriod * 200);
		left.stop();
		right.stop();
	}


	/** This method makes the robot move backward by moving both motors backward. 
	*
	* @param speed the speed the motors will be set to 
	* @param timePeriod the amount of time the motors will run for
	* @param ant the instance of the Robot class
	* @param left the instance of the Motor class for the left motor
	* @param right the instance of the Motor class for the right motor
	*
	* @author Megan Maton
	* @author Daniel Whiteman
	* @author Harshil Dodhia
	*/
	public static void reverse(int speed, int timePeriod, Robot ant, Motor left, Motor right) {
		left.setSpeed(speed);
		right.setSpeed(speed);
		left.backward();
		right.backward();
		ant.sleep(timePeriod * 1000);
		left.stop();
		right.stop();
	}


	/** This method locates the ping pong ball by moving forward and then calling the
	*
	* pickBallup method to trap the ball.
	* @param ant the instance of the Robot class
	* @param arm the instance of the Motor class for the robot's arm
	* @param left the instance of the Motor class for the left motor
	* @param right the instance of the Motor class for the right motor
	* @return true
	*
	* @author Megan Maton
	* @author Daniel Whiteman
	* @author Harshil Dodhia
	*/
	public static Boolean locateBall(Robot ant, Motor arm, Motor left, Motor right) {
		goForward(100, 2, ant, left, right);
		pickBallup(ant, arm);
		return true;
	}


	/** This method moves the motor to forward to lower the robot's arm. 
	*
	* @param ant the instance of the Robot class
	* @param arm the instance of the Motor class for the robot's arm
	*
	* @author Megan Maton
	* @author Daniel Whiteman
	* @author Harshil Dodhia
	*/
	public static void pickBallup(Robot ant, Motor arm) {
		System.out.println("Pickup ball");
		arm.setSpeed(75);
		arm.forward();
		ant.sleep(3000);
		arm.stop();
	}


	/** This method moves the motor backward to raise the robots arm and release the ball. 
	*
	* @param ant the instance of the Robot class
	* @param arm the instance of the Motor class for the robot's arm
	*
	* @author Megan Maton
	* @author Daniel Whiteman
	* @author Harshil Dodhia
	*/
	public static void dropBall(Robot ant, Motor arm) {
		System.out.println("Drop ball");
		arm.setSpeed(75);
		arm.backward();
		ant.sleep(2000);
		arm.stop();
	}


	/** This method makes the robot play a tune from the speaker. 
	*
	* @param speaker the instance of the Speaker class to play sounds
	*
	* @author Megan Maton
	* @author Daniel Whiteman
	* @author Harshil Dodhia
	*/
	public static void speak(Speaker speaker) {
		for (int i = 0; i < 3; i++) {
			speaker.playTone(500, 100);
			speaker.playTone(600, 100);
			speaker.playTone(700, 100);
			speaker.playTone(400, 100);
		}
	}


	/** This method makes the robot do a celebratory dance. 
	*
	* @param ant the instance of the Robot class
	* @param arm the instance of the Motor class for the robot's arm
	* @param left the instance of the Motor class for the left motor
	* @param right the instance of the Motor class for the right motor
	* @param speaker the instance of the Speaker class to play sounds
	*
	* @author Megan Maton
	* @author Daniel Whiteman
	* @author Harshil Dodhia
	*/
	public static void celebratoryDance(Robot ant, Motor left, Motor right, Motor arm, Speaker speaker) {
		System.out.println("Celebratory Dance");
		speak(speaker);
		goForward(50, 2, ant, left, right);
		reverse(40, 2, ant, left, right);
		turnLeft(180, 5, ant, left, right);
		turnRight(180, 5, ant, left, right);
	}
}