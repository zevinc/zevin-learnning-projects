package com.zevin.jdk.bitset;

import java.util.BitSet;

/**
 * java.util.BitSet API 示例代码。
 * <p>
 * BitSet 是一个位向量（bit vector），每一位可以是 0 或 1，适合高效存储和处理大量布尔值。
 * 底层使用 long[] 存储位数据，每个 long 存储 64 位，因此内存占用极小且位运算极快。
 * </p>
 *
 * <h3>常用场景</h3>
 * <ul>
 *   <li>海量数据去重（布隆过滤器的基础）</li>
 *   <li>权限位标志存储</li>
 *   <li>排序和查找</li>
 *   <li>数据压缩表示</li>
 * </ul>
 *
 * @author zevin
 */
public class BitSetApiExample {

    public static void main(String[] args) {
        constructionDemo();
        setClearFlipDemo();
        getDemo();
        logicalOpsDemo();
        queryDemo();
        navigationDemo();
        conversionDemo();
        streamDemo();
    }

    // ==================== 1. 构造方法 ====================

    /** 两种构造方式：无参构造（默认 64 位）和有参构造（指定初始位数）。 */
    static void constructionDemo() {
        System.out.println("========== 1. 构造方法 ==========");

        // 无参构造，初始容量 64 位（一个 long），自动扩容
        BitSet bs1 = new BitSet();
        System.out.println("无参构造 BitSet().size() = " + bs1.size()); // 64

        // 指定初始位数（实际会向上对齐到 64 的倍数）
        BitSet bs2 = new BitSet(100);
        System.out.println("指定 100 位 BitSet(100).size() = " + bs2.size()); // 128（2 个 long）

        // 从字节数组还原
        byte[] bytes = {0b0000_0101}; // 第 0 位和第 2 位为 1
        BitSet bs3 = BitSet.valueOf(bytes);
        System.out.println("从 byte[] 还原: " + bs3); // {0, 2}

        // 从 long 数组还原
        long[] longs = {0b1010L}; // 第 1 位和第 3 位为 1
        BitSet bs4 = BitSet.valueOf(longs);
        System.out.println("从 long[] 还原: " + bs4); // {1, 3}

        System.out.println();
    }

    // ==================== 2. 设置 / 清除 / 翻转 ====================

    /** set、clear、flip 的三类重载：单点、区间、区间+值控制。 */
    static void setClearFlipDemo() {
        System.out.println("========== 2. set / clear / flip ==========");

        BitSet bs = new BitSet();

        // --- set ---
        // 设置指定位为 true
        bs.set(3);
        bs.set(5);
        System.out.println("set(3)、set(5) 后: " + bs); // {3, 5}

        // 设置指定位为指定值
        bs.set(3, false); // 将第 3 位改回 false
        System.out.println("set(3, false) 后: " + bs); // {5}

        // 设置区间 [fromIndex, toIndex) 全部为 true
        bs.set(10, 15);
        System.out.println("set(10, 15) 后: " + bs); // {5, 10, 11, 12, 13, 14}

        // 设置区间 [fromIndex, toIndex) 为指定值
        bs.set(10, 13, false);
        System.out.println("set(10, 13, false) 后: " + bs); // {5, 13, 14}

        // --- clear ---
        // 清除单一位
        bs.clear(13);
        // 清除区间
        // 全部清除
        System.out.println("clear(13) 后: " + bs); // {5, 14}

        // --- flip ---
        // 翻转单一位（0 → 1, 1 → 0）
        bs.flip(5);
        System.out.println("flip(5) 后: " + bs); // {14}
        bs.flip(5);
        System.out.println("再次 flip(5) 后: " + bs); // {5, 14}

        // 翻转区间
        bs.flip(0, 8);
        System.out.println("flip(0, 8) 后: " + bs); // 0-7 位全部翻转

        System.out.println();
    }

    // ==================== 3. 获取 ====================

    /** get 单点和 get 区间（返回新 BitSet）。 */
    static void getDemo() {
        System.out.println("========== 3. get ==========");

        BitSet bs = new BitSet();
        bs.set(3);
        bs.set(7);
        bs.set(10);

        // 获取单一位的值
        System.out.println("bs.get(3) = " + bs.get(3));   // true
        System.out.println("bs.get(4) = " + bs.get(4));   // false

        // 获取区间，返回一个新的 BitSet（不修改原 BitSet）
        BitSet sub = bs.get(3, 11);
        System.out.println("bs.get(3, 11) = " + sub);     // {0, 4, 7}（注意下标偏移！）

        System.out.println();
    }

    // ==================== 4. 逻辑运算 ====================

    /** and / or / xor / andNot —— 这些方法会修改调用者自身。 */
    static void logicalOpsDemo() {
        System.out.println("========== 4. 逻辑运算 ==========");

        BitSet a = new BitSet();
        a.set(0); a.set(2); a.set(4); // {0, 2, 4}

        BitSet b = new BitSet();
        b.set(2); b.set(4); b.set(6); // {2, 4, 6}

        // AND —— 交集，修改 a
        BitSet andResult = (BitSet) a.clone();
        andResult.and(b);
        System.out.println("a AND b = " + andResult); // {2, 4}

        // OR —— 并集，修改 a
        BitSet orResult = (BitSet) a.clone();
        orResult.or(b);
        System.out.println("a OR  b = " + orResult); // {0, 2, 4, 6}

        // XOR —— 对称差集，修改 a
        BitSet xorResult = (BitSet) a.clone();
        xorResult.xor(b);
        System.out.println("a XOR b = " + xorResult); // {0, 6}

        // andNot —— 差集 a - b，修改 a
        BitSet andNotResult = (BitSet) a.clone();
        andNotResult.andNot(b);
        System.out.println("a - b (andNot) = " + andNotResult); // {0}

        System.out.println();
    }

