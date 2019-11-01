package juliocesar;


import java.util.Random;

import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class KillerBeamV2 extends Robot {

	private int power;
	private int contador;
	private int contadorBalas;
	private boolean bandera;
	private boolean gire = false;

	private Estado estado;

	private ScannedRobotEvent lastEvent;

	@Override
	public void run() {
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------
		inicializar();
		while (true) {

			switch (estado) {
			case GIRANDO:
				if (gire == false) {
					turnLeft(10);
				}
				if (gire == true) {
					turnRight(10);
				}
				break;
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------
			case DISPARANDO:
				fire(power);
				ahead(15);
				break;
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------
			case AJUSTANDO:
				turnRight((getHeading() + (lastEvent.getBearing() - getHeading())));
				estado = Estado.DISPARANDO;
				break;
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------
			case CALCULARFUERZA:
				if (lastEvent.getDistance() >= 250) {
					power = 1;
				}
				if (lastEvent.getDistance() < 250 && lastEvent.getDistance() > 110) {
					ahead(50);
					power = 2;
				}
				if (lastEvent.getDistance() < 110 && lastEvent.getDistance() > 50) {
					ahead(15);
					power = 3;
				} else {
					power = 2;
					estado = Estado.DISPARANDO;
				}
				break;
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------
			case HUYENDO:
				if (getOthers() == 1) {
					if (contador >= 2) {
						back(30);
						if (gire == false) {
							turnLeft(20);
							ahead(65);
							contador = 0;
						}
						if (gire == true) {
							turnRight(20);
							ahead(65);
							contador = 0;

						}
					}
				} else {
					back(45);
					if (gire == false) {
						turnLeft(35);
						back(75);
					}
					if (gire == true) {
						turnRight(35);
						back(75);

					}
				}

				estado = Estado.GIRANDO;
				break;
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------
			case IMPACTOPARED:
				bandera = false;
				Random rnd = new Random();
				bandera = rnd.nextBoolean();
				if (bandera == false) {
					turnLeft(120);
				} else {
					turnRight(120);
				}
				ahead(100);
				estado = Estado.GIRANDO;
				break;
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------
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

	private void inicializar() {
		estado = Estado.GIRANDO;
		power = 1;
		contador = 0;
	}

	private enum Estado {
		GIRANDO, DISPARANDO, AJUSTANDO, CALCULARFUERZA, HUYENDO, IMPACTOPARED, CHOCANDO

	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		estado = Estado.AJUSTANDO;
		lastEvent = event;
	}

	@Override
	public void onBulletHit(BulletHitEvent event) {
		contadorBalas++;
		estado = Estado.CALCULARFUERZA;
		if (contadorBalas == 3) {
			if (gire == true) {
				gire = false;
				contadorBalas = 0;
				estado = Estado.GIRANDO;

			}
			if (gire == false) {
				gire = true;
				contadorBalas = 0;
				estado = Estado.GIRANDO;

			}
		}
	}

	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		estado = Estado.GIRANDO;
	}

	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		contador++;
		if (contador == 2) {
			estado = Estado.HUYENDO;
			contador = 0;
		}

	}

	@Override
	public void onHitWall(HitWallEvent event) {
		estado = Estado.IMPACTOPARED;
	}

	@Override
	public void onHitRobot(HitRobotEvent event) {
		estado = Estado.CHOCANDO;

	}

}
