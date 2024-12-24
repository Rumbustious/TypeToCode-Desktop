package rumb.typetocode;

import java.util.List;
import java.util.Random;

public class TestData {
    private static final List<String> EXAMPLES = List.of(
        """
        public class HelloWorld {
            public static void main(String[] args) {
                System.out.println("Hello, World!");
            }
        }
        """,
        
        """
        public class Calculator {
            public int add(int a, int b) {
                return a + b;
            }
            
            public int subtract(int a, int b) {
                return a - b;
            }
        }
        """
        
        // Add more examples here
    );
    
    private static final Random random = new Random();
    
    public static String getRandomExample() {
        return EXAMPLES.get(random.nextInt(EXAMPLES.size()));
    }
}
