package org.spongycastle.math.ntru.euclid;

/* loaded from: classes.dex */
public class IntEuclidean {
    public int gcd;
    public int x;
    public int y;

    private IntEuclidean() {
    }

    public static IntEuclidean calculate(int a, int b) {
        int x = 0;
        int lastx = 1;
        int y = 1;
        int lasty = 0;
        while (b != 0) {
            int quotient = a / b;
            int temp = a;
            a = b;
            b = temp % b;
            int temp2 = x;
            x = lastx - (quotient * x);
            lastx = temp2;
            int temp3 = y;
            y = lasty - (quotient * y);
            lasty = temp3;
        }
        IntEuclidean result = new IntEuclidean();
        result.x = lastx;
        result.y = lasty;
        result.gcd = a;
        return result;
    }
}