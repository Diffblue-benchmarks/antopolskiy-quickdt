package quickdt.predictiveModels.downsamplingPredictiveModel;

import org.junit.Assert;
import org.junit.Test;
/**
 * Created by ian on 4/23/14.
 */
public class UtilsTest {

  @Test
  public void correctProbabilityInputZeroZeroOutputZero() {
    Assert.assertEquals(0.0, Utils.correctProbability(0.0, 0.0), 0.0);
  }
}
