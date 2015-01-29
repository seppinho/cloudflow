package cloudflow.core;

import java.util.List;
import java.util.Vector;

public class SerializableSteps<c> {

	private List<Class> steps;

	public int getSize() {
		return steps.size();
	}

	public c getStepInstance(int i) throws InstantiationException,
			IllegalAccessException {
		return (c) steps.get(i).newInstance();
	}

	public void addStep(Class clazz) {
		steps.add(clazz);
	}

	public SerializableSteps() {
		this.steps = new Vector<Class>();
	}

	public String serialize() {
		String data = "";
		for (Class step : steps) {
			data += step.getName() + " ";
		}

		return data;
	}

	public void load(String data) throws ClassNotFoundException {
		this.steps = new Vector<Class>();
		String[] tiles = data.split(" ");
		for (String tile : tiles) {
			steps.add(Class.forName(tile));
		}
	}

	public List<c> createInstances() throws InstantiationException,
			IllegalAccessException {

		List<c> instances = new Vector<c>();
		for (int i = 0; i < steps.size(); i++) {
			instances.add((c) steps.get(i).newInstance());
		}
		return instances;
	}

}