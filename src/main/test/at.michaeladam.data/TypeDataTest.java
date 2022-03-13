package at.michaeladam.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TypeDataTest {

    @Test
    void TestGenericExtraction() {
        TypeData extractedType = TypeData.ofString(new FieldHolder() {
            @Override
            public String[] getImports() {
                return new String[0];
            }
        }, "List<String>");

        Assertions.assertNotNull(extractedType.getGeneric());
    }

    @Test
    void TestComplexGeneric() {
        TypeData extractedType = TypeData.ofString(new FieldHolder() {
            @Override
            public String[] getImports() {
                return new String[0];
            }
        }, "1<2<3<4<5<6<7<8<9>>>>>>>>");

        Assertions.assertNotNull(extractedType.getGeneric());
    }
    @Test
    void TestLayeredGeneric() {
        TypeData extractedType = TypeData.ofString(new FieldHolder() {
            @Override
            public String[] getImports() {
                return new String[0];
            }
        }, "Generic<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z>");

        Assertions.assertNotNull(extractedType.getGeneric());
        Assertions.assertEquals(26, extractedType.getGeneric().length);
    }
}