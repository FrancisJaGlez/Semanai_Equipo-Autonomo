package juliocesar;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.BulletMissedEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;
import static robocode.util.Utils.normalRelativeAngleDegrees;

//Autores
//Santiago España Vázquez
//Julio Cesar Lugo Castañeda
//Francisco Javier Molina

public class Ezio extends AdvancedRobot {
	// Variables que se utilizaran durante toda la programacion del robot
	private ScannedRobotEvent scanEvent;
	private Estado estado;
	private String Focus;
	private boolean Focused;
	private int Missed;
	private int direction;

	// Los diferentes estados que tiene el robot
	private enum Estado {
		ESCANEANDO, PRIORIZANDO, PRESIONANDO
	}

	// Metodo el cual permite que el robot comience a funcionar
	@Override
	public void run() {
		// Metodo que inicializa las diferentes variables y comieza con el estado
		// ESCANEANDO
		inicializar();
		while (true) {
			switch (estado) {
			case ESCANEANDO:
				turnGunRight(10);
				break;

			// Hace un enfoque en un tanque mediante su nombre y enfoca sus ataques en él a
			// menos de perderlo de vista despues de 4 sondeos
			case PRIORIZANDO:
				// Verifica que el tanque no tenga un objetivo actualmente y si es asi asigna el
				// primero que ve
				if (Focused == false) {
					Focus = scanEvent.getName();
					Focused = true;
				}
				// Si el tanque escaneado es el objetivo se centra en el y ataca
				if (Focused == true && scanEvent.getName() == Focus) {
					if (getHeading() != scanEvent.getBearing()) {
						if (getGunHeading() < scanEvent.getBearing() + 10
								|| getGunHeading() > scanEvent.getBearing() - 10) {
							// Calcula el lugar donde estan los otros tanques, checa el calor del arma para
							// no perder de vista
							// al tanque centrado, si se pierde se realiza un scan que llama al metodo
							// OnScannedRobot cuando sea necesario
							// si la distancia es menor a 250 pixeles ataca con fuerza 3 y si no fuerza 1
							setAhead(500 * direction);
							setTurnRight(45 * direction);
							if (scanEvent.getDistance() <= 250) {
								double total = getHeading() + scanEvent.getBearing();
								double anguloArma = normalRelativeAngleDegrees(total - getGunHeading());
								if (Math.abs(anguloArma) <= 3) {
									turnGunRight(anguloArma);
									if (getGunHeat() == 0) {
										fireBullet(3);
									}
								} else {
									turnGunRight(anguloArma);
								}
								if (anguloArma == 0) {
									scan();
								}
							}
							if (scanEvent.getDistance() > 250) {
								double total = getHeading() + scanEvent.getBearing();
								double anguloArma = normalRelativeAngleDegrees(total - getGunHeading());
								if (Math.abs(anguloArma) <= 3) {
									turnGunRight(anguloArma);
									if (getGunHeat() == 0) {
										fireBullet(1);
									}
								} else {
									turnGunRight(anguloArma);
								}
								if (anguloArma == 0) {
									scan();
								}
							}

						}
					} else {
						doNothing();
					}
					Missed = 0;
				}

				// Cuenta cuantas veces a visto un tanque que no es el objetivo guardado, si
				// supera las 4 veces cambia de objetivo por el proximo visto
				if (Focused == true && scanEvent.getName() != Focus) {
					Missed++;
					if (Missed >= 4) {
						Focused = false;
					}
				}
				break;
			// Su movimiento es totalmente dependiente del enemigo, lo sigue de manera
			// ofensiva disparando con fuerza 2
			case PRESIONANDO:
				setAhead(scanEvent.getDistance() - 150);
				setTurnRight(scanEvent.getBearing());
				double total = getHeading() + scanEvent.getBearing();
				double anguloArma = normalRelativeAngleDegrees(total - getGunHeading());
				if (Math.abs(anguloArma) <= 3) {
					turnGunRight(anguloArma);
					if (getGunHeat() == 0) {
						fireBullet(2);
					}
				} else {
					turnGunRight(anguloArma);
				}
				if (anguloArma == 0) {
					scan();
				}

				break;
			default:
				doNothing();
				break;
			}
		}
	}

	// Inicialización (Cambio de colores y estados predeterminados)

	private void inicializar() {
		setColors(Color.WHITE, Color.RED, Color.WHITE);
		setScanColor(Color.DARK_GRAY);
		estado = Estado.ESCANEANDO;
		Focus = "";
		Focused = false;
		Missed = 0;
		direction = 1;
	}

	// Si hay mas de dos tanques sigue operando con el estado PRIORIZANDO, si hay 2
	// o menos cambia a PRESIONANDO

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		if (getOthers() > 2) {
			estado = Estado.PRIORIZANDO;
		}
		if (getOthers() <= 2) {
			estado = Estado.PRESIONANDO;
		}

		scanEvent = event;
	}

	// Si se falla el disparo vuelve a escanear la zona

	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		estado = Estado.ESCANEANDO;
	}

	// En caso de ser golpeado por una bala o un tanque cambia de dirrección

	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		direction *= -1;
	}

	@Override
	public void onHitRobot(HitRobotEvent event) {
		direction *= -1;
	}
}
