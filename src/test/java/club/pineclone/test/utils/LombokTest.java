package club.pineclone.test.utils;

import junit.framework.TestCase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class LombokTest extends TestCase {

    public void testLombok() {
        APojo aPojo = new APojo();
        aPojo.setName("pineclone");
        aPojo.setValue(12);
        System.out.println(aPojo);
    }


    @Getter
    @Setter
    @ToString
    private static class APojo {
        private String name;
        private int value;
    }

}
