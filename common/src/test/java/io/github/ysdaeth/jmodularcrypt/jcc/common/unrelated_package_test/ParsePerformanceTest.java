package io.github.ysdaeth.jmodularcrypt.jcc.common.unrelated_package_test;

import io.github.ysdaeth.jmodularcrypt.common.parser.Section;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.function.Consumer;

public class ParsePerformanceTest {

    //@Test
    void startPerformanceCheck() throws  Exception{
        int[] sectionsCount = new int[]{100,500,1000,10_000};
        int[] sectionLength = new int[]{4,8,16,32,64,128,256,512,1024};
        Vector<PerformanceTestResult> result = new Vector<>();
        List<Thread> threads = new ArrayList<>();
        for(int sc : sectionsCount){
            for (int sl: sectionLength){
                Section[] sections = generateSections(sc,sl);
                Thread t1 = new Thread(()->{
                    result.add(performanceTest("builder",ParsePerformanceTest::composeBuild,100, sections));
                });
                Thread t2 = new Thread(()->{
                    result.add(performanceTest("join",ParsePerformanceTest::composeJoin,100, sections));
                });
                threads.add(t1);
                threads.add(t2);
            }
        }
        threads.forEach(t->{
            try{
                t.start();
                t.join();
            }catch (Exception e){}});

        result.stream().sorted(PerformanceTestResult::compareTo).forEach(
                t-> System.out.println(t.name + " rating: " + t.rating)
        );

    }

    private record PerformanceTestResult(long time, long iterations, long rating, String name) implements Comparable<PerformanceTestResult>{
        @Override
        public int compareTo(PerformanceTestResult test) {
            if (this.rating == test.rating) return 0;
            else return this.rating > test.rating ? 1 : -1;
        }
    }


    static PerformanceTestResult performanceTest(String name, Consumer<Section[]> func, int iterations, Section ...args){
        long now = System.currentTimeMillis();
        for(int i =0; i<iterations;i++){
            func.accept(args);
        }
        long end = System.currentTimeMillis();
        long time = end - now;
        long rating = (long)args.length * iterations * (long)args[0].key().length() / (time ==0 ? 1 : time );
        return new PerformanceTestResult(time,iterations,rating,name);
    }

    public static String composeBuild(Section[] sections) {
        StringBuilder stringbuilder = new StringBuilder();
        for(int i=0; i<sections.length; i++){
            Section section = sections[i];
            String key = section.key();
            String value = section.value();
            stringbuilder.append(key).append('=').append(value);
            if(i != sections.length -1) stringbuilder.append(',');
        }
        return stringbuilder.toString();
    }

    public static String composeJoin(Section[] sections) {
        List<String> pairs =new ArrayList<>(sections.length);
        for(int i=0; i<sections.length; i++) {
            String pair = sections[i].key() + '=' + sections[i].value();
            pairs.add(pair);
        }
        return String.join(",",pairs);
    }

    static Section[] generateSections(int sectionCount,int sectionTotalLength){
        Section[] sections = new Section[sectionCount];
        for(int i=0; i < sections.length; i++){
            sections[i] = generateSection(sectionTotalLength);
        }
        return sections;
    }

    static Section generateSection(int totalLength){
        String key = generateSting(totalLength/2);
        String value = generateSting(totalLength/2);
        return new Section(key,value);
    }

    static String generateSting(int length){
        char[] chars = new char[length];
        Random random = new Random();
        for(int i=0; i < chars.length; i++){
            chars[i] =(char) (random.nextInt('z' - 'a') + (int)'a');
        }
        return new String(chars);
    }
}
