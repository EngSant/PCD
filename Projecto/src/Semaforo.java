
public class Semaforo {

	private Runnable r;
	private int count;
	private boolean interrupted;
	private LocalFile lf;
	
	public Semaforo (int i, LocalFile lf) {	
		count = i;
		this.lf = lf;
	}
	
	
	public LocalFile getLf() {
		return lf;
	}


	public synchronized void semWait() {
		boolean interrupted = false;
		while (count == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				interrupted = true;
				e.printStackTrace();
			}
			
		}
		count--;
		if (interrupted) {
			Thread.currentThread().interrupt();
		}
	}
	
	
	public synchronized void semPost() {
		count++;
		notify();
	}
	
}
