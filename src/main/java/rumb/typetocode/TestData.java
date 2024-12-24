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
        """,
        
        // Loop example
        """
        public class LoopExample {
            public static void main(String[] args) {
                for (int i = 0; i < 10; i++) {
                    System.out.println("Number: " + i);
                }
            }
        }
        """,
        
        // If-else example
        """
        public class IfElseExample {
            public static void main(String[] args) {
                int number = 10;
                if (number > 0) {
                    System.out.println("Positive number");
                } else {
                    System.out.println("Negative number");
                }
            }
        }
        """,
        
        // Function example
        """
        public class FunctionExample {
            public static void main(String[] args) {
                int result = add(5, 3);
                System.out.println("Sum: " + result);
            }
            
            public static int add(int a, int b) {
                return a + b;
            }
        }
        """,
        
        // Object-oriented example
        """
        public class Person {
            private String name;
            private int age;
            
            public Person(String name, int age) {
                this.name = name;
                this.age = age;
            }
            
            public void display() {
                System.out.println("Name: " + name + ", Age: " + age);
            }
            
            public static void main(String[] args) {
                Person person = new Person("John", 25);
                person.display();
            }
        }
        """,
        
        // IO-files example
        """
        import java.io.BufferedReader;
        import java.io.FileReader;
        import java.io.IOException;
        
        public class FileExample {
            public static void main(String[] args) {
                try (BufferedReader br = new BufferedReader(new FileReader("example.txt"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        """,
        
        // Binary IO example
        """
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.io.IOException;
        
        public class BinaryIOExample {
            public static void main(String[] args) {
                try (FileOutputStream fos = new FileOutputStream("example.bin")) {
                    fos.write(65);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                try (FileInputStream fis = new FileInputStream("example.bin")) {
                    int data = fis.read();
                    System.out.println("Data: " + data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        """,
        
        // Strings example
        """
        public class StringExample {
            public static void main(String[] args) {
                String str = "Hello, World!";
                System.out.println("Length: " + str.length());
                System.out.println("Uppercase: " + str.toUpperCase());
                System.out.println("Substring: " + str.substring(7));
            }
        }
        """,
        
        // Math functions example
        """
        public class MathExample {
            public static void main(String[] args) {
                double result = Math.sqrt(25);
                System.out.println("Square root of 25: " + result);
                
                result = Math.pow(2, 3);
                System.out.println("2 to the power of 3: " + result);
                
                result = Math.sin(Math.toRadians(90));
                System.out.println("Sine of 90 degrees: " + result);
            }
        }
        """
    );
    
    private static final Random random = new Random();
    
    public static String getRandomExample() {
        return EXAMPLES.get(random.nextInt(EXAMPLES.size()));
    }
}
