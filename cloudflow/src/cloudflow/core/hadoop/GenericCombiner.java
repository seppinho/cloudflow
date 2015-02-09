package cloudflow.core.hadoop;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import cloudflow.core.Operations;
import cloudflow.core.PipelineConf;
import cloudflow.core.operations.MapOperation;
import cloudflow.core.operations.ReduceOperation;
import cloudflow.core.records.Record;

public class GenericCombiner
		extends
		Reducer<HadoopRecordKey, HadoopRecordValue, HadoopRecordKey, HadoopRecordValue> {

	private Operations<ReduceOperation<Record<?, ?>, Record<?, ?>>> reduceSteps;

	private ReduceOperation<Record<?, ?>, Record<?, ?>> reduceStep;

	private GroupedRecords<Record<?, ?>> recordValues;

	private static final Logger log = Logger.getLogger(GenericCombiner.class);

	@Override
	public void setup(final Context context) throws IOException,
			InterruptedException {

		try {

			log.info("Loading Reduce Step...");

			// read reduce step
			String data = context.getConfiguration().get(
					"cloudflow.steps.combiner");
			reduceSteps = new Operations<ReduceOperation<Record<?, ?>, Record<?, ?>>>();
			reduceSteps.load(data);

			PipelineConf conf = new PipelineConf();
			conf.loadFromConfiguration(context.getConfiguration());

			List<ReduceOperation<Record<?, ?>, Record<?, ?>>> instancesReduce = reduceSteps
					.createInstances();
			reduceStep = instancesReduce.get(0);
			reduceStep.configure(conf);

			// reduce step writes records to context
			reduceStep.getOutputRecords().addConsumer(
					new RecordToContextWriter(context));

		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			throw new IOException(e);
		}

		// create recordValues for input record type

		String inputRecordClassName = context.getConfiguration().get(
				"cloudflow.steps.map.output");

		log.info("Input Records are " + inputRecordClassName);

		Class<?> inputRecordClass;
		try {
			inputRecordClass = Class.forName(inputRecordClassName);
			recordValues = new GroupedRecords<Record<?, ?>>();
			recordValues.setRecordClassName(inputRecordClass);
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			throw new IOException(e);
		}
	}

	@Override
	protected void reduce(HadoopRecordKey key,
			Iterable<HadoopRecordValue> values, Context context)
			throws IOException, InterruptedException {

		recordValues.setValues(values);
		reduceStep.process(key.toString(), recordValues);

	}

}