package christian_willian;

public class JGLU {
    // Retorna matriz identidade 4x4
    public float[] matrixIdentity() {

        float[][] identity = {
                { 1.0f, 0.0f, 0.0f, 0.0f },
                { 0.0f, 1.0f, 0.0f, 0.0f },
                { 0.0f, 0.0f, 1.0f, 0.0f },
                { 0.0f, 0.0f, 0.0f, 1.0f }
        };

        float[] plainIdentity = toPlainMatrix4x4(identity);

        return plainIdentity;
    }

    // Retorna o resultado da multiplicação das matrizes m1 e m0 (m1 . m0).
    // O resultado das matrizes m0 e m1 são matrizes 4x4.
    public float[] matrixMultiply(float[] m1, float[] m0) {
        float[][] matrixA = toSquareMatrix4x4(m1);

        float[][] matrixB = toSquareMatrix4x4(m0);

        float[][] result = multiplyMatrix4x4(matrixA, matrixB);

        float[] plainResult = toPlainMatrix4x4(result);

        return plainResult;
    }

    public float[] matrixTranslate(float x, float y, float z) {
        float[][] result = {
                { 1.0f, 0.0f, 0.0f, x },
                { 0.0f, 1.0f, 0.0f, y },
                { 0.0f, 0.0f, 1.0f, z },
                { 0.0f, 0.0f, 0.0f, 1.0f }
        };

        float[] plainResult = toPlainMatrix4x4(result);

        return plainResult;
    }

    public float[] matrixRotate(float angle, float x, float y, float z) {
        float[][] result;
        float rad = (float) ((angle * Math.PI) / 180);
        float sin = (float) Math.sin(rad);
        float cos = (float) Math.cos(rad);
        float module = module(x, y, z);
        float xn = x / module;
        float yn = y / module;
        float zn = z / module;

        if (x == 1.0f) {
            // x rotate

            float[][] matrix = {
                    { 1.0f, 0.0f, 0.0f, 0.0f },
                    { 0.0f, cos, -sin, 0.0f },
                    { 0.0f, sin, cos, 0.0f },
                    { 0.0f, 0.0f, 0.0f, 1.0f }
            };

            result = matrix;
        } else if (y == 1.0f && z == 0.0f) {
            // y rotate

            float[][] matrix = {
                    { cos, 0.0f, sin, 0.0f },
                    { 0.0f, 1.0f, 0.0f, 0.0f },
                    { -sin, 0.0f, cos, 0.0f },
                    { 0.0f, 0.0f, 0.0f, 1.0f }
            };

            result = matrix;
        } else if (y == 0.0f && z == 1.0f) {
            // z rotate

            float[][] matrix = {
                    { cos, -sin, 0.0f, 0.0f },
                    { sin, cos, 0.0f, 0.0f },
                    { 0.0f, 0.0f, 1.0f, 0.0f },
                    { 0.0f, 0.0f, 0.0f, 1.0f }
            };

            result = matrix;
        } else {
            // arbitrary rotate
            float xn_2 = xn * xn;
            float yn_2 = yn * yn;
            float zn_2 = zn * zn;

            float[][] matrix = {
                    { xn_2 + (1 - xn_2) * cos, xn * yn * (1 - cos) - zn * sin, xn * zn * (1 - cos) + yn * sin, 0.0f },
                    { xn * yn * (1 - cos) + zn * sin, yn_2 + (1 - yn_2) * cos, yn * zn * (1 - cos) - xn * sin, 0.0f },
                    { xn * zn * (1 - cos) - yn * sin, yn * zn * (1 - cos) + xn * sin, zn_2 + (1 - zn_2) * cos, 0.0f },
                    { 0.0f, 0.0f, 0.0f, 1.0f }
            };

            result = matrix;
        }

        float[] plainResult = toPlainMatrix4x4(result);

        return plainResult;
    }

    //// Abaixo mais métodos auxiliares ////
    ////////////////////////////////////////

    public static float[][] toSquareMatrix4x4(float[] matrix) {
        float[][] square = new float[4][4];

        for (int i = 0; i < square.length; i++) {
            for (int j = 0; j < square[i].length; j++) {
                square[j][i] = matrix[4 * i + j];
            }
        }

        return square;
    }

    public static float[] toPlainMatrix4x4(float[][] matrix) {
        float[] plain = new float[16];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                plain[4 * i + j] = matrix[j][i];
            }
        }

        return plain;
    }

    public static float[][] multiplyMatrix4x4(float[][] matrix1, float[][] matrix2) {
        float[][] result = new float[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                float elem = 0;

                for (int k = 0; k < matrix1.length; k++) {
                    elem += matrix1[i][k] * matrix2[k][j];
                }

                result[i][j] = elem;
            }
        }

        return result;
    }

    public static float module(float x, float y, float z) {
        return (float) Math.sqrt((x * x) + (y * y) + (z * z));
    }

    public float[] lookAt(float ex, float ey, float ez, float cx, float cy,
            float cz, float ux, float uy, float uz) {
        float[][] result = getLookAtMatrix4x4(ex, ey, ez, cx, cy,
                cz, ux, uy, uz);
        float[] plainResult = toPlainMatrix4x4(result);
        return plainResult;
    }

    public static float[][] getLookAtMatrix4x4(float ex, float ey, float ez,
            float cx, float cy, float cz, float ux, float uy, float uz) {
        float[] u = create(ux, uy, uz);
        // vetor unitário D
        float[] d = subtract(cx, cy, cz, ex, ey, ez);
        float[] dn = normalize(d);
        // vetor unitário I
        float[] i = crossProduct(d, u);
        float[] in = normalize(i);
        // vetor unitário O
        float[] on = crossProduct(in, dn);
        float[][] result = {
                { in[0], in[1], in[2], 0 },
                { on[0], on[1], on[2], 0 },
                { -dn[0], -dn[1], -dn[2], 0 },
                { 0, 0, 0, 1 }
        };
        return result;
    }

    public static float[] create(float x, float y, float z) {
        float[] result = { x, y, z };
        return result;
    }

    public static float[] subtract(float x1, float y1, float z1, float x2, float y2, float z2) {
        float newX = 0.0f, newY = 0.0f, newZ = 0.0f;
        newX = x1 - x2;
        newY = y1 - y2;
        newZ = z1 - z2;
        float[] result = { newX, newY, newZ };
        return result;
    }

    public static float[] normalize(float x, float y, float z) {
        float xn = 0.0f, yn = 0.0f, zn = 0.0f, module = 0.0f;
        module = module(x, y, z);
        xn = x / module;
        yn = y / module;
        zn = z / module;
        float[] result = { xn, yn, zn };
        return result;
    }

    public static float[] normalize(float[] v) {
        return normalize(v[0], v[1], v[2]);
    }

    public static float[] crossProduct(float[] v1, float[] v2) {
        return crossProduct(v1[0], v1[1], v1[2], v2[0], v2[1],
                v2[2]);
    }

    public static float[] crossProduct(float x1, float y1, float z1,
            float x2, float y2, float z2) {
        float newX = 0.0f, newY = 0.0f, newZ = 0.0f;
        newX = (y1 * z2) - (z1 * y2);
        newY = (z1 * x2) - (x1 * z2);
        newZ = (x1 * y2) - (y1 * x2);
        float[] result = { newX, newY, newZ };
        return result;
    }
}
