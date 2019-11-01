package juliocesar;

import java.util.Random;

import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class Altair extends Robot {
	
	
	private double grados;
	
	private Estado estado;
	
	private ScannedRobotEvent lastEvent;
	
	@Override
	public void run() 
	{
		inicializar();
		while(true)
		{
			switch (estado) {
			case GIRANDOIZQ:
				turnLeft(20);
				break;
			case GIRANDODER:
				turnRight(20);
				break;
			case DECIDIENDOGIRO:
				grados = lastEvent.getBearing();
				if(grados>=0) {
					estado = Estado.GIRANDODER;
				}
				
				else {
					estado = Estado.GIRANDOIZQ;
				}
				
				break;
				
			case DISPARANDO:				
				if(lastEvent.getDistance()<=125) {
					fire(3);
				}
				else if(lastEvent.getDistance()<=225) {
					fire(2);
				}
				else {
					fire(1);
				}
				break;
			case AJUSTANDO:
				
				//turnRight(lastEvent.getBearing());
				turnRight((getHeading()+(lastEvent.getBearing()-getHeading())));					
				estado = Estado.DISPARANDO;
				break;
			case SIGUIENDO:				
				if(lastEvent.getDistance() >= 300)
				{
					ahead(20);
				}
				else
				{
					estado = Estado.DISPARANDO;
				}
				break;
			case HUYENDO:
				
				
				
				
				estado= Estado.DECIDIENDOGIRO;
				break;
			case ROTANDO:
				boolean rotar = false;
				Random rndRotar = new Random();
				rotar = rndRotar.nextBoolean();
				if(rotar==false) {
					turnLeft(120);
				}
				
				else {
					turnRight(120);
				}
				ahead(100);
				estado= Estado.DECIDIENDOGIRO;
				break;
			case CHOCANDO:
				back(40);
				estado = Estado.DISPARANDO;
				break;
			
			default:
				doNothing();
				break;
			}
		}
	}
	
	private void inicializar() 
	{
		estado = Estado.GIRANDOIZQ;
		
		grados = 0.0;
	}

	private enum Estado
	{
		GIRANDOIZQ,
		GIRANDODER,
		DECIDIENDOGIRO,
		DISPARANDO,
		AJUSTANDO,
		SIGUIENDO,
		HUYENDO,
		ROTANDO,
		CHOCANDO,
	}
	
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		estado = Estado.AJUSTANDO;				
		lastEvent = event;			
	}
	
	@Override
	public void onBulletHit(BulletHitEvent event) {
		estado = Estado.SIGUIENDO;
		
	}
	
	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		estado = Estado.DECIDIENDOGIRO;
	}
	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		estado = Estado.HUYENDO;
	}
	@Override
	public void onHitWall(HitWallEvent event) {
		estado= Estado.ROTANDO;
	}
	
	@Override
	public void onHitRobot(HitRobotEvent event) {
		estado = Estado.CHOCANDO;
		
	}
	
	

}
