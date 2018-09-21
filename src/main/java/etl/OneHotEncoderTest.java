package etl;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class OneHotEncoderTest {
    public static void main(String[] args) {
        String[]              names       = {"Sanket", "Michael", "Priyanka", "Sandeep", "Joel", "Prasad", "Blake Lively"};
        LinkedHashSet<String> uniqueNames = new LinkedHashSet<>();
        uniqueNames.addAll(Arrays.asList(names));
        OneHotEncoder encoder = new OneHotEncoder(uniqueNames);

        for (String name : names) {
            System.out.println("Name = " + name + " " + encoder.getEncodedClass(name));
        }
    }
}
