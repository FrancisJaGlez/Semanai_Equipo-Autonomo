package juliocesar;

import java.util.Random;

import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.HitByBulletEvent;
import robocode.HitWallEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class Autonomo extends Robot {
	//Creacion de variables que seran utilizadas durantes los diferentes metodos y estados
	private int potencia;
	private int contador;
	private double grados;
	private boolean bandera;;
	private Estado estado;
	private ScannedRobotEvent eventScan;
	private HitByBulletEvent eventHit;

	@Override
	public void run() {
		//Llamada al metodo inicializar
		inicializar();
		while (true) {

			switch (estado) {
			//Estados los cuales funcionan para decidir hacia donde girara el robot para rastrear enemigos
			case GIROIZQ:
				turnLeft(20);
				break;
			case GIRODER:
				turnRight(20);
				break;
			case DECGIRO:
				grados = eventScan.getBearing();
				if(grados>0) {
					estado = Estado.GIRODER;
				}
				else {
					estado = Estado.GIROIZQ;
				}
				break;
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------
			//Estado que se encargara de acercarse al enemigo dependiendo de la distancia a la que esté para despues disparar
			case ATACANDO:
				if(eventScan.getDistance()<=100) {
					ahead(10);
				}
				else if(eventScan.getDistance()<=200) {
					ahead(30);
				}
				else if(eventScan.getDistance()<=300) {
					ahead(40);
				}
				else if(eventScan.getDistance()<=400) {
					ahead(50);
				}
				else {
					ahead(60);
				}
				fire(potencia);
				
				break;
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------
			//Estado para ajustar el tiro para conseguir un tiro preciso
			case AJUSTANDO:
				turnRight(eventScan.getBearing());
				estado = Estado.ATACANDO;
				break;
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------
			//Estado que calcula la fuerza recomendada para el disparo
			case CALCULARFUERZA:
				if(eventScan.getDistance()<=80) {
					potencia=4;
				}
				else if(eventScan.getDistance()<=200) {
					potencia=3;
				}
				else if(eventScan.getDistance()<=300) {
					potencia=2;
				}
				else {
					potencia=1;
				}
				estado=Estado.ATACANDO;
				break;
	
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------
			//Estado que permite que el robot no se quede atascado en paredes
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
				estado = Estado.DECGIRO;
				break;
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------
			//Estado que aleja el robot del otro robot si han chocado
			case CHOCANDO:
				back(70);
				estado = Estado.ATACANDO;
				break;	
//--------------------------------------------------------------------------------------------------------------------------------------------
				default:
				doNothing();
				break;
			}
		}
	}
	
	//Inicializacion de las variables y que el robot comenzara girando a la izquierda
	private void inicializar() {
		estado = Estado.GIROIZQ;
		potencia = 1;
		contador = 0;
	}
	
	//Los diferentes estados que tiene el robot
	private enum Estado {
		ATACANDO, AJUSTANDO, CALCULARFUERZA, IMPACTOPARED, CHOCANDO, GIROIZQ, GIRODER, DECGIRO
	}
	
	//Metodo donde el robot encuenta a un enemigo en su scanner y decide pasar al estado AJUSTANDO
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		estado = Estado.AJUSTANDO;
		eventScan = event;
	}
	
	//Metodo que al impactar una bala contra el enemigo decide pasar al estado CALCULARFUERZA
	@Override
	public void onBulletHit(BulletHitEvent event) {
		estado = Estado.CALCULARFUERZA;
	}
	
	//Metodo que al fallar una bala decide pasar al estado DECGIRO
	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		estado = Estado.DECGIRO;
	}
	

	
	//Metodo que al chocar contra una pared decide pasar al estado IMPACTOPARED
	@Override
	public void onHitWall(HitWallEvent event) {
		estado = Estado.IMPACTOPARED;
	}
}
