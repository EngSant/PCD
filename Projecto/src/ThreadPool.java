
import java.util.PriorityQueue;
import java.util.Queue;


public class ThreadPool {

	private int numWorkers;
	private Queue<Runnable> tarefas;

	public ThreadPool(int numWorkers) {
		this.numWorkers = numWorkers;
		tarefas = new PriorityQueue<>();
		for (int i = 0; i < numWorkers; i++) {
			new WorkerThread(this).start();
		}
	}

	public synchronized Runnable pushTarefa() {
		while (tarefas.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return tarefas.poll();
	}

	public synchronized void addTarefa(Runnable tarefa) {
		tarefas.offer(tarefa);
		notify();
	}
	

	public Queue<Runnable> getTarefas() {
		return tarefas;
	}

}
