package quickdt.crossValidation;

import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;
import org.apache.mahout.classifier.evaluation.Auc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chris on 5/5/2014.
 */
public class WeightedAUCCrossValLossFunctionTest {

    @Test(expected = RuntimeException.class)
    public void testOnlySupportBinaryClassifications() {
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");
        PredictiveModel predictiveModel = Mockito.mock(PredictiveModel.class);
        AbstractInstance instance = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance.getClassification()).thenReturn("instance1");
        Mockito.when(instance.getWeight()).thenReturn(1.0);
        AbstractInstance instance2 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance2.getClassification()).thenReturn("instance2");
        Mockito.when(instance2.getWeight()).thenReturn(1.0);
        AbstractInstance instance3 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance3.getClassification()).thenReturn("instance3");
        Mockito.when(instance3.getWeight()).thenReturn(1.0);
        List<AbstractInstance> instances = new LinkedList<>();
        instances.add(instance);
        instances.add(instance2);
        instances.add(instance3);
        crossValLoss.getLoss(instances, predictiveModel);
    }

    @Test
    public void testGetTotalLoss() {
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");
        PredictiveModel predictiveModel = Mockito.mock(PredictiveModel.class);
        Attributes test1Attributes = Mockito.mock(Attributes.class);
        Attributes test2Attributes = Mockito.mock(Attributes.class);
        Attributes test3Attributes = Mockito.mock(Attributes.class);
        Attributes test4Attributes = Mockito.mock(Attributes.class);
        Mockito.when(predictiveModel.getProbability(test1Attributes, "test1")).thenReturn(0.5);
        Mockito.when(predictiveModel.getProbability(test2Attributes, "test1")).thenReturn(0.3);
        Mockito.when(predictiveModel.getProbability(test3Attributes, "test1")).thenReturn(0.4);
        Mockito.when(predictiveModel.getProbability(test4Attributes, "test1")).thenReturn(0.2);

        AbstractInstance instance = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance.getClassification()).thenReturn("test1");
        Mockito.when(instance.getWeight()).thenReturn(1.0);
        Mockito.when(instance.getAttributes()).thenReturn(test1Attributes);

        AbstractInstance instance2 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance2.getClassification()).thenReturn("test1");
        Mockito.when(instance2.getWeight()).thenReturn(1.0);
        Mockito.when(instance2.getAttributes()).thenReturn(test2Attributes);

        AbstractInstance instance3 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance3.getClassification()).thenReturn("test0");
        Mockito.when(instance3.getWeight()).thenReturn(1.0);
        Mockito.when(instance3.getAttributes()).thenReturn(test3Attributes);

        AbstractInstance instance4 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance4.getClassification()).thenReturn("test0");
        Mockito.when(instance4.getWeight()).thenReturn(1.0);
        Mockito.when(instance4.getAttributes()).thenReturn(test4Attributes);

        List<AbstractInstance> instances = new LinkedList<>();
        instances.add(instance);
        instances.add(instance2);
        instances.add(instance3);
        instances.add(instance4);

        //AUC Points at 0:0 0:.5 .5:.5 1:.5 1:1
        double expectedArea = .25;

        Assert.assertEquals(expectedArea, crossValLoss.getLoss(instances, predictiveModel));
    }

    @Test
    public void testSortDataByProbability() {
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");
        List<WeightedAUCCrossValLossFunction.AUCData> aucDataList = getAucDataList();
        crossValLoss.sortDataByProbability(aucDataList);
        double probability = 0;
        for(WeightedAUCCrossValLossFunction.AUCData aucData : aucDataList) {
            Assert.assertTrue(aucData.getProbability() >= probability);
            probability = aucData.getProbability();
        }
    }

    @Test
    public void testGetAUCPoint() {
        //FPR = FP / (FP + TN)
        //TRP = TP / (TP + FN)
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");
        WeightedAUCCrossValLossFunction.AUCPoint aucPoint = crossValLoss.getAUCPoint(2, 2, 0, 1);
        Assert.assertEquals(1.0, aucPoint.getFalsePositiveRate());
        Assert.assertEquals(2.0/3.0, aucPoint.getTruePositiveRate());
        aucPoint = crossValLoss.getAUCPoint(2, 1, 1, 1);
        Assert.assertEquals(0.5, aucPoint.getFalsePositiveRate());
        Assert.assertEquals(2.0/3.0, aucPoint.getTruePositiveRate());
        aucPoint = crossValLoss.getAUCPoint(2, 0, 0, 1);
        Assert.assertEquals(0.0, aucPoint.getFalsePositiveRate());
        Assert.assertEquals(2.0/3.0, aucPoint.getTruePositiveRate());
        aucPoint = crossValLoss.getAUCPoint(0, 1, 3, 0);
        Assert.assertEquals(0.25, aucPoint.getFalsePositiveRate());
        Assert.assertEquals(0.0, aucPoint.getTruePositiveRate());
    }

    @Test
    public void testGetAucPointsFromData() {
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");
        List<WeightedAUCCrossValLossFunction.AUCData> aucDataList = getAucDataList();
        crossValLoss.sortDataByProbability(aucDataList);
        ArrayList<WeightedAUCCrossValLossFunction.AUCPoint> aucPoints = crossValLoss.getAUCPointsFromData(aucDataList);
        //We should have the same number of points as data plus 1 for threshold 0
        Assert.assertEquals(aucDataList.size()+1, aucPoints.size());
        //0 false negative, 0 true negative, 4 true positive, 2 false positive: FPR = 2 / 2, TRP = 4 / 4 get(0)
        //1 false negative, 0 true negative, 3 true positive, 2 false positive: FPR = 2 / 2, TRP = 3 / 4 get(1)
        //1 false negative, 1 true negative, 3 true positive, 1 false positive: FPR = 1 / 2, TRP = 3 / 4 get(2)
        //2 false negative, 1 true negative, 2 true positive, 1 false positive: FPR = 1 / 2, TRP = 2 / 4 get(3)
        //2 false negative, 2 true negative, 2 true positive, 0 false positive: FPR = 0, TRP = 2 / 4 get(4)
        //3 false negative, 2 true negative, 1 true positive, 0 false positive: FPR = 0, TRP = 1 / 4 get(5)
        //4 false negative, 2 true negative, 0 true positive, 0 false positive: FPR = 0, TRP = 0 get(6)
        Assert.assertEquals(1.0, aucPoints.get(0).getFalsePositiveRate());
        Assert.assertEquals(1.0, aucPoints.get(0).getTruePositiveRate());
        Assert.assertEquals(1.0, aucPoints.get(1).getFalsePositiveRate());
        Assert.assertEquals(3.0/4.0, aucPoints.get(1).getTruePositiveRate());
        Assert.assertEquals(0.5, aucPoints.get(2).getFalsePositiveRate());
        Assert.assertEquals(3.0/4.0, aucPoints.get(2).getTruePositiveRate());
        Assert.assertEquals(0.5, aucPoints.get(3).getFalsePositiveRate());
        Assert.assertEquals(2.0/4.0, aucPoints.get(3).getTruePositiveRate());
        Assert.assertEquals(0.0, aucPoints.get(4).getFalsePositiveRate());
        Assert.assertEquals(2.0/4.0, aucPoints.get(4).getTruePositiveRate());
        Assert.assertEquals(0.0, aucPoints.get(5).getFalsePositiveRate());
        Assert.assertEquals(1.0/4.0, aucPoints.get(5).getTruePositiveRate());
        Assert.assertEquals(0.0, aucPoints.get(6).getFalsePositiveRate());
        Assert.assertEquals(0.0, aucPoints.get(6).getTruePositiveRate());
        aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData("test1", 1.0, 0.8));
        aucPoints = crossValLoss.getAUCPointsFromData(aucDataList);
        //Added data with same probability, should not result in new number of points but will change rates
        Assert.assertEquals(aucDataList.size(), aucPoints.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testTotalLossNoData() {
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");
        crossValLoss.getLoss(Collections.EMPTY_LIST, Mockito.mock(PredictiveModel.class));
    }

    private List<WeightedAUCCrossValLossFunction.AUCData> getAucDataList() {
        List<WeightedAUCCrossValLossFunction.AUCData> aucDataList = new ArrayList<>();
        aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData("test1", 1.0, 0.5));
        aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData("test0", 1.0, 0.3));
        aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData("test0", 1.0, 0.6));
        aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData("test1", 1.0, 0.2));
        aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData("test1", 1.0, 0.7));
        aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData("test1", 1.0, 0.8));
        return aucDataList;
    }

    @Test
    public void testAgainstMahout() {
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");
        List<WeightedAUCCrossValLossFunction.AUCData> aucDataList = new ArrayList<>();
        int dataSize = 9000; //mahout only stores 10000 data points, test against less than what they consider
        for(int i = 0; i < dataSize; i++) {
            String classification = "test0";
            if (i % 5 == 0) {
                classification = "test1";
            }
            aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData(classification, 1.0, Math.random()));
        }
        crossValLoss.sortDataByProbability(aucDataList);
        ArrayList<WeightedAUCCrossValLossFunction.AUCPoint> aucPoints = crossValLoss.getAUCPointsFromData(aucDataList);
        double aucCrossValLoss = crossValLoss.getAUCLoss(aucPoints);

        Auc auc = new Auc();
        for(WeightedAUCCrossValLossFunction.AUCData aucData : aucDataList) {
            auc.add("test1".equals(aucData.getClassification()) ? 1 : 0, aucData.getProbability());
        }

        double mahoutAucLoss = 1.0 - auc.auc();
        //These aren't matching exactly, but the difference is minimal
        double acceptableDifference = 0.000000000001;
        Assert.assertTrue(Math.abs(mahoutAucLoss - aucCrossValLoss) < acceptableDifference);
    }

    @Test
    public void getAUCPointInputPositivePositiveNegativeNegativeOutputNotNull() {
      final WeightedAUCCrossValLossFunction weightedAUCCrossValLossFunction = new WeightedAUCCrossValLossFunction(false);
      final WeightedAUCCrossValLossFunction.AUCPoint actual = weightedAUCCrossValLossFunction.getAUCPoint(65536.0, 1.0, -32.0, -65536.0);
      Assert.assertNotNull(actual);
      Assert.assertEquals(-0x1.0842108421084p-5 /* -0.0322581 */, actual.getFalsePositiveRate(), 0.0);
      Assert.assertEquals(0.0, actual.getTruePositiveRate(), 0.0);
    }

    @Test
    public void getAUCPointInputZeroPositiveNegativePositiveOutputNotNull() {
      final WeightedAUCCrossValLossFunction weightedAUCCrossValLossFunction = new WeightedAUCCrossValLossFunction(false);
      final WeightedAUCCrossValLossFunction.AUCPoint actual = weightedAUCCrossValLossFunction.getAUCPoint(0.0, 1.0, -32.0, 1.0);
      Assert.assertNotNull(actual);
      Assert.assertEquals(-0x1.0842108421084p-5 /* -0.0322581 */, actual.getFalsePositiveRate(), 0.0);
      Assert.assertEquals(0.0, actual.getTruePositiveRate(), 0.0);
    }

    @Test
    public void getAUCPointInputZeroZeroZeroZeroOutputNotNull() {
      final WeightedAUCCrossValLossFunction weightedAUCCrossValLossFunction = new WeightedAUCCrossValLossFunction(false);
      final WeightedAUCCrossValLossFunction.AUCPoint actual = weightedAUCCrossValLossFunction.getAUCPoint(0.0, 0.0, 0.0, 0.0);
      Assert.assertNotNull(actual);
      Assert.assertEquals(0.0, actual.getFalsePositiveRate(), 0.0);
      Assert.assertEquals(0.0, actual.getTruePositiveRate(), 0.0);
    }

    @Test
    public void getAUCPointsFromDataInputNotNullOutput1() {
      final WeightedAUCCrossValLossFunction weightedAUCCrossValLossFunction = new WeightedAUCCrossValLossFunction(false);
      final ArrayList<WeightedAUCCrossValLossFunction.AUCData> aucDataList = new ArrayList<WeightedAUCCrossValLossFunction.AUCData>();
      final ArrayList<WeightedAUCCrossValLossFunction.AUCPoint> actual = weightedAUCCrossValLossFunction.getAUCPointsFromData(aucDataList);
      Assert.assertNotNull(actual);
      Assert.assertEquals(1, actual.size());
      Assert.assertNotNull(actual.get(0));
      Assert.assertEquals(0.0, actual.get(0).getFalsePositiveRate(), 0.0);
      Assert.assertEquals(0.0, actual.get(0).getTruePositiveRate(), 0.0);
    }

    @Test
    public void getAUCPointsFromDataInputNotNullOutput11() {
      final WeightedAUCCrossValLossFunction weightedAUCCrossValLossFunction = new WeightedAUCCrossValLossFunction(false);
      final ArrayList<WeightedAUCCrossValLossFunction.AUCData> aucDataList = new ArrayList<WeightedAUCCrossValLossFunction.AUCData>();
      final ArrayList<WeightedAUCCrossValLossFunction.AUCPoint> actual = weightedAUCCrossValLossFunction.getAUCPointsFromData(aucDataList);
      Assert.assertNotNull(actual);
      Assert.assertEquals(1, actual.size());
      Assert.assertNotNull(actual.get(0));
      Assert.assertEquals(0.0, actual.get(0).getFalsePositiveRate(), 0.0);
    }

    @Test
    public void getAUCPointsFromDataInputNotNullOutput12() {
      final WeightedAUCCrossValLossFunction weightedAUCCrossValLossFunction = new WeightedAUCCrossValLossFunction(false);
      final ArrayList<WeightedAUCCrossValLossFunction.AUCData> aucDataList = new ArrayList<WeightedAUCCrossValLossFunction.AUCData>();
      final WeightedAUCCrossValLossFunction.AUCData weightedAUCCrossValLossFunction$AUCData = new WeightedAUCCrossValLossFunction.AUCData(false, 1.0, 0.0);
      aucDataList.add(weightedAUCCrossValLossFunction$AUCData);
      final ArrayList<WeightedAUCCrossValLossFunction.AUCPoint> actual = weightedAUCCrossValLossFunction.getAUCPointsFromData(aucDataList);
      Assert.assertNotNull(actual);
      Assert.assertEquals(1, actual.size());
      Assert.assertNotNull(actual.get(0));
      Assert.assertEquals(0.0, actual.get(0).getFalsePositiveRate(), 0.0);
      Assert.assertEquals(0.0, actual.get(0).getTruePositiveRate(), 0.0);
    }

    @Test
    public void getAUCPointsFromDataInputNotNullOutput13() {
      final WeightedAUCCrossValLossFunction weightedAUCCrossValLossFunction = new WeightedAUCCrossValLossFunction(true);
      final ArrayList<WeightedAUCCrossValLossFunction.AUCData> aucDataList = new ArrayList<WeightedAUCCrossValLossFunction.AUCData>();
      final WeightedAUCCrossValLossFunction.AUCData weightedAUCCrossValLossFunction$AUCData = new WeightedAUCCrossValLossFunction.AUCData(false, 4.0, 0.0);
      aucDataList.add(weightedAUCCrossValLossFunction$AUCData);
      final ArrayList<WeightedAUCCrossValLossFunction.AUCPoint> actual = weightedAUCCrossValLossFunction.getAUCPointsFromData(aucDataList);
      Assert.assertNotNull(actual);
      Assert.assertEquals(1, actual.size());
      Assert.assertNotNull(actual.get(0));
      Assert.assertEquals(0.0, actual.get(0).getFalsePositiveRate(), 0.0);
      Assert.assertEquals(0.0, actual.get(0).getTruePositiveRate(), 0.0);
    }

    @Test
    public void getAUCPointsFromDataInputNotNullOutput2() {
      final WeightedAUCCrossValLossFunction weightedAUCCrossValLossFunction = new WeightedAUCCrossValLossFunction(false);
      final ArrayList<WeightedAUCCrossValLossFunction.AUCData> aucDataList = new ArrayList<WeightedAUCCrossValLossFunction.AUCData>();
      final WeightedAUCCrossValLossFunction.AUCData weightedAUCCrossValLossFunction$AUCData = new WeightedAUCCrossValLossFunction.AUCData(false, 1.0, 0x1.00007ffcp+30 /* 1.07375e+09 */);
      aucDataList.add(weightedAUCCrossValLossFunction$AUCData);
      final ArrayList<WeightedAUCCrossValLossFunction.AUCPoint> actual = weightedAUCCrossValLossFunction.getAUCPointsFromData(aucDataList);
      Assert.assertNotNull(actual);
      Assert.assertEquals(2, actual.size());
      Assert.assertNotNull(actual.get(0));
      Assert.assertEquals(0.0, actual.get(0).getFalsePositiveRate(), 0.0);
      Assert.assertEquals(1.0, actual.get(0).getTruePositiveRate(), 0.0);
      Assert.assertNotNull(actual.get(1));
      Assert.assertEquals(0.0, actual.get(1).getFalsePositiveRate(), 0.0);
      Assert.assertEquals(0.0, actual.get(1).getTruePositiveRate(), 0.0);
    }

    @Test(expected = IllegalStateException.class)
    public void getLossInputNotNullNullOutputIllegalStateException() {
      final WeightedAUCCrossValLossFunction weightedAUCCrossValLossFunction = new WeightedAUCCrossValLossFunction(null);
      final ArrayList instances = new ArrayList();
      weightedAUCCrossValLossFunction.getLoss(instances, null);
    }

    @Test
    public void getFalsePositiveRateOutputZero() {
      final WeightedAUCCrossValLossFunction.AUCPoint weightedAUCCrossValLossFunctionAUCPoint = new WeightedAUCCrossValLossFunction.AUCPoint(0.0, 0.0);
      Assert.assertEquals(0.0, weightedAUCCrossValLossFunctionAUCPoint.getFalsePositiveRate(), 0.0);
    }

    @Test
    public void getTruePositiveRateOutputZero() {
      final WeightedAUCCrossValLossFunction.AUCPoint weightedAUCCrossValLossFunctionAUCPoint = new WeightedAUCCrossValLossFunction.AUCPoint(0.0, 0.0);
      Assert.assertEquals(0.0, weightedAUCCrossValLossFunctionAUCPoint.getTruePositiveRate(), 0.0);
    }

    @Test
    public void getClassificationOutputFalse() {
      final WeightedAUCCrossValLossFunction.AUCData weightedAUCCrossValLossFunctionAUCData = new WeightedAUCCrossValLossFunction.AUCData(false, 0.0, 0.0);
      Assert.assertFalse((boolean) weightedAUCCrossValLossFunctionAUCData.getClassification());
    }

    @Test
    public void getProbabilityOutputZero() {
      final WeightedAUCCrossValLossFunction.AUCData weightedAUCCrossValLossFunctionAUCData = new WeightedAUCCrossValLossFunction.AUCData(false, 0.0, 0.0);
      Assert.assertEquals(0.0, weightedAUCCrossValLossFunctionAUCData.getProbability(), 0.0);
    }

    @Test
    public void getWeightOutputZero() {
      final WeightedAUCCrossValLossFunction.AUCData weightedAUCCrossValLossFunctionAUCData = new WeightedAUCCrossValLossFunction.AUCData(false, 0.0, 0.0);
      Assert.assertEquals(0.0, weightedAUCCrossValLossFunctionAUCData.getWeight(), 0.0);
    }
}
