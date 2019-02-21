package org.usfirst.frc.team1559.robot;
import edu.wpi.first.wpilibj.I2C;

public class Arduino {
	I2C arduino;
	byte[] data;
	int address;
	int value1 = 0;

	public Arduino (int address_){
		arduino = new I2C(I2C.Port.kMXP,address_);
		address_ = address;
		data = new byte[1];
	}
	private void write(int val1){
		data[0] = (byte) val1;
		arduino.writeBulk(data);
	}
	public void writeSequence(int sequence){
		sequence = value1;
		write(value1); 
	}
}
