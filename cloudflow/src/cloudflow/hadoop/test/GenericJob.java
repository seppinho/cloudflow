package cloudflow.hadoop.test;

import genepi.hadoop.CacheStore;
import genepi.hadoop.HadoopJob;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Job;

public class GenericJob extends HadoopJob {

	private Class<InputFormat<?, ?>> inputFormat;

	private Class<?> clazz;

	public void setInputFormat(Class<InputFormat<?, ?>> inputFormat) {
		this.inputFormat = inputFormat;
	}

	public void setDriverClass(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public void setupJob(Job job) {

		job.setJarByClass(clazz);
		job.setInputFormatClass(inputFormat);
		job.setMapperClass(GenericMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputKeyClass(Text.class);
		job.setReducerClass(GenericReducer.class);

	}

	@Override
	protected void setupDistributedCache(CacheStore cache) throws IOException {

		// cache.

	}

	public GenericJob(String name) throws IOException {
		super(name);
	}

	public void setMapSteps(SerializableSteps<MapStep> steps) {
		set("cloudflow.steps.map", steps.serialize());
	}

	public void setMap2Steps(SerializableSteps<MapStep> steps) {
		set("cloudflow.steps.map2", steps.serialize());
	}

	public void setReduceSteps(SerializableSteps<ReduceStep> steps) {
		set("cloudflow.steps.reduce", steps.serialize());
	}

}
