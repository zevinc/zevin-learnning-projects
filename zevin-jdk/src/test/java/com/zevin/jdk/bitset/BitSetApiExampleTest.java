package com.zevin.jdk.bitset;

import org.junit.jupiter.api.Test;

import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BitSet API 单元测试，验证各方法的行为。
 */
class BitSetApiExampleTest {

    @Test
    void testConstruction() {
        BitSet bs = new BitSet();
        assertEquals(64, bs.size());

        BitSet bs2 = new BitSet(100);
        assertEquals(128, bs2.size()); // 向上对齐到 64 的倍数

        BitSet bs3 = BitSet.valueOf(new byte[]{0b0000_0101});
        assertTrue(bs3.get(0));
        assertFalse(bs3.get(1));
        assertTrue(bs3.get(2));
    }

    @Test
    void testSetClearFlip() {
        BitSet bs = new BitSet();
        bs.set(10);
        assertTrue(bs.get(10));

        bs.set(10, false);
        assertFalse(bs.get(10));

        bs.set(5, 10); // [5, 10)
        assertTrue(bs.get(5));
        assertTrue(bs.get(9));
        assertFalse(bs.get(10));

        bs.set(5, 8, false);
        assertFalse(bs.get(5));
        assertFalse(bs.get(7));

        bs.flip(20);
        assertTrue(bs.get(20));
        bs.flip(20);
        assertFalse(bs.get(20));
    }

    @Test
    void testGetRange() {
        BitSet bs = new BitSet();
        bs.set(3);
        bs.set(7);
        bs.set(10);

        BitSet sub = bs.get(3, 11);
        // 区间 get 返回的 BitSet 中，原 bitset 的第 3 位在 sub 中是第 0 位
        assertTrue(sub.get(0));  // 原第 3 位 → sub 第 0 位
        assertTrue(sub.get(4));  // 原第 7 位 → sub 第 4 位
        assertTrue(sub.get(7));  // 原第 10 位 → sub 第 7 位
    }

    @Test
    void testLogicalOps() {
        BitSet a = new BitSet();
        a.set(0); a.set(2); a.set(4);
        BitSet b = new BitSet();
        b.set(2); b.set(4); b.set(6);

        BitSet andResult = (BitSet) a.clone();
        andResult.and(b);
        assertTrue(andResult.get(2));
        assertTrue(andResult.get(4));
        assertFalse(andResult.get(0));

        BitSet orResult = (BitSet) a.clone();
        orResult.or(b);
        assertTrue(orResult.get(0));
        assertTrue(orResult.get(2));
        assertTrue(orResult.get(4));
        assertTrue(orResult.get(6));

        BitSet xorResult = (BitSet) a.clone();
        xorResult.xor(b);
        assertTrue(xorResult.get(0));
        assertTrue(xorResult.get(6));
        assertFalse(xorResult.get(2));
    }

    @Test
    void testQuery() {
        BitSet bs = new BitSet();
        bs.set(3);
        bs.set(100);
        bs.set(200);

        assertEquals(3, bs.cardinality());
        assertEquals(201, bs.length());
        assertFalse(bs.isEmpty());

        BitSet other = new BitSet();
        other.set(100);
        assertTrue(bs.intersects(other));

        other.clear();
        other.set(500);
        assertFalse(bs.intersects(other));
    }

    @Test
    void testNavigation() {
        BitSet bs = new BitSet();
        bs.set(5);
        bs.set(10);

        assertEquals(5, bs.nextSetBit(0));
        assertEquals(10, bs.nextSetBit(6));
        assertEquals(-1, bs.nextSetBit(11));

        assertEquals(0, bs.nextClearBit(0));
        assertEquals(6, bs.nextClearBit(5));

        assertEquals(5, bs.previousSetBit(7));
        assertEquals(-1, bs.previousSetBit(4));
    }

    @Test
    void testConversion() {
        BitSet bs = new BitSet();
        bs.set(0);
        bs.set(3);
        bs.set(7);

        byte[] byteArr = bs.toByteArray();
        BitSet restored = BitSet.valueOf(byteArr);
        assertEquals(bs, restored);

        long[] longArr = bs.toLongArray();
        BitSet restored2 = BitSet.valueOf(longArr);
        assertEquals(bs, restored2);
    }

    @Test
    void testStream() {
        BitSet bs = new BitSet();
        bs.set(2);
        bs.set(5);
        bs.set(8);

        int[] indices = bs.stream().toArray();
        assertArrayEquals(new int[]{2, 5, 8}, indices);
    }
}
