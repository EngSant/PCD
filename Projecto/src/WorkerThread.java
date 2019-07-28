
public class WorkerThread extends Thread {

	private ThreadPool pool;

	public WorkerThread(ThreadPool pool) {
		this.pool = pool;
	}

	@Override
	public void run() {
		while (!interrupted()) {

			Runnable tarefa;
			tarefa = pool.pushTarefa();
			tarefa.run();

		}
	}
	

}