    // ==================== 5. 查询方法 ====================

    /** cardinality、length、size、isEmpty、intersects。 */
    static void queryDemo() {
        System.out.println("========== 5. 查询方法 ==========");

        BitSet bs = new BitSet();
        bs.set(3);
        bs.set(100);
        bs.set(200);

        // cardinality() —— 值为 true 的位数
        System.out.println("bs.cardinality() = " + bs.cardinality()); // 3

        // length() —— 最高位 true 的索引 + 1（逻辑长度）
        System.out.println("bs.length() = " + bs.length()); // 201

        // size() —— 实际占用的位空间（物理容量，每次扩容翻倍）
        System.out.println("bs.size() = " + bs.size()); // 当前容量（>= 64）

        // isEmpty() —— 是否没有任何 true 位
        System.out.println("bs.isEmpty() = " + bs.isEmpty()); // false

        // intersects(BitSet) —— 两个 BitSet 是否有交集
        BitSet other = new BitSet();
        other.set(100);
        System.out.println("bs.intersects({100}) = " + bs.intersects(other)); // true
        other.clear();
        other.set(500);
        System.out.println("bs.intersects({500}) = " + bs.intersects(other)); // false

        System.out.println();
    }

    // ==================== 6. 位导航 ====================

    /** nextSetBit、nextClearBit、previousSetBit、previousClearBit —— 失败返回 -1。 */
    static void navigationDemo() {
        System.out.println("========== 6. 位导航 ==========");

        BitSet bs = new BitSet();
        bs.set(5);
        bs.set(10);
        bs.set(100);

        // nextSetBit(fromIndex) —— 从 fromIndex（含）开始向后找下一个 true 位
        System.out.println("bs.nextSetBit(0)   = " + bs.nextSetBit(0));   // 5
        System.out.println("bs.nextSetBit(6)   = " + bs.nextSetBit(6));   // 10
        System.out.println("bs.nextSetBit(101) = " + bs.nextSetBit(101)); // -1

        // nextClearBit(fromIndex) —— 从 fromIndex（含）开始向后找下一个 false 位
        System.out.println("bs.nextClearBit(0)  = " + bs.nextClearBit(0));   // 0
        System.out.println("bs.nextClearBit(5)  = " + bs.nextClearBit(5));   // 6

        // previousSetBit(fromIndex) —— 从 fromIndex（含）开始向前找
        System.out.println("bs.previousSetBit(7)   = " + bs.previousSetBit(7));   // 5
        System.out.println("bs.previousSetBit(4)   = " + bs.previousSetBit(4));   // -1

        // previousClearBit(fromIndex) —— 从 fromIndex（含）开始向前找 false 位
        System.out.println("bs.previousClearBit(5) = " + bs.previousClearBit(5)); // 4

        System.out.println();
    }

    // ==================== 7. 字节 / 长整数转换 ====================

    /** toByteArray、toLongArray、valueOf —— 用于持久化或网络传输。 */
    static void conversionDemo() {
        System.out.println("========== 7. 字节/长整数转换 ==========");

        BitSet bs = new BitSet();
        bs.set(0);
        bs.set(3);
        bs.set(7);
        System.out.println("原始 BitSet: " + bs); // {0, 3, 7}

        // toByteArray() —— 转为 byte[]，小端序
        byte[] byteArr = bs.toByteArray();
        System.out.print("toByteArray(): [");
        for (int i = 0; i < byteArr.length; i++) {
            System.out.print(String.format("0x%02X", byteArr[i] & 0xFF));
            if (i < byteArr.length - 1) System.out.print(", ");
        }
        System.out.println("]");

        // toLongArray() —— 转为 long[]，小端序
        long[] longArr = bs.toLongArray();
        System.out.print("toLongArray(): [");
        for (int i = 0; i < longArr.length; i++) {
            System.out.print(String.format("0x%016X", longArr[i]));
            if (i < longArr.length - 1) System.out.print(", ");
        }
        System.out.println("]");

        // valueOf(byte[]) —— 从字节数组还原
        BitSet restored = BitSet.valueOf(byteArr);
        System.out.println("从 byte[] 还原: " + restored);

        System.out.println();
    }

    // ==================== 8. 流式处理 ====================

    /** stream() 返回所有 true 位索引的 IntStream。 */
    static void streamDemo() {
        System.out.println("========== 8. stream() 流式处理 ==========");

        BitSet bs = new BitSet();
        bs.set(2);
        bs.set(5);
        bs.set(8);
        bs.set(15);

        // 遍历所有 true 位的索引
        System.out.print("stream() 遍历: ");
        bs.stream().forEach(i -> System.out.print(i + " "));
        System.out.println();

        // 配合流操作：筛选大于 5 的位索引
        System.out.print("筛选 >5 的位: ");
        bs.stream()
                .filter(i -> i > 5)
                .forEach(i -> System.out.print(i + " "));
        System.out.println();

        // 转换为数组
        int[] indices = bs.stream().toArray();
        System.out.print("转为 int[]: ");
        for (int idx : indices) {
            System.out.print(idx + " ");
        }
        System.out.println();

        System.out.println();
    }
}
