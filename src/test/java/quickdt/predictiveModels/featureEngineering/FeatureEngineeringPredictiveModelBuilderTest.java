package quickdt.predictiveModels.featureEngineering;

import com.google.common.collect.Lists;
import org.testng.annotations.Test;
import quickdt.data.*;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;

import javax.annotation.Nullable;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeatureEngineeringPredictiveModelBuilderTest {

    @Test
    public void simpleTest() {
        TestAEBS testFEPMB = new TestAEBS();
        List<Instance> trainingData = Lists.newArrayList();
        trainingData.add(new Instance(new HashMapAttributes(), 1));
        FeatureEngineeringPredictiveModelBuilder feBuilder = new FeatureEngineeringPredictiveModelBuilder(new TestPMBuilder(), Lists.newArrayList(new TestAEBS()));
        final FeatureEngineeredPredictiveModel predictiveModel = feBuilder.buildPredictiveModel(trainingData);
        predictiveModel.getProbability(trainingData.get(0).getAttributes(), 1);
    }

    public static class TestAEBS implements AttributesEnrichStrategy {

        @Override
        public AttributesEnricher build(final Iterable<? extends AbstractInstance> trainingData) {
            return new AttributesEnricher() {
                private static final long serialVersionUID = -4851048617673142530L;

                @Nullable
                @Override
                public Attributes apply(@Nullable final Attributes attributes) {
                    HashMapAttributes er = new HashMapAttributes();
                    er.putAll(attributes);
                    er.put("enriched", 1);
                    return er;
                }
            };
        }

    }

    public static class TestPMBuilder implements PredictiveModelBuilder<TestPM> {

        @Override
        public TestPM buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
            for (AbstractInstance instance : trainingData) {
                if (!instance.getAttributes().containsKey("enriched")) {
                    throw new IllegalArgumentException("Predictive model training data must contain enriched instances");
                }
            }

            return new TestPM();
        }

        @Override
        public TestPMBuilder updatable(boolean updatable) {
            return this;
        }

        @Override
        public void setID(Serializable id) {

        }

    }

    public static class TestPM implements PredictiveModel {

        private static final long serialVersionUID = -3449746370937561259L;

        @Override
        public double getProbability(final Attributes attributes, final Serializable classification) {
            if (!attributes.containsKey("enriched")) {
                throw new IllegalArgumentException("Predictive model training data must contain enriched instances");
            }
            return 0;
        }

        @Override
        public Map<Serializable, Double> getProbabilitiesByClassification(final Attributes attributes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void dump(final PrintStream printStream) {

        }

        @Override
        public Serializable getClassificationByMaxProb(final Attributes attributes) {
            return null;
        }
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void updatableInputFalseOutputUnsupportedOperationException() {
      final ArrayList arrayList = new ArrayList();
      arrayList.add(0);
      arrayList.add(0);
      final FeatureEngineeringPredictiveModelBuilder featureEngineeringPredictiveModelBuilder = new FeatureEngineeringPredictiveModelBuilder(null, arrayList);
      featureEngineeringPredictiveModelBuilder.updatable(false);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void updatableInputFalseOutputUnsupportedOperationException1() throws Exception {
      final ArrayList arrayList = new ArrayList();
      final FeatureEngineeringPredictiveModelBuilder featureEngineeringPredictiveModelBuilder2 = new FeatureEngineeringPredictiveModelBuilder(null, arrayList);
      final ArrayList arrayList1 = new ArrayList();
      final FeatureEngineeringPredictiveModelBuilder featureEngineeringPredictiveModelBuilder1 = new FeatureEngineeringPredictiveModelBuilder(featureEngineeringPredictiveModelBuilder2, arrayList1);
      final ArrayList arrayList2 = new ArrayList();
      final FeatureEngineeringPredictiveModelBuilder featureEngineeringPredictiveModelBuilder = new FeatureEngineeringPredictiveModelBuilder(featureEngineeringPredictiveModelBuilder1, arrayList2);
      featureEngineeringPredictiveModelBuilder.updatable(false);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void updatableInputFalseOutputUnsupportedOperationException2() throws Exception {
      final ArrayList arrayList = new ArrayList();
      final FeatureEngineeringPredictiveModelBuilder featureEngineeringPredictiveModelBuilder2 = new FeatureEngineeringPredictiveModelBuilder(null, arrayList);
      final ArrayList arrayList1 = new ArrayList();
      arrayList1.add(0);
      final FeatureEngineeringPredictiveModelBuilder featureEngineeringPredictiveModelBuilder1 = new FeatureEngineeringPredictiveModelBuilder(featureEngineeringPredictiveModelBuilder2, arrayList1);
      final ArrayList arrayList2 = new ArrayList();
      final FeatureEngineeringPredictiveModelBuilder featureEngineeringPredictiveModelBuilder = new FeatureEngineeringPredictiveModelBuilder(featureEngineeringPredictiveModelBuilder1, arrayList2);
      featureEngineeringPredictiveModelBuilder.updatable(false);
    }
}

