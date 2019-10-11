package quickdt.collections;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
/**
 * Created by ian on 3/2/14.
 */
public class ValueSummingMapTest {
    @Test
    public void simpleTest() {
        ValueSummingMap<String> valueSummingMap = new ValueSummingMap<String>();
        Assert.assertEquals(valueSummingMap.getSumOfValues(), 0.0);
        valueSummingMap.put("a", 1);
        Assert.assertEquals(valueSummingMap.getSumOfValues(), 1.0);
        valueSummingMap.put("a", 1);
        Assert.assertEquals(valueSummingMap.getSumOfValues(), 1.0);
        valueSummingMap.put("b", 1);
        Assert.assertEquals(valueSummingMap.getSumOfValues(), 2.0);
        valueSummingMap.addToValue("b", 2);
        Assert.assertEquals(valueSummingMap.getSumOfValues(), 4.0);
        Assert.assertEquals(valueSummingMap.get("b"), 3.0);
        valueSummingMap.remove("b");
        Assert.assertEquals(valueSummingMap.getSumOfValues(), 1.0);
    }

    @Test
    public void containsKeyInputZeroOutputFalse() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        Assert.assertFalse(valueSummingMap.containsKey(0));
    }

    @Test
    public void containsValueInputZeroOutputFalse() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        Assert.assertFalse(valueSummingMap.containsValue(0));
    }

    @Test
    public void getInputNegativeOutputNegative() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        final HashMap hashMap = new HashMap();
        hashMap.put(67_241_985, -32_767);
        hashMap.put(-2_029_942_567, -131_056);
        valueSummingMap.putAll(hashMap);
        Assert.assertEquals(-131_056, valueSummingMap.get(-2_029_942_567));
    }

    @Test
    public void getInputNegativeOutputZero() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        final HashMap hashMap = new HashMap();
        hashMap.put(-176_178_963, 0);
        valueSummingMap.putAll(hashMap);
        Assert.assertEquals(0, valueSummingMap.get(-176_178_963));
    }

    @Test
    public void getInputZeroOutputNull() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        Assert.assertNull(valueSummingMap.get(0));
    }

    @Test
    public void getSumOfValuesOutputZero() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        Assert.assertEquals(0.0, valueSummingMap.getSumOfValues(), 0.0);
    }

    @Test
    public void isEmptyOutputTrue() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        Assert.assertTrue(valueSummingMap.isEmpty());
    }

    @Test
    public void putAllInputNotNullOutputVoid() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        final HashMap m = new HashMap();
        m.put(1_749_391_428, -32_768);
        valueSummingMap.putAll(m);
        Assert.assertEquals(-32768.0, valueSummingMap.getSumOfValues(), 0.0);
    }

    @Test
    public void putAllInputNotNullOutputVoid1() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        final HashMap hashMap = new HashMap();
        hashMap.put(-1_480_659_284, -1);
        hashMap.put(-1_480_659_796, -60_817_407);
        hashMap.put(-1_476_464_980, -65_536);
        valueSummingMap.putAll(hashMap);
        final HashMap m = new HashMap();
        m.put(-1_480_659_028, -32_768);
        m.put(-1_476_464_980, -2_086_666_243);
        valueSummingMap.putAll(m);
        Assert.assertEquals(-0x1.00010006p+31 /* -2.14752e+09 */, valueSummingMap.getSumOfValues(), 0.0);
    }

    @Test
    public void putInputPositiveNegativeOutputNull() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        final HashMap hashMap = new HashMap();
        hashMap.put(1_695_458_229, -2047);
        valueSummingMap.putAll(hashMap);
        Assert.assertNull(valueSummingMap.put(1_628_349_425, -2_752_552));
        Assert.assertEquals(-0x1.504138p+21 /* -2.7546e+06 */, valueSummingMap.getSumOfValues(), 0.0);
    }

    @Test
    public void putInputPositivePositiveOutputNull() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        Assert.assertNull(valueSummingMap.put(1_628_349_425, 42));
        Assert.assertEquals(42.0, valueSummingMap.getSumOfValues(), 0.0);
    }

    @Test
    public void putInputPositivePositiveOutputPositive() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        final HashMap hashMap = new HashMap();
        hashMap.put(1_695_458_229, 1);
        valueSummingMap.putAll(hashMap);
        Assert.assertEquals(1, valueSummingMap.put(1_695_458_229, 2_754_606));
        Assert.assertEquals(0x1.50417p+21 /* 2.75461e+06 */, valueSummingMap.getSumOfValues(), 0.0);
    }

    @Test
    public void putInputZeroZeroOutputNull() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        Assert.assertNull(valueSummingMap.put(0, 0));
    }

    @Test
    public void removeInputNegativeOutputNull() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        final HashMap hashMap = new HashMap();
        hashMap.put(-47_780_058, 16_810_368);
        hashMap.put(-47_779_994, 16_810_368);
        hashMap.put(-47_779_982, 16_810_368);
        valueSummingMap.putAll(hashMap);
        Assert.assertNull(valueSummingMap.remove(-1_523_593_872));
    }

    @Test
    public void removeInputNegativeOutputPositive() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        final HashMap hashMap = new HashMap();
        hashMap.put(-47_780_058, 16_810_368);
        hashMap.put(-47_779_994, 16_810_368);
        hashMap.put(-1_523_593_872, 16_810_368);
        valueSummingMap.putAll(hashMap);
        Assert.assertEquals(16_810_368, valueSummingMap.remove(-1_523_593_872));
        Assert.assertEquals(0x1.00818p+25 /* 3.36207e+07 */, valueSummingMap.getSumOfValues(), 0.0);
    }

    @Test
    public void removeInputPositiveOutputNull() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        Assert.assertNull(valueSummingMap.remove(181_994_713));
    }

    @Test
    public void removeInputZeroOutputNull() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        Assert.assertNull(valueSummingMap.remove(0));
    }

    @Test
    public void sizeOutputZero() {
        final ValueSummingMap valueSummingMap = new ValueSummingMap();
        Assert.assertEquals(0, valueSummingMap.size());
    }
}
